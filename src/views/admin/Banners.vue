<template>
  <div class="admin-banners">
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
              <el-option label="禁用" value="0" />
              <el-option label="启用" value="1" />
            </el-select>
          </el-form-item>
          <el-form-item label="类型">
            <el-select
              v-model="queryParams.bannerType"
              placeholder="请选择类型"
              clearable
              style="width: 180px"
            >
              <el-option label="系统公告" value="0" />
              <el-option label="活动宣传" value="1" />
              <el-option label="组织宣传" value="2" />
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
          <span>轮播图列表</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd">新增轮播图</el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="bannerList"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" prop="bannerId" width="80" align="center" />
        <el-table-column label="图片" width="120" align="center">
          <template #default="{ row }">
            <el-image
              style="width: 100px; height: 50px"
              :src="row.imageUrl || defaultImage"
              fit="cover"
              @click="previewImage(row.imageUrl)"
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
        <el-table-column label="标题" prop="title" min-width="150" show-overflow-tooltip />
        <el-table-column label="类型" prop="bannerType" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.bannerType === 0" type="info">系统公告</el-tag>
            <el-tag v-else-if="row.bannerType === 1" type="success">活动宣传</el-tag>
            <el-tag v-else-if="row.bannerType === 2" type="warning">组织宣传</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createdTime" min-width="160" />
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
      :title="dialogType === 'add' ? '新增轮播图' : '编辑轮播图'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="bannerFormRef"
        :model="bannerForm"
        :rules="rules"
        label-width="100px"
        class="banner-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="bannerForm.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="bannerForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入轮播图描述"
          />
        </el-form-item>
        <el-form-item label="类型" prop="bannerType">
          <el-select
            v-model="bannerForm.bannerType"
            placeholder="请选择轮播图类型"
            style="width: 100%"
          >
            <el-option label="系统公告" :value="0" />
            <el-option label="活动宣传" :value="1" />
            <el-option label="组织宣传" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="bannerForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="轮播图片" prop="imageUrl">
          <el-upload
            class="banner-uploader"
            action="http://localhost:8080/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
          >
            <img v-if="bannerForm.imageUrl" :src="bannerForm.imageUrl" class="banner-image" />
            <el-icon v-else class="banner-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="banner-tip">建议尺寸: 1920x400px，大小不超过2MB</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 图片预览 -->
    <el-dialog v-model="previewVisible" title="图片预览" width="800px">
      <div class="image-preview-container">
        <img :src="previewUrl" class="image-preview" />
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
  getBannerPage,
  addBanner,
  updateBanner,
  deleteBanner,
  updateBannerStatus,
} from '../../api/banner'
import type { Banner } from '../../api/banner'

// 查询参数
const queryParams = reactive({
  status: '',
  bannerType: '',
  page: 1,
  size: 10,
})

// 表格数据
const bannerList = ref<Banner[]>([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref<number[]>([])

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const bannerFormRef = ref<FormInstance>()
const submitLoading = ref(false)

// 默认图片
const defaultImage = 'http://60.204.157.137:9000/volunteer/default-banner.jpg'

// 表单数据
const bannerForm = reactive({
  bannerId: '',
  title: '',
  imageUrl: '',
  description: '',
  bannerType: 0,
  status: 1,
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入轮播图标题', trigger: 'blur' },
    { max: 100, message: '标题长度不能超过100个字符', trigger: 'blur' },
  ],
  imageUrl: [{ required: true, message: '请上传轮播图片', trigger: 'change' }],
  bannerType: [{ required: true, message: '请选择轮播图类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择轮播图状态', trigger: 'change' }],
}

// 上传相关
const uploadHeaders = {
  satoken: localStorage.getItem('satoken') || '',
}

// 图片预览
const previewVisible = ref(false)
const previewUrl = ref('')

// 获取轮播图列表
const getList = async () => {
  try {
    loading.value = true

    // 使用新的参数结构调用 getBannerPage
    const page = queryParams.page || 1
    const size = queryParams.size || 10
    const status = queryParams.status ? parseInt(queryParams.status) : undefined
    const bannerType = queryParams.bannerType ? parseInt(queryParams.bannerType) : undefined

    const res = await getBannerPage(page, size, status, bannerType)

    if (res.code === 200) {
      bannerList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取轮播图列表失败')
    }
  } catch (error) {
    console.error('获取轮播图列表失败:', error)
    ElMessage.error('获取轮播图列表失败')
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
  queryParams.bannerType = ''
  queryParams.page = 1
  getList()
}

// 表格选择项变化
const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map((item) => item.bannerId)
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

// 修改轮播图状态
const handleStatusChange = async (row: any) => {
  try {
    const res = await updateBannerStatus(row.bannerId, row.status)
    if (res.code === 200) {
      ElMessage.success(`${row.status === 1 ? '启用' : '禁用'}成功`)
    } else {
      // 如果失败，回滚状态
      row.status = row.status === 1 ? 0 : 1
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    // 如果出错，回滚状态
    row.status = row.status === 1 ? 0 : 1
    console.error('修改轮播图状态失败:', error)
    ElMessage.error('修改状态失败，请稍后重试')
  }
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
    bannerForm.imageUrl = res.data
  } else {
    ElMessage.error('上传失败')
  }
}

// 预览图片
const previewImage = (url: string) => {
  if (!url) return
  previewUrl.value = url
  previewVisible.value = true
}

// 新增轮播图
const handleAdd = () => {
  dialogType.value = 'add'
  bannerForm.bannerId = ''
  bannerForm.title = ''
  bannerForm.imageUrl = ''
  bannerForm.description = ''
  bannerForm.bannerType = 0
  bannerForm.status = 1
  dialogVisible.value = true
}

// 编辑轮播图
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  bannerForm.bannerId = row.bannerId
  bannerForm.title = row.title
  bannerForm.imageUrl = row.imageUrl
  bannerForm.description = row.description || ''
  bannerForm.bannerType = row.bannerType
  bannerForm.status = row.status
  dialogVisible.value = true
}

// 删除轮播图
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除轮播图 ${row.title} 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const res = await deleteBanner(row.bannerId.toString())
        if (res.code === 200) {
          ElMessage.success('删除成功')
          getList()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        console.error('删除轮播图失败:', error)
        ElMessage.error('删除失败，请稍后重试')
      }
    })
    .catch(() => {
      // 取消删除
    })
}

// 提交表单
const submitForm = async () => {
  if (!bannerFormRef.value) return

  try {
    await bannerFormRef.value.validate()
    submitLoading.value = true

    const formData = { ...bannerForm }

    if (dialogType.value === 'add') {
      // 新增轮播图
      const res = await addBanner(formData)
      if (res.code === 200) {
        ElMessage.success('新增轮播图成功')
        dialogVisible.value = false
        getList()
      } else {
        ElMessage.error(res.message || '新增轮播图失败')
      }
    } else {
      // 编辑轮播图
      const res = await updateBanner(formData)
      if (res.code === 200) {
        ElMessage.success('编辑轮播图成功')
        dialogVisible.value = false
        getList()
      } else {
        ElMessage.error(res.message || '编辑轮播图失败')
      }
    }
    submitLoading.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
    submitLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.admin-banners {
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

.banner-uploader {
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

.banner-uploader:hover {
  border-color: #409eff;
}

.banner-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 360px;
  height: 180px;
  line-height: 180px;
  text-align: center;
}

.banner-image {
  width: 360px;
  height: 180px;
  display: block;
  object-fit: cover;
}

.banner-tip {
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
</style>
