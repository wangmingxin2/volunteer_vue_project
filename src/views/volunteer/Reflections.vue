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
          <span>我的心得体会</span>
        </div>
      </template>

      <el-table v-loading="loading" :data="reflectionList" border>
        <el-table-column label="心得ID" prop="reflectionId" width="80" align="center" />
        <el-table-column
          label="活动名称"
          prop="activityName"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column label="心得标题" prop="title" min-width="150" show-overflow-tooltip />
        <el-table-column label="提交时间" width="150" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.createdTime, 'YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.auditStatus === 0" type="info">待审核</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === 2" type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="心得图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.images && row.images.trim() !== ''"
              :src="row.images"
              style="width: 50px; height: 50px"
              fit="cover"
              :preview-src-list="[row.images]"
              preview-teleported
              class="clickable-image"
              @click="handleImagePreview(row.images)"
            />
            <span v-else>无图片</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button type="primary" link size="small" icon="View" @click="handleView(scope.row)"
              >查看</el-button
            >
            <el-button
              v-if="scope.row.auditStatus === 0"
              type="primary"
              link
              size="small"
              icon="Edit"
              @click="handleEdit(scope.row)"
              >编辑</el-button
            >
            <el-button
              type="primary"
              link
              size="small"
              icon="Delete"
              @click="handleDelete(scope.row)"
              >删除</el-button
            >
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

    <!-- 查看心得对话框 -->
    <el-dialog v-model="viewDialogVisible" title="查看心得" width="700px">
      <div class="detail-container">
        <div class="info-item">
          <span class="label">活动名称：</span>
          <span>{{ currentReflection.activityName }}</span>
        </div>
        <div class="info-item">
          <span class="label">心得标题：</span>
          <span>{{ currentReflection.title }}</span>
        </div>
        <div class="info-item">
          <span class="label">提交时间：</span>
          <span>{{ formatDate(currentReflection.createdTime, 'YYYY-MM-DD HH:mm:ss') }}</span>
        </div>
        <div class="info-item">
          <span class="label">审核状态：</span>
          <span>
            <el-tag v-if="currentReflection.auditStatus === 0" type="info">待审核</el-tag>
            <el-tag v-else-if="currentReflection.auditStatus === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="currentReflection.auditStatus === 2" type="danger">已拒绝</el-tag>
          </span>
        </div>
        <div class="info-item" v-if="currentReflection.auditStatus === 2">
          <span class="label">拒绝原因：</span>
          <span>{{ currentReflection.auditRemark || '无' }}</span>
        </div>
        <div class="info-item">
          <span class="label">心得内容：</span>
        </div>
        <div class="reflection-content">{{ currentReflection.content }}</div>
        <div
          class="info-item"
          v-if="currentReflection.images && currentReflection.images.trim() !== ''"
        >
          <span class="label">心得图片：</span>
        </div>
        <div
          class="reflection-images"
          v-if="currentReflection.images && currentReflection.images.trim() !== ''"
        >
          <el-image
            :src="currentReflection.images"
            :preview-src-list="[currentReflection.images]"
            class="reflection-image"
            fit="cover"
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑/新增心得对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="rules"
        label-width="100px"
        class="dialog-form"
      >
        <el-form-item label="活动ID" prop="activityId">
          <el-input v-model="editForm.activityId" placeholder="请输入活动ID" />
        </el-form-item>
        <el-form-item label="心得标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入心得标题" />
        </el-form-item>
        <el-form-item label="心得内容" prop="content">
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入心得内容"
          />
        </el-form-item>
        <el-form-item label="心得图片">
          <div class="image-upload-container">
            <div v-if="editForm.images && editForm.images.trim() !== ''" class="image-preview">
              <el-image
                :src="editForm.images"
                style="width: 100px; height: 100px; margin-right: 10px"
                fit="cover"
                :preview-src-list="[editForm.images]"
              />
              <el-button type="danger" size="small" @click="editForm.images = ''"
                >删除图片</el-button
              >
            </div>
            <div v-else class="upload-area">
              <el-upload
                ref="uploadRef"
                action="#"
                :auto-upload="false"
                :show-file-list="false"
                :multiple="false"
                :on-change="handleFileChange"
                accept="image/jpeg,image/png"
                class="image-uploader"
              >
                <el-button type="primary">
                  <el-icon><Plus /></el-icon>
                  上传图片
                </el-button>
                <div class="el-upload__tip">仅支持jpg、png格式，单张不超过2MB</div>
              </el-upload>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 图片预览 -->
    <el-dialog v-model="previewVisible" width="800px">
      <img :src="previewImage" alt="Preview Image" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { View, Edit, Delete, Plus } from '@element-plus/icons-vue'
