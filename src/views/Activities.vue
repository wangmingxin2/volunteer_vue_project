<template>
  <div class="activities-container">
    <el-row class="page-header">
      <el-col :span="24">
        <h2>志愿者活动列表</h2>
      </el-col>
    </el-row>

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="活动名称" prop="activityName">
          <el-input
            v-model="queryParams.activityName"
            placeholder="请输入活动名称"
            clearable
            @keyup.enter="handleQuery"
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="活动状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择活动状态"
            clearable
            style="width: 220px"
          >
            <el-option label="报名中" :value="1" />
            <el-option label="已截止" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 活动列表区域 -->
    <div class="activity-list" v-loading="loading">
      <!-- 无数据时显示空状态 -->
      <el-empty v-if="activityList.length === 0 && !loading" description="暂无活动数据" />

      <!-- 活动卡片网格 -->
      <el-row :gutter="20">
        <el-col
          v-for="activity in activityList"
          :key="activity.activityId"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          class="activity-col"
        >
          <el-card class="activity-card" shadow="hover">
            <div class="activity-image">
              <el-image
                :src="
                  activity.actibityImg ||
                  'http://60.204.157.137:9000/volunteer/activity-default.jpg'
                "
                fit="cover"
                :preview-src-list="activity.actibityImg ? [activity.actibityImg] : []"
                :initial-index="0"
              />
            </div>
            <div class="activity-info">
              <h3 class="activity-title">{{ activity.activityName }}</h3>

              <div class="activity-type">
                <el-tag size="small">{{ activity.activityType || '志愿活动' }}</el-tag>
                <el-tag size="small" :type="getStatusType(activity.status)" class="status-tag">
                  {{ getStatusText(activity.status) }}
                </el-tag>
              </div>

              <div class="activity-detail">
                <p><i class="el-icon-location"></i> 地点：{{ activity.location }}</p>
                <p>
                  <i class="el-icon-date"></i> 时间： {{ formatDate(activity.startTime) }} 至
                  {{ formatDate(activity.endTime) }}
                </p>
                <p>
                  <i class="el-icon-user"></i> 名额：{{ activity.volunteerQuota }}人 (已报名:
                  {{ activity.signedCount }}人)
                </p>
                <p class="activity-description">
                  {{
                    activity.description
                      ? activity.description.length > 50
                        ? activity.description.substring(0, 50) + '...'
                        : activity.description
                      : '暂无描述'
                  }}
                </p>
              </div>

              <div class="activity-actions">
                <el-button type="primary" @click="viewActivityDetail(activity)">查看详情</el-button>
                <el-button
                  type="success"
                  :disabled="!canSignup(activity)"
                  @click="handleSignup(activity)"
                >
                  {{ getSignupButtonText(activity) }}
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 分页 -->
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[8, 16, 24, 32]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </div>

    <!-- 活动详情对话框 -->
    <el-dialog v-model="detailVisible" title="活动详情" width="700px" destroy-on-close>
      <div class="activity-detail-dialog" v-loading="detailLoading">
        <div class="activity-header">
          <h2 class="detail-title">{{ currentActivity.activityName }}</h2>
          <el-tag :type="getStatusType(currentActivity.status)">
            {{ getStatusText(currentActivity.status) }}
          </el-tag>
        </div>

        <div class="activity-image-container" v-if="currentActivity.actibityImg">
          <el-image
            :src="currentActivity.actibityImg"
            fit="cover"
            class="detail-image"
            :preview-src-list="[currentActivity.actibityImg]"
          />
        </div>

        <div class="detail-item">
          <span class="detail-label">活动类型：</span>
          <span class="detail-value">{{ currentActivity.activityType || '志愿活动' }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">活动地点：</span>
          <span class="detail-value">{{ currentActivity.location }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">活动时间：</span>
          <span class="detail-value">
            {{ formatDate(currentActivity.startTime, 'YYYY-MM-DD HH:mm') }} 至
            {{ formatDate(currentActivity.endTime, 'YYYY-MM-DD HH:mm') }}
          </span>
        </div>

        <div class="detail-item">
          <span class="detail-label">志愿者名额：</span>
          <span class="detail-value">{{ currentActivity.volunteerQuota }}人</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">已报名人数：</span>
          <span class="detail-value">{{ currentActivity.signedCount }}人</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">联系人：</span>
          <span class="detail-value">{{ currentActivity.contactPerson || '暂无' }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">联系电话：</span>
          <span class="detail-value">{{ currentActivity.contactPhone || '暂无' }}</span>
        </div>

        <div class="detail-item description">
          <span class="detail-label">活动描述：</span>
          <div class="detail-value description-content">
            {{ currentActivity.description || '暂无活动描述' }}
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
          <el-button
            type="success"
            :disabled="!canSignup(currentActivity)"
            @click="handleSignup(currentActivity)"
          >
            {{ getSignupButtonText(currentActivity) }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getAllActivities, getActivityPage, type Activity } from '../api/activity'
import { formatDate } from '../utils/format'
import type { ApiResponse } from '../api/signup'

const router = useRouter()
const loading = ref(false)
const detailLoading = ref(false)
const detailVisible = ref(false)
const activityList = ref<Activity[]>([])
const total = ref(0)
const currentActivity = ref<Activity>({} as Activity)

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 8,
  activityName: '',
  status: '',
})

// 在页面加载时获取活动列表
onMounted(() => {
  getList()
})

// 获取活动列表数据
const getList = async () => {
  loading.value = true
  try {
    const params = {
      page: queryParams.page,
      size: queryParams.pageSize,
      activityName: queryParams.activityName || undefined,
      status: queryParams.status || undefined,
    }

    const response = (await getActivityPage(
      params.page,
      params.size,
      params.status?.toString(),
      undefined,
      params.activityName,
    )) as ApiResponse<{ records: Activity[]; total: number }>

    if (response.code === 200) {
      activityList.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取活动列表失败')
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
    ElMessage.error('获取活动列表失败')
  } finally {
    loading.value = false
  }
}

// 根据活动状态获取对应的标签类型
const getStatusType = (status: number) => {
  switch (status) {
    case 1:
      return 'success' // 报名中
    case 2:
      return 'info' // 已截止
    case 3:
      return 'danger' // 已取消
    default:
      return 'info'
  }
}

// 根据活动状态获取对应的文本
const getStatusText = (status: number) => {
  switch (status) {
    case 1:
      return '报名中'
    case 2:
      return '已截止'
    case 3:
      return '已取消'
    default:
      return '未知状态'
  }
}

// 判断是否可以报名
const canSignup = (activity: Activity) => {
  // 只有状态为1(报名中)且未满额的活动才能报名
  return activity.status === 1 && activity.signedCount < activity.volunteerQuota
}

// 获取报名按钮的文本
const getSignupButtonText = (activity: Activity) => {
  if (activity.status !== 1) {
    return '已结束'
  }
  if (activity.signedCount >= activity.volunteerQuota) {
    return '已满员'
  }
  return '立即报名'
}

// 查看活动详情
const viewActivityDetail = (activity: Activity) => {
  currentActivity.value = JSON.parse(JSON.stringify(activity))
  detailVisible.value = true
}

// 处理报名
const handleSignup = (activity: Activity) => {
  if (!canSignup(activity)) {
    ElMessage.warning(getSignupButtonText(activity))
    return
  }

  // 跳转到报名页面或打开报名对话框
  router.push({
    path: '/volunteer/activity-signup',
    query: {
      activityId: activity.activityId,
    },
  })
}

// 处理查询
const handleQuery = () => {
  queryParams.page = 1
  getList()
}

// 重置查询条件
const resetQuery = () => {
  queryParams.activityName = ''
  queryParams.status = ''
  queryParams.page = 1
  getList()
}

// 处理每页数量变化
const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  getList()
}

// 处理页码变化
const handleCurrentChange = (page: number) => {
  queryParams.page = page
  getList()
}
</script>

<style scoped>
.activities-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  padding: 10px 0;
  color: #409eff;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.activity-list {
  margin-top: 20px;
}

