<template>
  <div class="reflection-management">
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
          <el-form-item label="用户姓名">
            <el-input
              v-model="queryParams.userRealName"
              placeholder="请输入用户姓名"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="审核状态">
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
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>活动心得管理</span>
          <div>
            <el-button type="primary" @click="handleAdd">新增心得</el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="reflectionList"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="心得ID" prop="reflectionId" width="80" align="center" />
        <el-table-column
          label="活动名称"
          prop="activityName"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column label="用户姓名" prop="userRealName" width="100" show-overflow-tooltip />
        <el-table-column label="心得标题" prop="title" min-width="150" show-overflow-tooltip />
        <el-table-column label="提交时间" width="150" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.createdTime, 'YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="公开状态" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isPublic === 1 ? 'success' : 'info'">
              {{ scope.row.isPublic === 1 ? '公开' : '不公开' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="90" align="center">
          <template #default="scope">
            <el-tag :type="getAuditStatusType(scope.row.auditStatus)">
              {{ getAuditStatusText(scope.row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="点赞数" width="80" align="center">
          <template #default="scope">
            {{ scope.row.likeCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="心得图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.images"
              :src="row.images.split(',')[0]"
              style="width: 50px; height: 50px"
              fit="cover"
              :preview-src-list="row.images ? [row.images.split(',')[0]] : []"
              preview-teleported
              class="clickable-image"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <span v-else>无图片</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleView(scope.row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
              v-if="scope.row.auditStatus === 0"
              type="success"
              link
              @click="handleAudit(scope.row, 1)"
              >通过</el-button
            >
            <el-button
              v-if="scope.row.auditStatus === 0"
              type="danger"
              link
              @click="handleAudit(scope.row, 2)"
              >拒绝</el-button
            >
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="getList"
          @size-change="getList"
        />
      </div>
    </el-card>

    <!-- 查看详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="心得详情" width="600px">
      <div class="detail-container">
        <div class="info-item">
          <span class="label">活动名称：</span>
          <span>{{ currentReflection?.activityName }}</span>
        </div>
        <div class="info-item">
          <span class="label">用户姓名：</span>
          <span>{{ currentReflection?.userRealName }}</span>
        </div>
        <div class="info-item">
          <span class="label">心得标题：</span>
          <span>{{ currentReflection?.title }}</span>
        </div>
        <div class="info-item">
          <span class="label">提交时间：</span>
          <span>{{ formatDate(currentReflection?.createdTime, 'YYYY-MM-DD HH:mm:ss') }}</span>
        </div>
        <div class="info-item">
          <span class="label">公开状态：</span>
          <span>
            <el-tag :type="currentReflection?.isPublic === 1 ? 'success' : 'info'">
              {{ currentReflection?.isPublic === 1 ? '公开' : '不公开' }}
            </el-tag>
          </span>
        </div>
        <div class="info-item">
          <span class="label">审核状态：</span>
          <span>
            <el-tag :type="getAuditStatusType(currentReflection?.auditStatus || 0)">
              {{ getAuditStatusText(currentReflection?.auditStatus || 0) }}
            </el-tag>
          </span>
        </div>
        <div class="info-item">
          <span class="label">点赞数：</span>
          <span>{{ currentReflection?.likeCount || 0 }}</span>
        </div>
        <div class="info-item">
          <span class="label">心得内容：</span>
        </div>
        <div class="reflection-content">{{ currentReflection?.content }}</div>

        <div v-if="currentReflection?.images" class="info-item">
          <span class="label">心得图片：</span>
        </div>
        <div v-if="currentReflection?.images" class="reflection-images">
          <el-image
            v-for="(url, index) in formatImageUrls(currentReflection.images)"
            :key="index"
            :src="url"
            :preview-src-list="formatImageUrls(currentReflection.images)"
            class="reflection-image"
            fit="cover"
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button
            v-if="currentReflection && currentReflection.auditStatus === 0"
            type="success"
            @click="handleAudit(currentReflection, 1)"
            >通过审核</el-button
          >
          <el-button
            v-if="currentReflection && currentReflection.auditStatus === 0"
            type="danger"
            @click="handleAudit(currentReflection, 2)"
            >拒绝审核</el-button
          >
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 拒绝原因弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="请输入拒绝原因" width="500px">
      <el-form :model="rejectForm" :rules="rejectRules" ref="rejectFormRef" label-width="100px">
        <el-form-item label="拒绝原因" prop="reason">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            rows="3"
            placeholder="请输入拒绝原因"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitReject">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="formDialogVisible"
      :title="formTitle"
      width="650px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="form"
        :rules="formRules"
        ref="formRef"
        label-width="100px"
        class="dialog-form"
      >
        <el-form-item label="活动ID" prop="activityId">
          <el-input v-model="form.activityId" placeholder="请输入活动ID" clearable />
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>
        <el-form-item label="心得标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入心得标题" clearable />
        </el-form-item>
        <el-form-item label="心得内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入心得内容" />
        </el-form-item>
        <el-form-item label="心得图片">
          <el-upload
            ref="uploadRef"
            list-type="picture-card"
            :file-list="fileList"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove"
            :on-success="handleUploadSuccess"
            :limit="1"
            :on-exceed="handleExceed"
            action="http://localhost:8080/upload"
          >
            <el-icon v-if="fileList.length < 1"><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">只能上传一张JPG/PNG图片，且不超过5MB</div>
            </template>
          </el-upload>
          <el-dialog v-model="dialogImageVisible" title="预览图片">
            <img w-full :src="dialogImageUrl" alt="Preview Image" style="max-width: 100%" />
          </el-dialog>
        </el-form-item>
        <el-form-item label="公开状态" prop="isPublic">
          <el-radio-group v-model="form.isPublic">
            <el-radio :label="1">公开</el-radio>
            <el-radio :label="0">不公开</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核状态" prop="auditStatus">
          <el-radio-group v-model="form.auditStatus">
            <el-radio :label="0">待审核</el-radio>
            <el-radio :label="1">已通过</el-radio>
            <el-radio :label="2">已拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="formDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { formatDate } from '@/utils/format'
import request from '@/api/request'
import { Plus, Picture } from '@element-plus/icons-vue'

// API
function getReflectionPage(params: any) {
  return request({
    url: '/reflection/page',
    method: 'get',
    params,
  })
}

function auditReflection(reflectionId: number | string, auditStatus: number, auditRemark?: string) {
  return request({
    url: `/reflection/audit/${reflectionId}`,
    method: 'put',
    params: { auditStatus, auditRemark: auditRemark || undefined },
  })
}

function deleteReflection(reflectionId: number) {
  return request({
    url: `/reflection/${reflectionId}`,
    method: 'delete',
  })
}

function addReflection(data: any) {
  return request({
    url: '/reflection',
    method: 'post',
    data,
  })
}

function updateReflection(data: any) {
  return request({
    url: `/reflection/${data.reflectionId}`,
    method: 'put',
    data,
  })
}

// 上传图片
function uploadImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)

  return request({
    url: '/common/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

// 状态和数据
const loading = ref(false)
const total = ref(0)
const reflectionList = ref<any[]>([])
const selectedIds = ref<number[]>([])
const detailDialogVisible = ref(false)
const rejectDialogVisible = ref(false)
const formDialogVisible = ref(false)
const formType = ref<'add' | 'edit'>('add')
const currentReflection = ref<any>(null)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const rejectFormRef = ref<FormInstance>()
const fileList = ref<any[]>([])
const formTitle = ref('新增心得')
const uploadRef = ref<any>(null)
const dialogImageVisible = ref(false)
const dialogImageUrl = ref('')

// 查询参数
const queryParams = reactive({
  activityName: '',
  userRealName: '',
  auditStatus: '',
  isPublic: '1', // 默认查询公开的
  page: 1,
  size: 10,
})

// 拒绝表单
const rejectForm = reactive({
  reflectionId: 0,
  reason: '',
})

// 拒绝表单验证规则
const rejectRules = {
  reason: [{ required: true, message: '请输入拒绝原因', trigger: 'blur' }],
}

// 新增/编辑表单
const form = reactive({
  reflectionId: 0,
  userId: '',
  activityId: '',
  title: '',
  content: '',
  images: '',
  isPublic: 1,
  auditStatus: 0,
})

// 表单验证规则
const formRules = {
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  activityId: [{ required: true, message: '请输入活动ID', trigger: 'blur' }],
  title: [{ required: true, message: '请输入心得标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入心得内容', trigger: 'blur' }],
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await getReflectionPage(queryParams)
    if (res.code === 200) {
      reflectionList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取心得列表失败')
    }
  } catch (error) {
    console.error('获取心得列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询按钮
const handleQuery = () => {
  queryParams.page = 1
  getList()
}

// 重置查询条件
const resetQuery = () => {
  queryParams.activityName = ''
  queryParams.userRealName = ''
  queryParams.auditStatus = ''
  queryParams.page = 1
  getList()
}

// 查看详情
const handleView = (row: any) => {
  currentReflection.value = row
  detailDialogVisible.value = true
}

// 审核操作
const handleAudit = (row: any, status: number) => {
  if (status === 2) {
    // 拒绝需要填写理由
    currentReflection.value = row
    rejectForm.reflectionId = row.reflectionId
    rejectForm.reason = ''
    rejectDialogVisible.value = true
  } else {
    // 通过不需要理由，直接调用审核接口
    ElMessageBox.confirm(`确定要${status === 1 ? '通过' : '拒绝'}该心得吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      try {
        loading.value = true
        const res = await auditReflection(row.reflectionId, status)
        if (res.code === 200) {
          ElMessage.success(status === 1 ? '审核通过成功' : '审核拒绝成功')
          getList()
        } else {
          ElMessage.error(res.message || '审核失败')
        }
      } catch (error) {
        console.error('审核失败:', error)
        ElMessage.error('审核失败')
      } finally {
        loading.value = false
      }
    })
  }
}

// 提交拒绝
const submitReject = async () => {
  if (!rejectFormRef.value) return

  try {
    await rejectFormRef.value.validate()
    submitLoading.value = true

    try {
      const res = await auditReflection(rejectForm.reflectionId, 2, rejectForm.reason)
      if (res.code === 200) {
        ElMessage.success('审核拒绝成功')
        rejectDialogVisible.value = false
        getList()
        detailDialogVisible.value = false
      } else {
        ElMessage.error(res.message || '审核拒绝失败')
      }
    } catch (error) {
      console.error('审核拒绝失败:', error)
      ElMessage.error('审核拒绝失败')
    } finally {
      submitLoading.value = false
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 删除心得
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除这条心得吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const res = await deleteReflection(row.reflectionId)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          getList()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

// 多选框变化
const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map((item) => item.reflectionId)
}

// 获取审核状态文本
const getAuditStatusText = (status: number) => {
  switch (status) {
    case 0:
      return '待审核'
    case 1:
      return '已通过'
    case 2:
      return '已拒绝'
    default:
      return '未知'
  }
}

// 获取审核状态标签类型
const getAuditStatusType = (status: number) => {
  switch (status) {
    case 0:
      return 'info'
    case 1:
      return 'success'
    case 2:
      return 'danger'
    default:
      return 'info'
  }
}

// 格式化图片URL数组
const formatImageUrls = (imagesStr: string) => {
  if (!imagesStr) return []
  return imagesStr.split(',').filter((url) => url.trim() !== '')
}

// 新增心得
const handleAdd = () => {
  formType.value = 'add'
  formTitle.value = '新增心得'
  resetForm()
  formDialogVisible.value = true
}

// 编辑心得
const handleEdit = (row: any) => {
  formType.value = 'edit'
  formTitle.value = '编辑心得'
  resetForm()

  // 填充表单数据
  form.reflectionId = row.reflectionId
  form.userId = row.userId
  form.activityId = row.activityId
  form.title = row.title
  form.content = row.content
  form.isPublic = row.isPublic
  form.auditStatus = row.auditStatus

  // 设置images，确保有值
  form.images = row.images || ''

  // 处理已有图片，限制为一张
  fileList.value = []
  if (row.images) {
    const imageArray = row.images.split(',').filter((url: string) => url.trim() !== '')
    if (imageArray.length > 0) {
      // 只取第一张图片
      fileList.value = [
        {
          name: '已有图片',
          url: imageArray[0],
          uid: 'existing-1',
          status: 'success',
        },
      ]
    }
  }

  formDialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  form.reflectionId = 0
  form.userId = ''
  form.activityId = ''
  form.title = ''
  form.content = ''
  form.images = ''
  form.isPublic = 1
  form.auditStatus = 0
  fileList.value = []
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 处理文件改变
const handleFileChange = (file: any) => {
  const isJPG = file.raw.type === 'image/jpeg'
  const isPNG = file.raw.type === 'image/png'
  const isLt2M = file.raw.size / 1024 / 1024 < 2

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

// 处理文件移除
const handleFileRemove = (file: any) => {
  const index = fileList.value.indexOf(file)
  if (index !== -1) {
    fileList.value.splice(index, 1)
  }
}

// 处理超出上传数量限制
const handleExceed = () => {
  ElMessage.warning('最多只能上传1张图片')
}

// 图片上传成功处理
const handleUploadSuccess = (response: any, uploadFile: any) => {
  // 确保只有一张图片，如果已有图片就替换
  fileList.value = [
    {
      name: uploadFile.name,
      url: response.data,
      uid: uploadFile.uid,
      status: 'success',
    },
  ]
}

// 处理编辑时的图片加载
const handlePictureCardPreview = (file: any) => {
  dialogImageUrl.value = file.url
  dialogImageVisible.value = true
}

// 处理编辑时的图片移除
const handleRemove = (file: any) => {
  const index = fileList.value.indexOf(file)
  if (index !== -1) {
    fileList.value.splice(index, 1)
  }
}

// 提交表单
const submitForm = async () => {
  try {
    await formRef.value.validate()

    submitLoading.value = true

    // 将fileList转为images字符串
    form.images = fileList.value.length > 0 ? fileList.value[0].url : ''

    try {
      const res =
        formType.value === 'add' ? await addReflection(form) : await updateReflection(form)

      if (res.code === 200) {
        ElMessage.success(formType.value === 'add' ? '新增成功' : '更新成功')
        formDialogVisible.value = false
        getList()
      } else {
        ElMessage.error(res.message || (formType.value === 'add' ? '新增失败' : '更新失败'))
      }
    } catch (error) {
      console.error(formType.value === 'add' ? '新增失败:' : '更新失败:', error)
      ElMessage.error(formType.value === 'add' ? '新增失败' : '更新失败')
    } finally {
      submitLoading.value = false
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.reflection-management {
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-container {
  max-height: 60vh;
  overflow-y: auto;
  padding: 10px;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
}

.label {
  font-weight: bold;
  width: 120px;
  flex-shrink: 0;
}

.reflection-content {
  white-space: pre-wrap;
  line-height: 1.6;
  margin-bottom: 20px;
  background-color: #f8f8f8;
  padding: 15px;
  border-radius: 4px;
}

.reflection-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.reflection-image {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  transition: transform 0.3s;
}

.reflection-image:hover {
  transform: scale(1.05);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-form {
  max-height: 60vh;
  overflow-y: auto;
}

.el-upload__tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.clickable-image {
  cursor: pointer;
  transition: transform 0.2s;
}

.clickable-image:hover {
  transform: scale(1.1);
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
</style>
