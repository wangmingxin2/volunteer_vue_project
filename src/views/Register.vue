<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-left">
        <img
          src="http://60.204.157.137:9000/volunteer/001.jpg"
          alt="注册插图"
          class="register-image"
        />
      </div>
      <div class="register-right">
        <div class="register-header">
          <div class="logo">
            <img
              src="http://60.204.157.137:9000/volunteer/logo.png"
              alt="Logo"
              class="logo-image"
            />
          </div>
          <h2 class="welcome-text">欢迎注册</h2>
          <p class="system-name">志愿者服务平台</p>
        </div>
        <el-form :model="registerForm" :rules="rules" ref="registerFormRef" class="register-form">
          <el-form-item prop="username">
            <el-input v-model="registerForm.username" placeholder="用户名" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="确认密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item prop="role" class="role-select">
            <p class="role-label">角色</p>
            <el-radio-group v-model="registerForm.role">
              <el-radio label="organization">志愿组织</el-radio>
              <el-radio label="volunteer">志愿者</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              class="register-button"
              :loading="loading"
              @click="handleRegister"
              >注册</el-button
            >
          </el-form-item>
          <div class="register-options">
            <span>已有账号？</span>
            <router-link to="/login" class="login-link">立即登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { register } from '../api/user'
import type { RegisterDto } from '../api/user'

const router = useRouter()
const registerFormRef = ref<FormInstance>()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  role: 'volunteer',
})

// 自定义校验规则
const validatePass = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (registerForm.confirmPassword !== '') {
      if (registerFormRef.value) {
        registerFormRef.value.validateField('confirmPassword')
      }
    }
    callback()
  }
}

const validatePass2 = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' },
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

// 角色映射到 typeId
const roleToTypeId = {
  organization: 2,
  volunteer: 3,
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    const valid = await registerFormRef.value.validate()
    if (valid) {
      try {
        loading.value = true

        // 构建注册参数
        const registerData: RegisterDto = {
          username: registerForm.username,
          password: registerForm.password,
          typeId: roleToTypeId[registerForm.role as keyof typeof roleToTypeId],
        }

        console.log('发送注册请求:', registerData)

        // 调用注册接口
        const res = await register(registerData)
        console.log('注册响应:', res)

        if (res.code === 200) {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        } else {
          ElMessage.error(res.message || '注册失败')
        }
      } catch (error: any) {
        console.error('注册错误:', error)
        ElMessage.error(error.message || '注册失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  } catch (error) {
    ElMessage.error('请填写正确的注册信息')
    return false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #1e90ff, #00bfff);
}

.register-box {
  display: flex;
  width: 900px;
  height: 600px;
  background-color: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
}

.register-left {
  flex: 1;
  background-color: #f0f9ff;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.register-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.register-right {
  flex: 1;
  padding: 30px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.register-header {
  text-align: center;
  margin-bottom: 20px;
}

.logo {
  margin-bottom: 15px;
}

.logo-image {
  height: 40px;
}

.welcome-text {
  font-size: 24px;
  color: #333;
  margin: 0 0 5px 0;
}

.system-name {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.register-form {
  flex: 1;
}

.role-select {
  margin-bottom: 15px;
}

.role-label {
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
}

.register-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  border-radius: 4px;
}

.register-options {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 15px;
  color: #606266;
}

.login-link {
  color: #409eff;
  text-decoration: none;
  margin-left: 5px;
}

.login-link:hover {
  text-decoration: underline;
}

/* 滚动条样式 */
.register-right::-webkit-scrollbar {
  width: 6px;
}

.register-right::-webkit-scrollbar-thumb {
  background-color: #ddd;
  border-radius: 3px;
}

.register-right::-webkit-scrollbar-track {
  background-color: #f5f5f5;
}
</style>
