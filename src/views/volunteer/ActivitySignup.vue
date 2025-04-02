<template>
  <div class="activity-signup">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>活动报名</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="活动名称" prop="activityName">
          <el-input
            v-model="queryParams.activityName"
            placeholder="请输入活动名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="组织" prop="orgId">
          <el-input
            v-model="queryParams.orgId"
            placeholder="请输入组织ID"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 活动列表 -->
      <div v-loading="loading" class="activity-list">
        <el-empty v-if="activityList.length === 0" description="暂无活动可报名" />

        <el-card
          v-for="activity in activityList"
          :key="activity.activityId"
          class="activity-card"
          shadow="hover"
        >
          <div class="activity-card-content">
            <div class="activity-image">
              <el-image
                :src="
                  activity.actibityImg ||
                  'http://60.204.157.137:9000/volunteer/activity-default.jpg'
                "
                fit="cover"
                :preview-src-list="activity.actibityImg ? [activity.actibityImg] : []"
                :initial-index="0"
                preview-teleported
              />
            </div>
            <div class="activity-info">
              <h3 class="activity-title">{{ activity.activityName }}</h3>
              <div class="activity-meta">
                <p><i class="el-icon-location"></i> 地点：{{ activity.location }}</p>
                <p>
                  <i class="el-icon-date"></i> 时间：{{ formatDate(activity.startTime) }} 至
                  {{ formatDate(activity.endTime) }}
                </p>
                <p>
                  <i class="el-icon-user"></i> 招募：{{ activity.volunteerQuota }}人 (已报名:
                  {{ activity.signedCount }}人)
                </p>
                <p><i class="el-icon-office-building"></i> 组织ID：{{ activity.orgId }}</p>
              </div>
              <div class="activity-description">
                {{
                  activity.description
                    ? activity.description.substring(0, 100) +
                      (activity.description.length > 100 ? '...' : '')
                    : '暂无描述'
                }}
              </div>
              <div class="activity-actions">
                <el-button type="primary" @click="handleView(activity)">查看详情</el-button>
                <el-button type="success" @click="handleSignup(activity)">立即报名</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 分页区域 -->
      <el-pagination
        v-if="total > 0"
        :current-page="queryParams.page"
        :page-sizes="[5, 10, 20, 30]"
        :page-size="queryParams.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        background
        class="pagination"
      />
    </el-card>

    <!-- 活动详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="活动详情" width="800px">
      <div class="view-dialog-content" v-loading="detailLoading">
        <div class="activity-header">
          <h2>{{ viewActivity.activityName }}</h2>
          <el-tag type="success">报名中</el-tag>
        </div>
        <div class="activity-detail-info">
          <div class="info-item">
            <span class="info-label">活动类型：</span>
            <span class="info-value">{{ viewActivity.activityType || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">活动时间：</span>
            <span class="info-value"
              >{{ formatDate(viewActivity.startTime) }} 至
              {{ formatDate(viewActivity.endTime) }}</span
            >
          </div>
          <div class="info-item">
            <span class="info-label">活动地点：</span>
            <span class="info-value">{{ viewActivity.location }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">招募人数：</span>
            <span class="info-value">{{ viewActivity.volunteerQuota }}人</span>
          </div>
          <div class="info-item">
            <span class="info-label">已报名：</span>
            <span class="info-value">{{ viewActivity.signedCount }}人</span>
          </div>
          <div class="info-item">
            <span class="info-label">组织ID：</span>
            <span class="info-value">{{ viewActivity.orgId }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">联系人：</span>
            <span class="info-value">{{ viewActivity.contactPerson || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">联系电话：</span>
            <span class="info-value">{{ viewActivity.contactPhone || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">活动介绍：</span>
            <div class="info-value description">{{ viewActivity.description || '暂无描述' }}</div>
          </div>
          <div class="info-item" v-if="viewActivity.actibityImg">
            <span class="info-label">活动图片：</span>
            <div class="info-value">
              <el-image
                :src="viewActivity.actibityImg"
                class="activity-detail-image"
                :preview-src-list="[viewActivity.actibityImg]"
                :initial-index="0"
                preview-teleported
              />
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
          <el-button type="success" @click="handleSignup(viewActivity)">立即报名</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 活动报名对话框 -->
    <el-dialog v-model="signupDialogVisible" title="活动报名" width="500px">
      <h3 class="signup-activity-title">{{ currentActivity.activityName }}</h3>
      <el-form
        ref="signupFormRef"
        :model="signupForm"
        :rules="signupRules"
        label-width="100px"
        class="signup-form"
      >
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="signupForm.emergencyContact" placeholder="请输入紧急联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="emergencyPhone">
          <el-input v-model="signupForm.emergencyPhone" placeholder="请输入紧急联系电话" />
        </el-form-item>
        <div class="signup-notice">
          <p>注意事项：</p>
          <ul>
            <li>请确保提供的联系信息真实有效，以便在紧急情况下联系。</li>
            <li>活动报名成功后，请按时参加活动。</li>
            <li>如需取消报名，请提前在"我的活动"中操作。</li>
          </ul>
        </div>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelSignup">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitSignupForm">
            提交报名
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivityPage, type Activity } from '../../api/activity'
import { addSignup, type ActivitySignupData, type ApiResponse } from '../../api/signup'
import { formatDate, getCurrentBeijingTime } from '../../utils/format'
import type { FormInstance } from 'element-plus'

// 扩展Activity类型以包含orgName属性
interface ExtendedActivity extends Activity {
  orgName?: string
}

// 在组件顶部添加用户ID和用户信息相关变量
const userId = localStorage.getItem('userId') || ''

// 查询参数
const queryParams = reactive({
  activityName: '',
  orgId: '',
  status: 1, // 改为数字类型
  page: 1,
  size: 5,
})

// 活动列表数据
const activityList = ref<ExtendedActivity[]>([])
const total = ref(0)
const loading = ref(false)
const detailLoading = ref(false)

// 查看活动详情相关变量
const viewDialogVisible = ref(false)
const viewActivity = ref<ExtendedActivity>({} as ExtendedActivity)

// 报名相关变量
const signupDialogVisible = ref(false)
const signupFormRef = ref<FormInstance>()
const submitLoading = ref(false)
const currentActivity = ref<ExtendedActivity>({} as ExtendedActivity)

// 添加报名表单数据
const signupForm = reactive({
  emergencyContact: '',
  emergencyPhone: '',
})

// 添加表单验证规则
const signupRules = reactive({
  emergencyContact: [
    { required: true, message: '请输入紧急联系人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  emergencyPhone: [
    { required: true, message: '请输入紧急联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
})

// 获取活动列表
const getActivityList = async () => {
  try {
    loading.value = true
    console.log('查询参数:', queryParams)

    const res = (await getActivityPage(
      queryParams.page,
      queryParams.size,
      String(queryParams.status),
      queryParams.orgId || undefined,
      queryParams.activityName || undefined,
    )) as ApiResponse<{ records: ExtendedActivity[]; total: number }>

    console.log('活动列表响应:', res)

    if (res.code === 200) {
      activityList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取活动列表失败')
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
    ElMessage.error('获取活动列表失败')
  } finally {
    loading.value = false
  }
}

// 查询按钮点击事件
const handleQuery = () => {
  queryParams.page = 1
  getActivityList()
}

// 重置查询条件
const resetQuery = () => {
  queryParams.activityName = ''
  queryParams.orgId = ''
  queryParams.page = 1
  getActivityList()
}

// 修改分页大小
const handleSizeChange = (size: number) => {
  queryParams.size = size
  getActivityList()
}

// 修改当前页
const handleCurrentChange = (page: number) => {
  queryParams.page = page
  getActivityList()
}

// 查看活动详情
const handleView = (activity: ExtendedActivity) => {
  viewActivity.value = JSON.parse(JSON.stringify(activity))
  viewDialogVisible.value = true
}

// 修改报名活动函数
const handleSignup = (activity: ExtendedActivity) => {
  // 检查用户是否登录
  if (!userId) {
    ElMessage.warning('请先登录后再报名活动')
    return
  }

  // 检查活动是否已满
  if (activity.volunteerQuota <= activity.signedCount) {
    ElMessage.warning('该活动报名人数已满，请选择其他活动')
    return
  }

  // 保存当前活动信息
  currentActivity.value = activity

  // 显示报名表单对话框
  signupDialogVisible.value = true
}

// 修改提交报名表单函数
const submitSignupForm = async () => {
  if (!signupFormRef.value) return

  try {
    // 表单验证
    await signupFormRef.value.validate()

    // 设置提交状态
    submitLoading.value = true

    // 准备报名数据
    const signupData: ActivitySignupData = {
      activityId: currentActivity.value.activityId,
      userId: Number(userId),
      orgId: currentActivity.value.orgId,
      emergencyContact: signupForm.emergencyContact,
      emergencyPhone: signupForm.emergencyPhone,
      // 使用新的时间函数，解决时区问题
      signupTime: getCurrentBeijingTime(),
    }

    try {
      // 调用报名API
      const res = (await addSignup(signupData)) as ApiResponse<any>

      if (res.code === 200) {
        ElMessage.success(`成功报名活动：${currentActivity.value.activityName}`)
        signupDialogVisible.value = false

        // 重置表单
        signupForm.emergencyContact = ''
        signupForm.emergencyPhone = ''

        // 刷新活动列表
        getActivityList()
      } else {
        ElMessage.error(res.message || '报名失败，请稍后重试')
      }
    } catch (error: any) {
      // 捕获API返回的错误信息，特别处理已报名的情况
      if (error.message && error.message.includes('您已报名该活动')) {
        ElMessage.warning('您已报名该活动，无需重复报名')
      } else {
        ElMessage.error(error.message || '报名失败，请稍后重试')
      }
      console.error('报名提交错误:', error)
    }
  } catch (validationError) {
    console.error('表单验证失败:', validationError)
    ElMessage.error('表单验证失败，请检查输入')
  } finally {
    submitLoading.value = false
  }
}

// 添加取消报名对话框函数
const cancelSignup = () => {
  signupDialogVisible.value = false
  // 重置表单
  signupForm.emergencyContact = ''
  signupForm.emergencyPhone = ''
}

// 页面加载时获取活动列表
onMounted(() => {
  getActivityList()
})
</script>

<style scoped>
.activity-signup {
  margin: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 20px;
}

.activity-card {
  margin-bottom: 15px;
}

.activity-card-content {
  display: flex;
  gap: 20px;
}

.activity-image {
  width: 240px;
  height: 160px;
  overflow: hidden;
  border-radius: 4px;
}

.activity-image .el-image {
  width: 100%;
  height: 100%;
}

.activity-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.activity-title {
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
  font-size: 18px;
}

.activity-meta {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  margin-bottom: 10px;
}

.activity-meta p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.activity-description {
  color: #606266;
  margin-bottom: 15px;
  flex: 1;
}

.activity-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.view-dialog-content {
  padding: 0 20px;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.activity-header h2 {
  margin: 0;
}

.activity-detail-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-item {
  display: flex;
}

.info-label {
  flex: 0 0 100px;
  color: #606266;
}

.info-value {
  flex: 1;
}

.description {
  white-space: pre-line;
  line-height: 1.5;
}

.activity-detail-image {
  max-width: 100%;
  max-height: 300px;
  border-radius: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

:deep(.el-image-viewer__img) {
  max-width: 80vw;
  max-height: 80vh;
  object-fit: contain;
}

.signup-activity-title {
  text-align: center;
  margin-top: 0;
  margin-bottom: 20px;
  color: #409eff;
}

.signup-form {
  margin-bottom: 20px;
}

.signup-notice {
  background-color: #f8f8f8;
  padding: 15px;
  border-radius: 5px;
  margin-bottom: 10px;
}

.signup-notice p {
  font-weight: bold;
  margin-top: 0;
  margin-bottom: 10px;
  color: #e6a23c;
}

.signup-notice ul {
  margin: 0;
  padding-left: 20px;
}

.signup-notice li {
  margin-bottom: 5px;
  color: #606266;
  font-size: 14px;
}
</style>