.activity-col {
  margin-bottom: 20px;
}

.activity-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: all 0.3s;
}

.activity-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.activity-image {
  height: 180px;
  overflow: hidden;
}

.activity-image .el-image {
  width: 100%;
  height: 100%;
}

.activity-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 12px;
}

.activity-title {
  margin: 0 0 10px 0;
  font-size: 18px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-type {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.status-tag {
  margin-left: 8px;
}

.activity-detail {
  margin-bottom: 15px;
  flex: 1;
}

.activity-detail p {
  margin: 8px 0;
  font-size: 14px;
  color: #606266;
}

.activity-description {
  color: #909399;
  margin-top: 12px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.activity-actions {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* 详情对话框样式 */
.activity-detail-dialog {
  padding: 0 20px;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.detail-title {
  margin: 0;
  font-size: 22px;
  color: #303133;
}

.activity-image-container {
  text-align: center;
  margin-bottom: 20px;
}

.detail-image {
  max-width: 100%;
  max-height: 300px;
  border-radius: 4px;
}

.detail-item {
  margin-bottom: 15px;
  display: flex;
}

.detail-label {
  width: 100px;
  color: #606266;
  font-weight: bold;
}

.detail-value {
  flex: 1;
  color: #333;
}

.description {
  align-items: flex-start;
}

.description-content {
  white-space: pre-line;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .search-form {
    display: flex;
    flex-direction: column;
  }

  .activity-actions {
    flex-direction: column;
  }

  .detail-item {
    flex-direction: column;
  }

  .detail-label {
    width: 100%;
    margin-bottom: 5px;
  }
}
</style>
