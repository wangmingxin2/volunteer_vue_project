<template>
  <div class="admin-announcements">
    <el-card class="filter-card">
      <div class="filter-container">
        <el-form :inline="true" :model="queryParams" class="filter-form">
          <el-form-item label="状态">
            <el-select
              v-model="queryParams.status"
              placeholder="请选择状态"
              clearable
              style="width: 180px"
            >
              <el-option label="草稿" value="0" />
              <el-option label="展示" value="1" />
              <el-option label="不展示" value="2" />
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
          <span>公告列表</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd">新增公告</el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="announcementList"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="公告ID" prop="announcementId" width="80" align="center" />
        <el-table-column label="封面图" width="80" align="center">
          <template #default="{ row }">
            <el-image
              style="width: 50px; height: 50px"
              :src="row.coverImage || defaultCover"
              fit="cover"
              @click="previewImage(row.coverImage)"
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
        <el-table-column label="公告标题" prop="title" min-width="150" show-overflow-tooltip />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info">草稿</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">展示</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger">不展示</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态操作" width="120" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="success"
              link
              @click="handleStatusChange(row, 1)"
            >
              发布
            </el-button>
            <el-button
              v-else-if="row.status === 1"
              type="danger"
              link
              @click="handleStatusChange(row, 2)"
            >
              下架
            </el-button>
            <el-button
              v-else-if="row.status === 2"
              type="success"
              link
              @click="handleStatusChange(row, 1)"
            >
              重新发布
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createdTime" min-width="160" />
        <el-table-column label="更新时间" prop="updatedTime" min-width="160" />
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handlePreview(row)">预览</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑公告对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '新增公告' : '编辑公告'"
      v-model="dialogVisible"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="announcementFormRef"
        :model="announcementForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="announcementForm.title" placeholder="请输入公告标题" />
        </el-form-item>

        <el-form-item label="封面图片" prop="coverImage" class="cover-upload-item">
          <el-upload
            class="cover-uploader"
            action="http://localhost:8080/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
          >
            <img
              v-if="announcementForm.coverImage"
              :src="announcementForm.coverImage"
              class="cover"
            />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="cover-tip">点击上传封面图片</div>
        </el-form-item>

        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="announcementForm.content"
            type="textarea"
            :rows="10"
            placeholder="请输入公告内容"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="announcementForm.status">
            <el-radio :label="0">草稿</el-radio>
            <el-radio :label="1">展示</el-radio>
            <el-radio :label="2">不展示</el-radio>
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

    <!-- 预览公告对话框 -->
    <el-dialog v-model="previewDialogVisible" title="公告预览" width="800px" center>
      <div class="announcement-preview">
        <h2 class="preview-title">{{ previewData.title }}</h2>
        <div class="preview-meta">
          <span>发布时间: {{ formatDateTime(previewData.createdTime) }}</span>
        </div>
        <div v-if="previewData.coverImage" class="preview-cover">
          <img :src="previewData.coverImage" alt="封面图片" />
        </div>
        <div class="preview-content">{{ previewData.content }}</div>
      </div>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="imagePreviewVisible" title="图片预览" width="600px" center>
      <div class="image-preview-container">
        <img :src="previewImageUrl" class="image-preview" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, UploadProps } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Picture, ArrowDown } from '@element-plus/icons-vue'
import {
  getAnnouncementPage,
  addAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
} from '../../api/announcement'
import type { Announcement } from '../../api/announcement'

// 查询参数
const queryParams = reactive({
  status: '',
  page: 1,
  size: 10,
})

