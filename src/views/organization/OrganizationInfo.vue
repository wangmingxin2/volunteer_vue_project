<template>
  <div class="organization-info">
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <span>组织信息</span>
          <el-button type="primary" @click="handleEdit">编辑</el-button>
        </div>
      </template>
      <el-skeleton :rows="6" animated v-if="loading" />
      <div v-else class="info-content">
        <div class="logo-container">
          <el-avatar :size="100" :src="orgInfo.logo || defaultLogo" />
          <div class="org-name">{{ orgInfo.orgName }}</div>
          <div class="org-status">
            <el-tag :type="orgInfo.status === 1 ? 'success' : 'danger'">
              {{ orgInfo.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </div>
        </div>
        <el-divider />
        <div class="info-list">
          <div class="info-item">
            <span class="info-label">组织ID：</span>
            <span class="info-value">{{ orgInfo.orgId }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">联系人：</span>
            <span class="info-value">{{ orgInfo.contactPerson || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">联系电话：</span>
            <span class="info-value">{{ orgInfo.contactPhone || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">联系邮箱：</span>
            <span class="info-value">{{ orgInfo.contactEmail || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">组织地址：</span>
            <span class="info-value">{{ orgInfo.address || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">成立日期：</span>
            <span class="info-value">{{ formatDate(orgInfo.establishmentDate) || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">志愿者数量：</span>
            <span class="info-value">{{ orgInfo.count || 0 }}人</span>
          </div>
          <div class="info-item">
            <span class="info-label">组织简介：</span>
            <div class="info-value description">{{ orgInfo.description || '未设置' }}</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 编辑组织信息对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑组织信息"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="editForm"
        :rules="rules"
        ref="editFormRef"
        label-width="100px"
        class="edit-form"
      >
        <el-form-item label="组织Logo">
          <div class="logo-uploader">
            <el-upload
              class="avatar-uploader"
              action="/api/file/upload"
              :show-file-list="false"
              :on-success="handleLogoSuccess"
              :before-upload="beforeLogoUpload"
            >
              <img v-if="editForm.logo" :src="editForm.logo" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="组织名称" prop="orgName">
          <el-input v-model="editForm.orgName" placeholder="请输入组织名称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="editForm.contactPerson" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="editForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系邮箱" prop="contactEmail">
          <el-input v-model="editForm.contactEmail" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="组织地址" prop="address">
          <el-input v-model="editForm.address" placeholder="请输入组织地址" />
        </el-form-item>
        <el-form-item label="成立日期" prop="establishmentDate">
          <el-date-picker
            v-model="editForm.establishmentDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="组织简介" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入组织简介"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getOrgByManagerId, updateOrgById } from '../../api/organization'
import { Plus } from '@element-plus/icons-vue'

// 默认组织Logo
const defaultLogo = 'http://60.204.157.137:9000/volunteer/default-org.png'

// 组织信息
const orgInfo = ref({
  orgId: '',
  managerId: '',
  orgName: '',
  description: '',
  logo: '',
  contactPerson: '',
  contactPhone: '',
  contactEmail: '',
  address: '',
  establishmentDate: '',
  count: 0,
  status: 1,
})

// 加载状态
const loading = ref(true)

// 编辑表单数据
const editForm = reactive({
  orgId: '',
  orgName: '',
  description: '',
  logo: '',
  contactPerson: '',
  contactPhone: '',
  contactEmail: '',
  address: '',
  establishmentDate: '',
})

// 编辑对话框可见性
const editDialogVisible = ref(false)
const editFormRef = ref<FormInstance>()

// 表单验证规则
const rules = reactive<FormRules>({
  orgName: [
    { required: true, message: '请输入组织名称', trigger: 'blur' },
    { max: 50, message: '组织名称不能超过50个字符', trigger: 'blur' },
  ],
  contactPerson: [{ max: 30, message: '联系人姓名不能超过30个字符', trigger: 'blur' }],
  contactPhone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }],
  contactEmail: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  description: [{ max: 500, message: '组织简介不能超过500个字符', trigger: 'blur' }],
})

// 获取组织信息
const fetchOrgInfo = async () => {
  try {
    loading.value = true
    const userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.error('获取用户ID失败')
      return
    }

    const res = await getOrgByManagerId(userId)
    if (res.code === 200) {
      if (Array.isArray(res.data) && res.data.length > 0) {
        orgInfo.value = res.data[0]
      } else {
        orgInfo.value = res.data
      }
      console.log('获取到的组织信息:', orgInfo.value)
    } else {
      ElMessage.error(res.message || '获取组织信息失败')
    }
  } catch (error) {
    console.error('获取组织信息失败:', error)
    ElMessage.error('获取组织信息失败')
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateString: string | null | undefined) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  if (isNaN(date.getTime())) return '' // 无效日期返回空字符串

  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

// 编辑按钮点击
const handleEdit = () => {
  editForm.orgId = orgInfo.value.orgId
  editForm.orgName = orgInfo.value.orgName
  editForm.description = orgInfo.value.description || ''
  editForm.logo = orgInfo.value.logo || ''
  editForm.contactPerson = orgInfo.value.contactPerson || ''
  editForm.contactPhone = orgInfo.value.contactPhone || ''
  editForm.contactEmail = orgInfo.value.contactEmail || ''
  editForm.address = orgInfo.value.address || ''
  editForm.establishmentDate = orgInfo.value.establishmentDate || ''

  editDialogVisible.value = true
}

// Logo上传前的验证
const beforeLogoUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('上传头像图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

// Logo上传成功回调
const handleLogoSuccess = (res: any) => {
  if (res.code === 200) {
    editForm.logo = res.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

// 提交表单
const submitForm = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()

    // 确保传递正确的参数
    const updateData = { ...editForm }

    const res = await updateOrgById(editForm.orgId, updateData)
    if (res.code === 200) {
      ElMessage.success('组织信息更新成功')
      editDialogVisible.value = false
      fetchOrgInfo() // 重新获取组织信息
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error: any) {
    console.error('更新组织信息错误:', error)
    ElMessage.error(error.message || '提交表单时发生错误')
  }
}

onMounted(() => {
  fetchOrgInfo()
})
</script>

<style scoped>
.organization-info {
  padding: 10px;
}

.info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.org-name {
  font-size: 20px;
  font-weight: bold;
  margin: 10px 0 5px;
}

.org-status {
  margin: 5px 0;
}

.info-content {
  padding: 10px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: flex-start;
}

.info-label {
  width: 100px;
  flex-shrink: 0;
  color: #606266;
}

.info-value {
  flex: 1;
  color: #303133;
}

.description {
  white-space: pre-line;
  line-height: 1.5;
}

.edit-form {
  max-height: 70vh;
  overflow-y: auto;
  padding: 0 20px;
}

.logo-uploader {
  text-align: center;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  display: inline-block;
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
