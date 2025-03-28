<template>
  <div class="activities">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>志愿活动管理</span>
          <el-button type="primary" @click="handleAdd">新增活动</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="活动名称" prop="activityName">
          <el-input
            v-model="queryParams.activityName"
            placeholder="请输入活动名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="dict in statusOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="activityList"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ background: '#f8f8f9', color: '#606266' }"
      >
        <el-table-column type="index" width="50" align="center" label="序号" />
        <el-table-column prop="activityName" label="活动名称" show-overflow-tooltip />
        <el-table-column label="活动图片" width="100" align="center">
          <template #default="scope">
            <el-image
              v-if="scope.row.actibityImg"
              :src="scope.row.actibityImg"
              fit="cover"
              class="table-image"
              :preview-src-list="[scope.row.actibityImg]"
              :initial-index="0"
              preview-teleported
              :z-index="9999"
            />
            <el-icon v-else><Picture /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="activityType" label="活动类型" width="100" />
        <el-table-column label="开始时间" width="120" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="结束时间" width="120" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="活动地点" width="150" show-overflow-tooltip />
        <el-table-column prop="volunteerQuota" label="招募人数" width="100" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>

            <!-- 状态按钮 -->
            <el-dropdown
              @command="(command) => handleStatusChange(scope.row, command)"
              trigger="click"
            >
              <el-button size="small" type="info">
                状态<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="1" :disabled="scope.row.status === 1"
                    >设为报名中</el-dropdown-item
                  >
                  <el-dropdown-item :command="2" :disabled="scope.row.status === 2"
                    >设为已截止</el-dropdown-item
                  >
                  <el-dropdown-item :command="3" :disabled="scope.row.status === 3"
                    >设为已取消</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <el-pagination
        v-if="total > 0"
        :page-sizes="[10, 20, 30, 50]"
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
      <div class="view-dialog-content">
        <div class="activity-header">
          <h2>{{ viewActivity.activityName }}</h2>
          <el-tag :type="getStatusType(viewActivity.status)">
            {{ getStatusText(viewActivity.status) }}
          </el-tag>
        </div>
        <div class="activity-info">
          <div class="info-item">
            <span class="info-label">活动类型：</span>
            <span class="info-value">{{ viewActivity.activityType || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">活动时间：</span>
            <span class="info-value"
              >{{ viewActivity.startTime }} 至 {{ viewActivity.endTime }}</span
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
            <span class="info-label">联系人：</span>
            <span class="info-value">{{ viewActivity.contactPerson || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">联系电话：</span>
            <span class="info-value">{{ viewActivity.contactPhone || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">活动介绍：</span>
            <div class="info-value description">{{ viewActivity.description }}</div>
          </div>
          <div class="info-item" v-if="viewActivity.actibityImg">
            <span class="info-label">活动图片：</span>
            <div class="info-value">
              <img :src="viewActivity.actibityImg" class="activity-image-preview" />
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 活动表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增活动' : '编辑活动'"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="dialog-form">
        <el-form-item label="活动名称" prop="activityName">
          <el-input v-model="form.activityName" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动类型" prop="activityType">
          <el-input v-model="form.activityType" placeholder="请输入活动类型" />
        </el-form-item>
        <el-form-item label="活动图片" prop="actibityImg">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:8080/upload"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="form.actibityImg" :src="form.actibityImg" class="activity-image" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">建议上传宽高比为16:9的图片</div>
        </el-form-item>
        <el-form-item label="活动时间" prop="activityTime">
          <el-date-picker
            v-model="form.activityTime"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="招募人数" prop="volunteerQuota">
          <el-input-number v-model="form.volunteerQuota" :min="1" :max="1000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="form.contactPerson" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="活动介绍" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入活动介绍"
          />
        </el-form-item>
        <el-form-item label="活动状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">报名中</el-radio>
            <el-radio :label="2">已截止</el-radio>
            <el-radio :label="3">已取消</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormRules } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { Plus, Picture, ArrowDown } from '@element-plus/icons-vue'
import {
  getActivityPage,
  addActivity,
  updateActivity,
  deleteActivity,
  updateActivityStatus,
  getActivitySignups,
  approveSignup,
} from '../../api/activity'

// 状态选项
const statusOptions = [
  { value: '1', label: '报名中' },
  { value: '2', label: '已截止' },
  { value: '3', label: '已取消' },
]

// 查询参数
const queryParams = reactive({
  status: '',
  activityName: '',
  page: 1,
  size: 10,
})

// 活动列表数据
const activityList = ref([])
const total = ref(0)
const loading = ref(false)

// 活动查看
const viewDialogVisible = ref(false)
const viewActivity = ref({
  activityId: '',
  activityName: '',
  startTime: '',
  endTime: '',
  location: '',
  maxParticipants: 0,
  signupCount: 0,
  description: '',
  workContent: '',
  requirements: '',
  status: 0,
})

// 活动表单对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const form = reactive({
  activityId: '',
  activityName: '',
  activityType: '',
  actibityImg: '',
  activityTime: [] as any[],
  location: '',
  volunteerQuota: 10,
  contactPerson: '',
  contactPhone: '',
  description: '',
  status: 1,
  orgId: '',
})

// 表单验证规则
const rules = reactive<FormRules>({
  activityName: [
    { required: true, message: '请输入活动名称', trigger: 'blur' },
    { max: 100, message: '活动名称不能超过100个字符', trigger: 'blur' },
  ],
  activityTime: [{ required: true, message: '请选择活动时间', trigger: 'change' }],
  location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
  volunteerQuota: [{ required: true, message: '请输入招募人数', trigger: 'blur' }],
  description: [{ required: true, message: '请输入活动介绍', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
})

// 获取列表数据
const getList = async () => {
  try {
    loading.value = true

    const userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.error('获取用户ID失败')
      return
    }

    const page = queryParams.page
    const size = queryParams.size
    const status = queryParams.status || undefined
    const activityName = queryParams.activityName || undefined

    // 使用managerId而不是orgId参数，后端会根据managerId查找对应的组织活动
    const res = await getActivityPage(page, size, status, undefined, activityName, userId)

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
  getList()
}

// 重置查询条件
const resetQuery = () => {
  queryParams.activityName = ''
  queryParams.status = ''
  queryParams.page = 1
  getList()
}

// 修改分页大小
const handleSizeChange = (size: number) => {
  queryParams.size = size
  getList()
}

// 修改当前页
const handleCurrentChange = (page: number) => {
  queryParams.page = page
  getList()
}

// 获取状态类型
const getStatusType = (status: number) => {
  const types = {
    1: 'success', // 报名中
    2: 'warning', // 已截止
    3: 'danger', // 已取消
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const texts = {
    1: '报名中',
    2: '已截止',
    3: '已取消',
  }
  return texts[status] || '未知'
}

// 查看活动
const handleView = (row: any) => {
  viewActivity.value = JSON.parse(JSON.stringify(row))
  viewDialogVisible.value = true
}

// 编辑活动
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  form.activityId = row.activityId
  form.activityName = row.activityName
  form.activityType = row.activityType
  form.actibityImg = row.actibityImg
  form.activityTime = [row.startTime, row.endTime]
  form.location = row.location
  form.volunteerQuota = row.volunteerQuota
  form.contactPerson = row.contactPerson
  form.contactPhone = row.contactPhone
  form.description = row.description || ''
  form.status = row.status
  dialogVisible.value = true
}

// 删除活动
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除活动 "${row.activityName}" 吗?`, '警告', {
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
        ElMessage.error('删除活动失败')
      }
    })
    .catch(() => {
      // 取消删除
    })
}

// 处理状态变更
const handleStatusChange = async (row: any, status: number) => {
  try {
    const statusText = {
      1: '报名中',
      2: '已截止',
      3: '已取消',
    }[status]

    await ElMessageBox.confirm(
      `确认将活动"${row.activityName}"状态修改为"${statusText}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )

    const res = await updateActivityStatus(row.activityId, status)

    if (res.code === 200) {
      ElMessage.success(`活动状态已更新为"${statusText}"`)
      row.status = status
    } else {
      ElMessage.error(res.message || '状态更新失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新活动状态失败:', error)
      ElMessage.error('更新活动状态失败')
    }
  }
}

// 新增活动
const handleAdd = () => {
  dialogType.value = 'add'
  formRef.value?.resetFields()
  form.activityId = ''
  form.activityName = ''
  form.activityTime = []
  form.location = ''
  form.volunteerQuota = 10
  form.description = ''
  form.contactPerson = ''
  form.contactPhone = ''
  form.status = 1
  // 获取本地存储的orgId或从用户信息获取
  const userId = localStorage.getItem('userId')
  form.orgId = userId || ''

  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    // 设置开始和结束时间
    if (form.activityTime && form.activityTime.length === 2) {
      form.startTime = form.activityTime[0]
      form.endTime = form.activityTime[1]
    }

    // 构建提交的数据
    const formData = {
      activityId: form.activityId,
      activityName: form.activityName,
      startTime: form.startTime,
      endTime: form.endTime,
      location: form.location,
      volunteerQuota: form.volunteerQuota,
      description: form.description,
      contactPerson: form.contactPerson,
      contactPhone: form.contactPhone,
      status: form.status,
      orgId: form.orgId,
    }

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
    console.error('表单提交错误:', error)
    submitLoading.value = false
    ElMessage.error('表单验证失败，请检查输入')
  }
}

// 处理图片上传成功
const handleImageSuccess = (response: any) => {
  if (response.code === 200) {
    form.actibityImg = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

// 上传图片前的验证
const beforeAvatarUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传图片只能是 JPG/PNG/GIF 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 格式化日期，只显示年月日
const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'

  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return dateStr

  return date.toISOString().split('T')[0]
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.activities {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-form {
  max-height: 70vh;
  overflow-y: auto;
  padding: 0 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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

.activity-info {
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

.activity-image {
  width: 320px;
  height: 180px;
  object-fit: cover;
  display: block;
}

.activity-image-preview {
  max-width: 100%;
  max-height: 300px;
  object-fit: contain;
  border-radius: 4px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 320px;
  height: 180px;
  display: inline-block;
  line-height: 180px;
  text-align: center;
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 320px;
  height: 180px;
  line-height: 180px;
  text-align: center;
}

.table-image {
  width: 60px;
  height: 40px;
  border-radius: 4px;
  cursor: pointer;
}

:deep(.el-image-viewer__img) {
  max-width: 80vw;
  max-height: 80vh;
  object-fit: contain;
}
</style>
