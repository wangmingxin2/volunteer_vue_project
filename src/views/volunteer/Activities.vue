<template>
  <div class="my-activities">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的活动</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="审核状态" prop="auditStatus">
          <el-select
            v-model="queryParams.auditStatus"
            placeholder="请选择审核状态"
            clearable
            style="width: 200px"
          >
            <el-option label="待审核" value="0" />
            <el-option label="已通过" value="1" />
            <el-option label="已拒绝" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getSignupList">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="signupList"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ background: '#f8f8f9', color: '#606266' }"
      >
        <el-table-column type="index" width="50" align="center" label="序号" />
        <el-table-column
          prop="activityName"
          label="活动名称"
          min-width="150"
          show-overflow-tooltip
        />
        <el-table-column prop="orgName" label="组织名称" width="120" show-overflow-tooltip />
        <el-table-column label="报名时间" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.signupTime) }}
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.auditStatus === 0" type="info">待审核</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === 2" type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="服务时长" width="100" align="center">
          <template #default="scope">
            {{ scope.row.serviceHours || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" effect="plain">
              {{ scope.row.status === 1 ? '正常' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <!-- 查看活动详情 -->
            <el-button size="small" type="primary" plain @click="viewActivityDetail(scope.row)">
              查看详情
            </el-button>

            <!-- 如果活动未取消且审核状态为已通过或待审核，且没有服务时长数据，显示取消报名按钮 -->
            <el-button
              v-if="
                scope.row.status === 1 &&
                (scope.row.auditStatus === 0 || scope.row.auditStatus === 1) &&
                (!scope.row.serviceHours || scope.row.serviceHours <= 0)
              "
              size="small"
              type="danger"
              plain
              @click="handleCancelSignup(scope.row)"
            >
              取消报名
            </el-button>

            <!-- 如果有服务时长且状态不是取消，显示心得体会按钮 -->
            <el-button
              v-if="scope.row.serviceHours && scope.row.serviceHours > 0 && scope.row.status === 1"
              size="small"
              type="success"
              plain
              @click="viewFeedback(scope.row)"
            >
              心得体会
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 活动详情对话框 -->
    <el-dialog v-model="detailVisible" title="活动详情" width="700px">
      <div v-loading="detailLoading" class="activity-detail">
        <div class="info-item">
          <span class="label">活动名称：</span>
          <span>{{ currentActivity.activityName || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">组织名称：</span>
          <span>{{ currentActivity.orgName || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">紧急联系人：</span>
          <span>{{ currentActivity.emergencyContact || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">紧急联系电话：</span>
          <span>{{ currentActivity.emergencyPhone || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">报名时间：</span>
          <span>{{ formatDate(currentActivity.signupTime, 'YYYY-MM-DD HH:mm') || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">审核状态：</span>
          <span>
            <el-tag v-if="currentActivity.auditStatus === 0" type="info">待审核</el-tag>
            <el-tag v-else-if="currentActivity.auditStatus === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="currentActivity.auditStatus === 2" type="danger">已拒绝</el-tag>
            <span v-else>-</span>
          </span>
        </div>
        <div class="info-item">
          <span class="label">审核时间：</span>
          <span>{{ formatDate(currentActivity.auditTime, 'YYYY-MM-DD HH:mm') || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">审核人：</span>
          <span>{{ currentActivity.auditBy || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">审核备注：</span>
          <span>{{ currentActivity.auditRemark || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">签到时间：</span>
          <span>{{ formatDate(currentActivity.signinTime, 'YYYY-MM-DD HH:mm') || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">签到地点：</span>
          <span>{{ currentActivity.signinLocation || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">签退时间：</span>
          <span>{{ formatDate(currentActivity.signoutTime, 'YYYY-MM-DD HH:mm') || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">服务时长：</span>
          <span>{{ currentActivity.serviceHours || '0' }} 小时</span>
        </div>
        <div class="info-item">
          <span class="label">状态：</span>
          <span>
            <el-tag :type="currentActivity.status === 1 ? 'success' : 'danger'" effect="plain">
              {{ currentActivity.status === 1 ? '正常' : '已取消' }}
            </el-tag>
          </span>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
          <el-button
            v-if="
              currentActivity.status === 1 &&
              (currentActivity.auditStatus === 0 || currentActivity.auditStatus === 1)
            "
            type="danger"
            @click="handleCancelSignup(currentActivity)"
          >
            取消报名
          </el-button>
          <el-button
            v-if="currentActivity.serviceHours > 0"
            type="primary"
            @click="viewFeedback(currentActivity)"
          >
            心得体会
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserSignups, cancelSignup } from '../../api/signup'
import { formatDate } from '../../utils/format'

// 用户ID
const userId = localStorage.getItem('userId')

// 表格数据
const signupList = ref([])
const total = ref(0)
const loading = ref(false)

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  auditStatus: '',
})

// 活动详情
const detailVisible = ref(false)
const detailLoading = ref(false)
const currentActivity = ref({
  signupId: '',
  activityId: '',
  activityName: '',
  orgId: '',
  orgName: '',
  userId: '',
  emergencyContact: '',
  emergencyPhone: '',
  signupTime: '',
  auditStatus: 0,
  auditTime: '',
  auditBy: '',
  auditRemark: '',
  signinTime: '',
  signinLocation: '',
  signoutTime: '',
  serviceHours: 0,
  status: 1,
  createdBy: '',
  createdTime: '',
  updatedBy: '',
  updatedTime: '',
})

// 获取报名列表
const getSignupList = async () => {
  if (!userId) {
    ElMessage.error('用户未登录')
    return
  }

  loading.value = true
  try {
    const res = await getUserSignups(userId, queryParams)
    if (res.code === 200) {
      signupList.value = res.data || []
      total.value = res.data.length || 0
    } else {
      ElMessage.error(res.message || '获取报名列表失败')
    }
  } catch (error) {
    console.error('获取报名列表失败:', error)
    ElMessage.error('获取报名列表失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const resetQuery = () => {
  queryParams.auditStatus = ''
  getSignupList()
}

// 处理分页大小变化
const handleSizeChange = (size: number) => {
  queryParams.size = size
  getSignupList()
}

// 处理当前页变化
const handleCurrentChange = (page: number) => {
  queryParams.page = page
  getSignupList()
}

// 查看活动详情
const viewActivityDetail = (row: any) => {
  // 复制活动信息到查看对象
  Object.assign(currentActivity.value, row)
  detailVisible.value = true
}

// 取消报名
const handleCancelSignup = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要取消参加 "${row.activityName}" 活动吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    const res = await cancelSignup(row.signupId.toString())

    if (res.code === 200) {
      ElMessage.success('已成功取消报名')
      getSignupList() // 刷新列表
    } else {
      ElMessage.error(res.message || '取消报名失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消报名失败:', error)
      ElMessage.error('取消报名失败')
    }
  }
}

// 查看心得体会
const viewFeedback = (row: any) => {
  ElMessage({
    message: '心得体会功能开发中，敬请期待！',
    type: 'info',
  })
}

onMounted(() => {
  getSignupList()
})
</script>

<style scoped>
.my-activities {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.activity-detail {
  padding: 20px;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
}

.label {
  font-weight: bold;
  width: 100px;
  flex-shrink: 0;
}

.description {
  white-space: pre-line;
  line-height: 1.5;
}

.activity-image {
  max-width: 100%;
  max-height: 300px;
  border-radius: 4px;
}
</style>