import { formatDate } from '../../utils/format'
import {
  getReflectionPage,
  addReflection,
  updateReflection,
  deleteReflection,
  uploadImage,
  type ReflectionItem,
  type ReflectionData,
} from '../../api/reflection'

// 状态和数据
const loading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const reflectionList = ref<ReflectionItem[]>([])
const viewDialogVisible = ref(false)
const editDialogVisible = ref(false)
const previewVisible = ref(false)
const previewImage = ref('')
const currentReflection = ref<ReflectionItem>({} as ReflectionItem)
const editFormRef = ref<FormInstance>()

// 从本地存储获取用户ID
const userId = localStorage.getItem('userId') || ''

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  activityName: '',
  auditStatus: '',
})

// 编辑表单
const editForm = reactive({
  reflectionId: undefined as number | undefined,
  activityId: undefined as number | undefined,
  title: '',
  content: '',
  images: '',
  userId: userId,
})

// 表单验证规则
const rules = {
  activityId: [{ required: true, message: '请选择活动', trigger: 'change' }],
  title: [{ required: true, message: '请输入心得标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入心得内容', trigger: 'blur' }],
}

// 计算属性
const dialogTitle = computed(() => {
  return '编辑心得'
})

// 方法
const handleQuery = () => {
  getList()
}

const resetQuery = () => {
  queryParams.activityName = ''
  queryParams.auditStatus = ''
  queryParams.page = 1
  getList()
}

const handleView = (row: any) => {
  currentReflection.value = row
  viewDialogVisible.value = true
}

const handleEdit = (row: any) => {
  editForm.reflectionId = row.reflectionId
  editForm.activityId = row.activityId
  editForm.title = row.title
  editForm.content = row.content
  editForm.images = row.images || ''

  editDialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该心得吗？', '警告', {
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
        console.error('删除心得失败:', error)
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {
      // 取消删除
    })
}

const handleImagePreview = (imageUrl: string) => {
  if (imageUrl && imageUrl.trim() !== '') {
    previewImage.value = imageUrl
    previewVisible.value = true
  }
}

const submitForm = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
    submitLoading.value = true

    // 准备提交数据
    const formData: ReflectionData = {
      activityId: editForm.activityId!,
      title: editForm.title,
      content: editForm.content,
      userId: userId,
      images: editForm.images.trim(),
      reflectionId: editForm.reflectionId,
    }

    try {
      // 只有更新操作，没有新增
      const res = await updateReflection(formData)

      if (res.code === 200) {
        ElMessage.success('编辑成功')
        editDialogVisible.value = false
        getList()
      } else {
        ElMessage.error(res.message || '编辑失败')
      }
    } catch (error) {
      console.error('编辑失败:', error)
      ElMessage.error('编辑失败')
    } finally {
      submitLoading.value = false
    }
  } catch (error) {
    console.error('表单验证失败:', error)
    submitLoading.value = false
  }
}

const getList = async () => {
  loading.value = true
  try {
    // 明确将所有查询参数放在params中
    const params = {
      page: queryParams.page,
      size: queryParams.size,
      activityName: queryParams.activityName || undefined,
      auditStatus: queryParams.auditStatus || undefined,
    }

    const res = await getReflectionPage(params)
    if (res.code === 200) {
      reflectionList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取心得列表失败')
    }
  } catch (error) {
    console.error('获取心得列表失败:', error)
    ElMessage.error('获取心得列表失败')
  } finally {
    loading.value = false
  }
}

// 图片上传处理
const uploadRef = ref<any>()
const handleFileChange = (file: any) => {
  handleUpload(file.raw)
}

const handleUpload = async (file: File) => {
  // 验证文件类型
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  if (!isJPG && !isPNG) {
    ElMessage.error('上传图片只能是 JPG 或 PNG 格式!')
    return
  }

  // 验证文件大小（不超过2MB）
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
    return
  }

  try {
    // 调用上传接口
    const response = await uploadImage(file)
    if (response && response.code === 200) {
      editForm.images = response.data
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error(response?.message || '图片上传失败')
    }
  } catch (error) {
    console.error('上传图片出错:', error)
    ElMessage.error('图片上传失败，请稍后重试')
  }
}

const handleSizeChange = (size: number) => {
  queryParams.size = size
  getList()
}

const handleCurrentChange = (page: number) => {
  queryParams.page = page
  getList()
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

.image-upload-container {
  display: flex;
  flex-direction: column;
}

.image-preview {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.upload-area {
  margin-top: 10px;
}

.image-uploader {
  width: 100%;
}

.clickable-image {
  cursor: pointer;
  transition: transform 0.2s;
}

.clickable-image:hover {
  transform: scale(1.1);
}
</style>
