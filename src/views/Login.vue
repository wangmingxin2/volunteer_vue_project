<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-left">
        <img
          src="http://60.204.157.137:9000/volunteer/001.jpg"
          alt="登录插图"
          class="login-image"
        />
      </div>
      <div class="login-right">
        <div class="login-header">
          <div class="logo">
            <img
              src="http://60.204.157.137:9000/volunteer/logo.png"
              alt="Logo"
              class="logo-image"
            />
          </div>
          <h2 class="welcome-text">欢迎登录</h2>
          <p class="system-name">志愿者服务平台</p>
        </div>
        <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item prop="role" class="role-select">
            <p class="role-label">角色</p>
            <el-radio-group v-model="loginForm.role">
              <el-radio label="admin">管理员</el-radio>
              <el-radio label="organization">志愿组织</el-radio>
              <el-radio label="volunteer">志愿者</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin"
              >登录</el-button
            >
          </el-form-item>
          <div class="login-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <router-link to="/register" class="register-link">注册账号</router-link>
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
import { login } from '../api/user'
import type { UserDto } from '../api/user'

const router = useRouter()
const loginFormRef = ref<FormInstance>()
const rememberMe = ref(false)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  role: 'volunteer',
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

// 角色映射到 typeId
const roleToTypeId = {
  admin: '1',
  organization: '2',
  volunteer: '3',
}

// 角色映射到路由路径
const roleToRoute = {
  admin: '/admin',
  organization: '/organization',
  volunteer: '/volunteer',
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    const valid = await loginFormRef.value.validate()
    if (valid) {
      try {
        loading.value = true

        // 构建登录参数
        const userDto: UserDto = {
          username: loginForm.username,
          password: loginForm.password,
          typeId: roleToTypeId[loginForm.role as keyof typeof roleToTypeId],
        }

        console.log('发送登录请求:', userDto)

        // 调用登录接口
        const res = await login(userDto)
        console.log('登录响应:', res)

        // 登录成功处理
        if (res.code === 200) {
          // 保存登录信息到本地存储
          localStorage.setItem('satoken', res.data.satoken)
          localStorage.setItem('userId', res.data.userId.toString())
          localStorage.setItem('username', res.data.username)
          localStorage.setItem('role', loginForm.role)

          // 如果选择了记住我，可以设置更长的过期时间
          if (rememberMe.value) {
            // 设置记住我的逻辑
          }

          ElMessage.success('登录成功')

          // 根据选择的角色跳转到对应页面
          router.push(roleToRoute[loginForm.role as keyof typeof roleToRoute] || '/')
        } else {
          ElMessage.error(res.message || '登录失败')
        }
      } catch (error: any) {
        console.error('登录错误:', error)
        ElMessage.error(error.message || '登录失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  } catch (error) {
    ElMessage.error('请填写正确的登录信息')
    return false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #1e90ff, #00bfff);
}

.login-box {
  display: flex;
  width: 900px;
  height: 500px;
  background-color: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
}

.login-left {
  flex: 1;
  background-color: #f0f9ff;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.login-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.login-right {
  flex: 1;
  padding: 40px;
  display: flex;
  flex-direction: column;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
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

.login-form {
  flex: 1;
}

.role-select {
  margin-bottom: 20px;
}

.role-label {
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
}

.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  border-radius: 4px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.register-link {
  color: #409eff;
  text-decoration: none;
}

.register-link:hover {
  text-decoration: underline;
}
</style>
