<template>
  <div class="admin-activities">
    <el-card class="filter-card">
      <div class="filter-container">
        <el-form :inline="true" :model="queryParams" class="filter-form">
          <el-form-item label="活动名称">
            <el-input
              v-model="queryParams.activityName"
              placeholder="请输入活动名称"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="组织ID">
            <el-input
              v-model="queryParams.orgId"
              placeholder="请输入组织ID"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="queryParams.status"
              placeholder="请选择状态"
              clearable
              style="width: 180px"
            >
              <el-option label="报名中" value="1" />
              <el-option label="已截止" value="2" />
              <el-option label="已取消" value="3" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>活动列表</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd">新增活动</el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="activityList"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" prop="activityId" width="80" align="center" />
        <el-table-column label="活动图片" width="120" align="center">
          <template #default="{ row }">
            <el-image
              style="width: 100px; height: 50px"
              :src="row.actibityImg || defaultImage"
              fit="cover"
              @click="previewImage(row.actibityImg)"
              class="clickable-image"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column
          label="活动名称"
          prop="activityName"
          min-width="150"
          show-overflow-tooltip
        />
        <el-table-column label="组织ID" prop="orgId" width="80" align="center" />
        <el-table-column label="活动类型" prop="activityType" width="100" align="center" />
        <el-table-column label="活动地点" prop="location" min-width="120" show-overflow-tooltip />
        <el-table-column label="开始时间" min-width="120">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="结束时间" min-width="120">
          <template #default="{ row }">
            {{ formatDate(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="志愿者名额" prop="volunteerQuota" width="100" align="center" />
        <el-table-column label="已报名人数" prop="signedCount" width="100" align="center" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">报名中</el-tag>
            <el-tag v-else-if="row.status === 2" type="info">已截止</el-tag>
            <el-tag v-else-if="row.status === 3" type="danger">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.status !== 1"
              type="success"
              link
              @click="handleStatusChange(row, 1)"
            >
              开放报名
            </el-button>
            <el-button v-if="row.status !== 2" type="info" link @click="handleStatusChange(row, 2)">
              截止报名
            </el-button>
            <el-button
              v-if="row.status !== 3"
              type="danger"
              link
              @click="handleStatusChange(row, 3)"
            >
              取消活动
            </el-button>
            <el-divider direction="vertical" />
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handlePreview(row)">预览</el-button>
            <el-button type="primary" link @click="handleSignups(row)">报名情况</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增活动' : '编辑活动'"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="activityFormRef"
        :model="activityForm"
        :rules="rules"
        label-width="100px"
        class="activity-form"
      >
        <el-form-item label="活动名称" prop="activityName">
          <el-input v-model="activityForm.activityName" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="组织ID" prop="orgId">
          <el-input v-model="activityForm.orgId" placeholder="请输入组织ID" />
        </el-form-item>
        <el-form-item label="活动类型" prop="activityType">
          <el-input v-model="activityForm.activityType" placeholder="请输入活动类型" />
        </el-form-item>
        <el-form-item label="活动图片" prop="actibityImg">
          <el-upload
            class="activity-uploader"
            action="http://localhost:8080/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
          >
            <img
              v-if="activityForm.actibityImg"
              :src="activityForm.actibityImg"
              class="activity-image"
            />
            <el-icon v-else class="activity-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="activity-tip">建议尺寸: 800x400px，大小不超过2MB</div>
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input
            v-model="activityForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入活动描述"
          />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="activityForm.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="activityForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="activityForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="志愿者名额" prop="volunteerQuota">
              <el-input-number
                v-model="activityForm.volunteerQuota"
                :min="1"
                :max="1000"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="已报名人数" prop="signedCount">
              <el-input-number
                v-model="activityForm.signedCount"
                :min="0"
                :max="1000"
                :disabled="dialogType === 'add'"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="activityForm.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="activityForm.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="activityForm.status">
            <el-radio :label="1">报名中</el-radio>
            <el-radio :label="2">已截止</el-radio>
            <el-radio :label="3">已取消</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 活动预览 -->
    <el-dialog v-model="previewVisible" title="活动预览" width="800px">
      <div class="preview-container">
        <h2 class="preview-title">{{ previewData.activityName }}</h2>
        <div class="preview-meta">
          <span>组织ID: {{ previewData.orgId }}</span>
          <span>活动类型: {{ previewData.activityType }}</span>
          <span>状态: {{ getStatusText(previewData.status) }}</span>
        </div>
        <div class="preview-image">
          <el-image :src="previewData.actibityImg || defaultImage" fit="cover">
            <template #error>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
        <div class="preview-info">
          <p><strong>活动地点:</strong> {{ previewData.location }}</p>
          <p>
            <strong>活动时间:</strong> {{ formatDateTime(previewData.startTime) }} 至
            {{ formatDateTime(previewData.endTime) }}
          </p>
          <p><strong>志愿者名额:</strong> {{ previewData.volunteerQuota }} 人</p>
          <p><strong>已报名人数:</strong> {{ previewData.signedCount }} 人</p>
          <p><strong>联系人:</strong> {{ previewData.contactPerson }}</p>
          <p><strong>联系电话:</strong> {{ previewData.contactPhone }}</p>
        </div>
        <div class="preview-description">
          <h3>活动描述</h3>
          <p>{{ previewData.description }}</p>
        </div>
      </div>
    </el-dialog>

    <!-- 图片预览 -->
    <el-dialog v-model="imagePreviewVisible" title="图片预览" width="800px">
      <div class="image-preview-container">
        <img :src="previewUrl" class="image-preview" />
      </div>
    </el-dialog>

    <!-- 报名情况对话框 -->
    <el-dialog v-model="signupsDialogVisible" title="活动报名情况" width="800px">
      <el-table v-loading="signupsLoading" :data="signupsList" border style="width: 100%">
        <el-table-column label="志愿者ID" prop="userId" width="100" align="center" />
        <el-table-column label="志愿者姓名" prop="realName" min-width="120" />
        <el-table-column label="联系电话" prop="phone" min-width="150" />
        <el-table-column label="报名时间" prop="signupTime" min-width="180" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">已报名</el-tag>
            <el-tag v-else-if="row.status === 2" type="info">已取消</el-tag>
            <el-tag v-else-if="row.status === 3" type="warning">已签到</el-tag>
            <el-tag v-else-if="row.status === 4" type="danger">已完成</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="signupsQuery.page"
          v-model:page-size="signupsQuery.size"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="signupsTotal"
          @size-change="handleSignupsSizeChange"
          @current-change="handleSignupsCurrentChange"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, UploadProps } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Picture } from '@element-plus/icons-vue'
import {
  getActivityPage,
  addActivity,
  updateActivity,
  deleteActivity,
  updateActivityStatus,
  getActivitySignups,
} from '../../api/activity'

// 查询参数
const queryParams = ref({
  activityName: '',
  orgId: '',
  status: '',
  page: 1,
  size: 10,
})

// 表格数据
const activityList = ref([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref<number[]>([])
const defaultImage = 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png'

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const activityFormRef = ref<FormInstance>()
const submitLoading = ref(false)

// 预览相关
const previewVisible = ref(false)
const previewData = ref<any>({})
const imagePreviewVisible = ref(false)
const previewUrl = ref('')

// 表单数据
const activityForm = reactive({
  activityId: undefined as number | undefined,
  orgId: undefined as number | undefined,
  activityName: '',
  activityType: '',
  actibityImg: '',
  description: '',
  location: '',
  startTime: '',
  endTime: '',
  volunteerQuota: 1,
  signedCount: 0,
  contactPerson: '',
  contactPhone: '',
  status: 1,
})

// 表单验证规则
const rules = {
  activityName: [
    { required: true, message: '请输入活动名称', trigger: 'blur' },
    { max: 100, message: '活动名称长度不能超过100个字符', trigger: 'blur' },
  ],
  orgId: [{ required: true, message: '请输入组织ID', trigger: 'blur' }],
  activityType: [{ required: true, message: '请输入活动类型', trigger: 'blur' }],
  actibityImg: [{ required: true, message: '请上传活动图片', trigger: 'change' }],
  description: [{ required: true, message: '请输入活动描述', trigger: 'blur' }],
  location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  volunteerQuota: [{ required: true, message: '请输入志愿者名额', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  status: [{ required: true, message: '请选择活动状态', trigger: 'change' }],
}

// 上传相关
const uploadHeaders = {
  satoken: localStorage.getItem('satoken') || '',
}

// 报名情况相关
const signupsDialogVisible = ref(false)
const signupsLoading = ref(false)
const signupsList = ref([])
const signupsTotal = ref(0)
const currentActivityId = ref('')
const signupsQuery = ref({
  page: 1,
  size: 10,
})

// 获取活动列表
const getList = async () => {
  try {
    loading.value = true

    // 使用新的参数结构调用 getActivityPage
    const page = queryParams.value.page || 1
    const size = queryParams.value.size || 10
    const status = queryParams.value.status ? parseInt(queryParams.value.status) : undefined
    const orgId = queryParams.value.orgId ? parseInt(queryParams.value.orgId) : undefined
    const activityName = queryParams.value.activityName || undefined

    const res = await getActivityPage(page, size, status, orgId, activityName)

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
  queryParams.value.page = 1
  getList()
}

// 重置查询条件
const resetQuery = () => {
  queryParams.value.activityName = ''
  queryParams.value.orgId = ''
  queryParams.value.status = ''
  queryParams.value.page = 1
  getList()
}

// 表格选择项变化
const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map((item) => item.activityId)
}

// 修改分页大小
const handleSizeChange = (size: number) => {
  queryParams.value.size = size
  getList()
}

// 修改当前页
const handleCurrentChange = (page: number) => {
  queryParams.value.page = page
  getList()
}

// 图片上传前的校验
const beforeImageUpload: UploadProps['beforeUpload'] = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('上传图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 图片上传成功回调
const handleImageSuccess = (res: any) => {
  if (res.code === 200) {
    activityForm.actibityImg = res.data
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

// 预览图片
const previewImage = (url: string) => {
  if (!url) return
  previewUrl.value = url
  imagePreviewVisible.value = true
}

// 预览活动
const handlePreview = (row: any) => {
  previewData.value = { ...row }
  previewVisible.value = true
}

// 获取状态文本
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    1: '报名中',
    2: '已截止',
    3: '已取消',
  }
  return statusMap[status] || '未知状态'
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(
    date.getDate(),
  ).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(
    date.getMinutes(),
  ).padStart(2, '0')}`
}

// 新增活动
const handleAdd = () => {
  dialogType.value = 'add'
  activityForm.activityId = undefined
  activityForm.orgId = undefined
  activityForm.activityName = ''
  activityForm.activityType = ''
  activityForm.actibityImg = ''
  activityForm.description = ''
  activityForm.location = ''
  activityForm.startTime = ''
  activityForm.endTime = ''
  activityForm.volunteerQuota = 1
  activityForm.signedCount = 0
  activityForm.contactPerson = ''
  activityForm.contactPhone = ''
  activityForm.status = 1
  dialogVisible.value = true
}

// 编辑活动
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  activityForm.activityId = row.activityId
  activityForm.orgId = row.orgId
  activityForm.activityName = row.activityName
  activityForm.activityType = row.activityType
  activityForm.actibityImg = row.actibityImg
  activityForm.description = row.description
  activityForm.location = row.location
  activityForm.startTime = row.startTime
  activityForm.endTime = row.endTime
  activityForm.volunteerQuota = row.volunteerQuota
  activityForm.signedCount = row.signedCount
  activityForm.contactPerson = row.contactPerson
  activityForm.contactPhone = row.contactPhone
  activityForm.status = row.status
  dialogVisible.value = true
}

// 删除活动
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除活动 ${row.activityName} 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const res = await deleteActivity(row.activityId.toString())
        if (res.code === 200) {
          ElMessage.success('删除成功')
          getList()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        console.error('删除活动失败:', error)
        ElMessage.error('删除失败，请稍后重试')
      }
    })
    .catch(() => {
      // 取消删除
    })
}

// 修改活动状态
const handleStatusChange = async (row: any, newStatus: number) => {
  try {
    let statusText = '开放报名'
    if (newStatus === 2) {
      statusText = '截止报名'
    } else if (newStatus === 3) {
      statusText = '取消活动'
    }

    const res = await updateActivityStatus(row.activityId, newStatus)

    if (res.code === 200) {
      ElMessage.success(`活动${statusText}成功`)
      row.status = newStatus
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('修改活动状态失败:', error)
    ElMessage.error('修改状态失败，请稍后重试')
  }
}

// 提交表单
const submitForm = async () => {
  if (!activityFormRef.value) return

  try {
    await activityFormRef.value.validate()
    submitLoading.value = true

    const formData = { ...activityForm }

    if (dialogType.value === 'add') {
      // 新增活动
      const res = await addActivity(formData)
      if (res.code === 200) {
        ElMessage.success('新增活动成功')
        dialogVisible.value = false
        getList()
      } else {
        ElMessage.error(res.message || '新增活动失败')
      }
    } else {
      // 编辑活动
      const res = await updateActivity(formData)
      if (res.code === 200) {
        ElMessage.success('编辑活动成功')
        dialogVisible.value = false
        getList()
      } else {
        ElMessage.error(res.message || '编辑活动失败')
      }
    }
    submitLoading.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
    submitLoading.value = false
  }
}

// 查看报名情况
const handleSignups = async (row: any) => {
  currentActivityId.value = row.activityId
  signupsDialogVisible.value = true
  signupsQuery.value.page = 1
  fetchSignups()
}

// 获取报名列表
const fetchSignups = async () => {
  if (!currentActivityId.value) return

  try {
    signupsLoading.value = true
    // 这里需要添加一个获取活动报名情况的API
    const res = await getActivitySignups(
      currentActivityId.value,
      signupsQuery.value.page,
      signupsQuery.value.size,
    )

    if (res.code === 200) {
      signupsList.value = res.data.records || []
      signupsTotal.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取报名情况失败')
    }
  } catch (error) {
    console.error('获取报名情况失败:', error)
    ElMessage.error('获取报名情况失败')
  } finally {
    signupsLoading.value = false
  }
}

// 报名列表分页
const handleSignupsSizeChange = (size: number) => {
  signupsQuery.value.size = size
  fetchSignups()
}

const handleSignupsCurrentChange = (page: number) => {
  signupsQuery.value.page = page
  fetchSignups()
}

// 格式化日期（只显示日期部分）
const formatDate = (dateTime: string) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(
    date.getDate(),
  ).padStart(2, '0')}`
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.admin-activities {
  padding: 10px;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.activity-uploader {
  width: 360px;
  height: 180px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.activity-uploader:hover {
  border-color: #409eff;
}

.activity-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 360px;
  height: 180px;
  line-height: 180px;
  text-align: center;
}

.activity-image {
  width: 360px;
  height: 180px;
  display: block;
  object-fit: cover;
}

.activity-tip {
  font-size: 12px;
  color: #606266;
  margin-top: 5px;
}

.clickable-image {
  cursor: pointer;
  transition: transform 0.3s;
}

.clickable-image:hover {
  transform: scale(1.1);
}

.image-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-preview {
  max-width: 100%;
  max-height: 600px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}

.preview-container {
  padding: 20px;
}

.preview-title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.preview-meta {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
  color: #666;
}

.preview-image {
  text-align: center;
  margin-bottom: 20px;
}

.preview-image img {
  max-width: 100%;
  max-height: 400px;
}

.preview-info {
  margin-bottom: 20px;
  line-height: 1.8;
}

.preview-description {
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.preview-description h3 {
  margin-bottom: 10px;
}

.preview-description p {
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