// 数据列表
const announcementList = ref<Announcement[]>([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref<number[]>([])

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const announcementFormRef = ref<FormInstance>()
const submitLoading = ref(false)

// 预览相关
const previewDialogVisible = ref(false)
const previewData = reactive({
  announcementId: '',
  title: '',
  content: '',
  coverImage: '',
  status: 0,
  createdTime: '',
  updatedTime: '',
})

// 图片预览
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')

// 默认封面
const defaultCover =
  'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png'

// 表单数据
const announcementForm = reactive({
  announcementId: '',
  title: '',
  content: '',
  coverImage: '',
  status: 0,
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { max: 100, message: '标题长度不能超过100个字符', trigger: 'blur' },
  ],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  status: [{ required: true, message: '请选择公告状态', trigger: 'change' }],
}

// 上传相关
const uploadHeaders = {
  satoken: localStorage.getItem('satoken') || '',
}

// 获取公告列表
const getList = async () => {
  try {
    loading.value = true
    const params = {
      status: queryParams.status || undefined,
      page: queryParams.page,
      size: queryParams.size,
    }

    const res = await getAnnouncementPage(params)
    if (res.code === 200) {
      announcementList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取公告列表失败')
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
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
  queryParams.status = ''
  queryParams.page = 1
  getList()
}

// 表格选择项变化
const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map((item) => item.announcementId)
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

// 修改公告状态
const handleStatusChange = async (row: any, newStatus: number) => {
  try {
    let statusText = '发布'
    if (newStatus === 2) {
      statusText = '下架'
    } else if (row.status === 2 && newStatus === 1) {
      statusText = '重新发布'
    }

    const res = await updateAnnouncement({
      announcementId: row.announcementId,
      status: newStatus,
      title: row.title,
      content: row.content,
      coverImage: row.coverImage,
    })

    if (res.code === 200) {
      ElMessage.success(`公告${statusText}成功`)
      row.status = newStatus
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('修改公告状态失败:', error)
    ElMessage.error('修改状态失败，请稍后重试')
  }
}

// 封面上传前的校验
const beforeCoverUpload: UploadProps['beforeUpload'] = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('上传封面图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传封面图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 封面上传成功回调
const handleCoverSuccess = (res: any) => {
  if (res.code === 200) {
    announcementForm.coverImage = res.data
    ElMessage.success('封面上传成功')
  } else {
    ElMessage.error(res.message || '封面上传失败')
  }
}

// 预览图片
const previewImage = (url: string) => {
  if (!url) return
  previewImageUrl.value = url
  imagePreviewVisible.value = true
}

// 新增公告
const handleAdd = () => {
  dialogType.value = 'add'
  announcementForm.announcementId = ''
  announcementForm.title = ''
  announcementForm.content = ''
  announcementForm.coverImage = ''
  announcementForm.status = 0
  dialogVisible.value = true
}

// 编辑公告
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  announcementForm.announcementId = row.announcementId
  announcementForm.title = row.title
  announcementForm.content = row.content
  announcementForm.coverImage = row.coverImage || ''
  announcementForm.status = row.status
  dialogVisible.value = true
}

// 预览公告
const handlePreview = (row: any) => {
  previewData.announcementId = row.announcementId
  previewData.title = row.title
  previewData.content = row.content
  previewData.coverImage = row.coverImage
  previewData.status = row.status
  previewData.createdTime = row.createdTime
  previewData.updatedTime = row.updatedTime
  previewDialogVisible.value = true
}

// 删除公告
const handleDelete = (row: Announcement) => {
  ElMessageBox.confirm(`确认删除公告 "${row.title}" 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const res = await deleteAnnouncement(row.announcementId.toString())
        if (res.code === 200) {
          ElMessage.success('删除成功')
          getList()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        console.error('删除公告失败:', error)
      }
    })
    .catch(() => {
      // 取消删除
    })
}

// 提交表单
const submitForm = async () => {
  if (!announcementFormRef.value) return

  try {
    await announcementFormRef.value.validate()
    submitLoading.value = true

    const formData = {
      announcementId: announcementForm.announcementId || undefined,
      title: announcementForm.title,
      content: announcementForm.content,
      coverImage: announcementForm.coverImage,
      status: announcementForm.status,
    }

    if (dialogType.value === 'add') {
      // 新增公告
      const res = await addAnnouncement(formData)
      if (res.code === 200) {
        ElMessage.success('新增公告成功')
        dialogVisible.value = false
        getList()
      } else {
        ElMessage.error(res.message || '新增公告失败')
      }
    } else {
      // 编辑公告
      const res = await updateAnnouncement(formData)
      if (res.code === 200) {
        ElMessage.success('编辑公告成功')
        dialogVisible.value = false
        getList()
      } else {
        ElMessage.error(res.message || '编辑公告失败')
      }
    }
    submitLoading.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
    submitLoading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString()
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.admin-announcements {
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

.cover-upload-item {
  display: flex;
  justify-content: center;
}

.cover-uploader {
  width: 200px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  text-align: center;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.cover {
  width: 200px;
  height: 120px;
  display: block;
  object-fit: cover;
}

.cover-tip {
  font-size: 12px;
  color: #606266;
  margin-top: 5px;
  text-align: center;
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
  max-height: 500px;
}

.announcement-preview {
  padding: 20px;
}

.preview-title {
  text-align: center;
  margin-bottom: 20px;
}

.preview-meta {
  text-align: center;
  color: #999;
  margin-bottom: 20px;
}

.preview-cover {
  text-align: center;
  margin-bottom: 20px;
}

.preview-cover img {
  max-width: 100%;
  max-height: 300px;
}

.preview-content {
  line-height: 1.8;
  white-space: pre-wrap;
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
