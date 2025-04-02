<template>
  <div class="layout">
    <el-menu
      :default-active="activeIndex"
      class="el-menu-demo"
      mode="horizontal"
      router
      @select="handleSelect"
    >
      <div class="logo-container">
        <img src="http://60.204.157.137:9000/volunteer/logo.png" alt="Logo" class="logo" />
        <span class="title">志愿者服务网</span>
      </div>
      <el-menu-item index="/">首页</el-menu-item>
      <el-menu-item index="/activity">活动列表</el-menu-item>
      <el-menu-item index="/organizations">组织</el-menu-item>
      <el-button type="primary" class="auth-btn" @click="$router.push('/login')">登录</el-button>
      <el-button class="auth-btn" @click="$router.push('/register')">注册</el-button>
    </el-menu>

    <router-view></router-view>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const activeIndex = ref('/')

const handleSelect = (key: string) => {
  console.log(key)
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.el-menu-demo {
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.logo-container {
  display: flex;
  align-items: center;
  margin-right: 40px;
}

.logo {
  height: 32px;
  margin-right: 10px;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
  white-space: nowrap;
}

.flex-grow {
  flex: 1;
}

.auth-btn {
  margin: 0 5px;
}

/* 覆盖 Element Plus 的默认样式 */
:deep(.el-menu) {
  border-bottom: none;
}

:deep(.el-menu-item) {
  font-size: 16px;
}
</style>
