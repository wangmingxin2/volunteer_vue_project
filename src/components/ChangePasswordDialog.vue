<template>
  <el-dialog
    v-model="dialogVisible"
    title="修改密码"
    width="400px"
    :close-on-click-modal="false"
    :before-close="handleClose"
  >
    <el-form
      :model="passwordForm"
      :rules="rules"
      ref="passwordFormRef"
      label-width="100px"
      class="password-form"
    >
      <el-form-item label="原密码" prop="oldPassword">
        <el-input
          v-model="passwordForm.oldPassword"
          type="password"
          placeholder="请输入原密码"
          show-password
        />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="passwordForm.newPassword"
          type="password"
          placeholder="请输入新密码"
          show-password
        />
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input
          v-model="passwordForm.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          show-password
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import type { FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import { updatePassword } from '../api/user'
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['update:visible', 'success'])

const dialogVisible = ref(props.visible)
const passwordFormRef = ref<FormInstance>()
const loading = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

// 自定义校验规则
const validatePass = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请输入新密码'))
  } else {
    if (passwordForm.confirmPassword !== '') {
      if (passwordFormRef.value) {
        passwordFormRef.value.validateField('confirmPassword')
      }
    }
    callback()
  }
}

const validatePass2 = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' },
  ],
}

// 监听 visible 属性变化
watch(
  () => props.visible,
  (newVal) => {
    dialogVisible.value = newVal
  },
)

// 监听对话框可见性变化
watch(
  () => dialogVisible.value,
  (newVal) => {
    emit('update:visible', newVal)
    if (!newVal) {
      // 重置表单
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      if (passwordFormRef.value) {
        passwordFormRef.value.resetFields()
      }
    }
  },
)

const handleClose = () => {
  dialogVisible.value = false
}

const handleSubmit = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    loading.value = true

    try {
      const res = await updatePassword(passwordForm.oldPassword, passwordForm.newPassword)
      if (res.code === 200) {
        ElMessage.success('密码修改成功，请重新登录')
        dialogVisible.value = false
        emit('success')

        // 清除登录信息
        localStorage.removeItem('satoken')
        localStorage.removeItem('userId')
        localStorage.removeItem('username')
        localStorage.removeItem('role')

        // 跳转到登录页
        setTimeout(() => {
          router.push('/login')
        }, 1500)
      } else {
        ElMessage.error(res.message || '密码修改失败')
      }
    } catch (error: any) {
      console.error('修改密码错误:', error)
      ElMessage.error(error.message || '密码修改失败，请稍后重试')
    } finally {
      loading.value = false
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}
</script>

<style scoped>
.password-form {
  margin: 0 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
