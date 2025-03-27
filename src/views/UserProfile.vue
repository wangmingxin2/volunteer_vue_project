<template>
  <div class="user-profile">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" @click="handleEdit">编辑</el-button>
        </div>
      </template>
      <el-skeleton :rows="6" animated v-if="loading" />
      <div v-else class="profile-content">
        <div class="avatar-container">
          <el-avatar :size="100" :src="userInfo.avatar || defaultAvatar" />
          <div class="username">{{ userInfo.username }}</div>
          <div class="user-role">{{ userRoleText }}</div>
        </div>
        <el-divider />
        <div class="info-list">
          <div class="info-item">
            <span class="info-label">用户ID：</span>
            <span class="info-value">{{ userInfo.userId }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">真实姓名：</span>
            <span class="info-value">{{ userInfo.realName || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">性别：</span>
            <span class="info-value">{{ genderText }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">身份证号：</span>
            <span class="info-value">{{ userInfo.idCard || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">邮箱：</span>
            <span class="info-value">{{ userInfo.email || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">手机号：</span>
            <span class="info-value">{{ userInfo.mobile || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">地址：</span>
            <span class="info-value">{{ userInfo.address || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">生日：</span>
            <span class="info-value">{{ formatDate(userInfo.birthday) || '未设置' }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="security-card">
      <template #header>
        <div class="card-header">
          <span>账号安全</span>
        </div>
      </template>
      <div class="security-content">
        <div class="security-item">
          <div class="security-info">
            <div class="security-title">登录密码</div>
            <div class="security-desc">定期修改密码可以保护账号安全</div>
          </div>
          <el-button @click="showChangePasswordDialog">修改</el-button>
        </div>
      </div>
    </el-card>

    <!-- 修改密码对话框 -->
    <change-password-dialog
      v-model:visible="passwordDialogVisible"
      @success="handlePasswordChanged"
    />

    <!-- 编辑个人信息对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑个人信息"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="editForm"
        :rules="rules"
        ref="editFormRef"
        label-width="100px"
        class="edit-form"
      >
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="editForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="editForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
            <el-radio :label="0">保密</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="editForm.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="editForm.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="editForm.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="生日" prop="birthday">
          <el-date-picker
            v-model="editForm.birthday"
            type="date"
            placeholder="选择生日"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:8080/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            name="file"
          >
            <img v-if="editForm.avatar" :src="editForm.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="editLoading" @click="submitEdit">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { getUserInfo, updateUserInfo } from '../api/user'
import type { UserInfo } from '../api/user'
import ChangePasswordDialog from '../components/ChangePasswordDialog.vue'
import { ElMessage, FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 角色映射到 typeId
const roleToTypeId = {
  admin: '1',
  organization: '2',
  volunteer: '3',
}

const loading = ref(true)
const editLoading = ref(false)
const userInfo = ref<UserInfo>({
  userId: localStorage.getItem('userId') || '',
  username: localStorage.getItem('username') || '',
  // 将角色转换为对应的 typeId
  typeId: roleToTypeId[localStorage.getItem('role') as keyof typeof roleToTypeId] || '',
})
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
const passwordDialogVisible = ref(false)
const editDialogVisible = ref(false)
const editFormRef = ref<FormInstance>()

// 编辑表单数据
const editForm = reactive({
  userId: localStorage.getItem('userId') || '',
  username: localStorage.getItem('username') || '',
  // 将角色转换为对应的 typeId
  typeId: roleToTypeId[localStorage.getItem('role') as keyof typeof roleToTypeId] || '',
  realName: '',
  gender: 0,
  idCard: '',
  email: '',
  mobile: '',
  address: '',
  birthday: '',
  avatar: '',
})

// 表单验证规则
const rules = {
  realName: [{ max: 20, message: '长度不能超过20个字符', trigger: 'blur' }],
  idCard: [
    {
      pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
      message: '请输入正确的身份证号',
      trigger: 'blur',
    },
  ],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  mobile: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
}

const userRoleText = computed(() => {
  const roleMap: Record<string, string> = {
    '1': '管理员',
    '2': '志愿组织',
    '3': '志愿者',
  }
  return roleMap[userInfo.value.typeId] || '未知角色'
})

const genderText = computed(() => {
  const genderMap: Record<number, string> = {
    0: '保密',
    1: '男',
    2: '女',
  }
  return genderMap[userInfo.value.gender || 0] || '保密'
})

// 格式化日期
const formatDate = (date: string | null | undefined) => {
  if (!date) return '未设置'
  return new Date(date).toLocaleDateString()
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    loading.value = true
    const res = await getUserInfo()
    if (res.code === 200) {
      userInfo.value = res.data
    } else {
      ElMessage.error(res.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 打开编辑对话框
const handleEdit = () => {
  // 复制用户信息到编辑表单
  editForm.userId = userInfo.value.userId
  editForm.username = userInfo.value.username
  editForm.typeId = userInfo.value.typeId
  editForm.realName = userInfo.value.realName || ''
  editForm.gender = userInfo.value.gender ? Number(userInfo.value.gender) : 0
  editForm.idCard = userInfo.value.idCard || ''
  editForm.email = userInfo.value.email || ''
  editForm.mobile = userInfo.value.mobile || userInfo.value.phone || ''
  editForm.address = userInfo.value.address || ''
  editForm.birthday = userInfo.value.birthday || ''
  editForm.avatar = userInfo.value.avatar || ''

  editDialogVisible.value = true
}

// 提交编辑
const submitEdit = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
    editLoading.value = true

    // 构建更新数据，确保包含 userId、username 和 typeId
    const updateData = {
      userId: userInfo.value.userId,
      username: userInfo.value.username,
      // 确保使用正确的 typeId
      typeId: userInfo.value.typeId,
      realName: editForm.realName,
      gender: String(editForm.gender),
      idCard: editForm.idCard,
      email: editForm.email,
      mobile: editForm.mobile,
      address: editForm.address,
      birthday: editForm.birthday,
      avatar: editForm.avatar,
    }

    console.log('更新用户信息:', updateData)

    const res = await updateUserInfo(updateData)
    if (res.code === 200) {
      ElMessage.success('个人信息更新成功')
      editDialogVisible.value = false
      // 重新获取用户信息
      fetchUserInfo()
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error: any) {
    console.error('更新用户信息错误:', error)
    ElMessage.error(error.message || '更新失败，请稍后重试')
  } finally {
    editLoading.value = false
  }
}

// 添加上传头像所需的请求头
const uploadHeaders = {
  satoken: localStorage.getItem('satoken') || '',
}

// 修改头像上传成功的处理函数
const handleAvatarSuccess = (res: any) => {
  if (res.code === 200) {
    editForm.avatar = res.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(res.message || '头像上传失败')
  }
}

// 头像上传前的验证
const beforeAvatarUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG 或 PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

const showChangePasswordDialog = () => {
  passwordDialogVisible.value = true
}

const handlePasswordChanged = () => {
  ElMessage.success('密码修改成功')
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.user-profile {
  max-width: 800px;
  margin: 0 auto;
}

.profile-card,
.security-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-content {
  padding: 20px 0;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.username {
  font-size: 18px;
  font-weight: bold;
  margin-top: 10px;
}

.user-role {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.info-list {
  padding: 0 20px;
}

.info-item {
  display: flex;
  margin-bottom: 15px;
  font-size: 14px;
}

.info-label {
  width: 100px;
  color: #606266;
}

.info-value {
  flex: 1;
  color: #303133;
}

.security-content {
  padding: 10px 0;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.security-item:last-child {
  border-bottom: none;
}

.security-title {
  font-size: 16px;
  margin-bottom: 5px;
}

.security-desc {
  font-size: 14px;
  color: #909399;
}

.edit-form {
  padding: 0 20px;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
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
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
