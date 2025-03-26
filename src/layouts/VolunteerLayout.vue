<template>
  <div class="volunteer-layout">
    <el-container class="layout-container">
      <!-- 侧边栏 -->
      <el-aside width="220px" class="aside">
        <div class="logo-container">
          <img src="http://60.204.157.137:9000/volunteer/logo.png" alt="Logo" class="logo" />
          <span class="title">志愿者服务平台</span>
        </div>
        <el-scrollbar>
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical"
            background-color="#001529"
            text-color="#fff"
            active-text-color="#409EFF"
            :collapse="isCollapse"
            router
          >
            <el-menu-item index="/volunteer/home">
              <el-icon><HomeFilled /></el-icon>
              <template #title>首页</template>
            </el-menu-item>
            <el-menu-item index="/volunteer/projects">
              <el-icon><Document /></el-icon>
              <template #title>志愿项目</template>
            </el-menu-item>
            <el-menu-item index="/volunteer/activities">
              <el-icon><Calendar /></el-icon>
              <template #title>我的活动</template>
            </el-menu-item>
            <el-menu-item index="/volunteer/hours">
              <el-icon><Timer /></el-icon>
              <template #title>服务时长</template>
            </el-menu-item>
            <el-menu-item index="/volunteer/certificates">
              <el-icon><Medal /></el-icon>
              <template #title>我的证书</template>
            </el-menu-item>
            <el-menu-item index="/volunteer/profile">
              <el-icon><User /></el-icon>
              <template #title>个人中心</template>
            </el-menu-item>
          </el-menu>
        </el-scrollbar>
      </el-aside>

      <el-container class="main-container">
        <!-- 顶部栏 -->
        <el-header class="header">
          <div class="header-left">
            <el-icon class="collapse-btn" @click="toggleSidebar">
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/volunteer/home' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-tooltip content="消息" placement="bottom">
              <el-badge :value="2" class="badge">
                <el-icon class="header-icon"><Bell /></el-icon>
              </el-badge>
            </el-tooltip>
            <el-dropdown trigger="click">
              <div class="user-info">
                <el-avatar
                  :size="32"
                  src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
                />
                <span class="username">{{ username }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/volunteer/profile')"
                    >个人信息</el-dropdown-item
                  >
                  <el-dropdown-item>修改密码</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 内容区域 -->
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  HomeFilled,
  Document,
  Calendar,
  Timer,
  Medal,
  User,
  Bell,
  Fold,
  Expand,
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)
const username = ref(localStorage.getItem('username') || '志愿者')

const activeMenu = computed(() => {
  return route.path
})

const currentRoute = computed(() => {
  const pathMap: Record<string, string> = {
    '/volunteer/home': '首页',
    '/volunteer/projects': '志愿项目',
    '/volunteer/activities': '我的活动',
    '/volunteer/hours': '服务时长',
    '/volunteer/certificates': '我的证书',
    '/volunteer/profile': '个人中心',
  }
  return pathMap[route.path] || '首页'
})

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      // 清除登录信息
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')

      ElMessage.success('退出登录成功')
      router.push('/login')
    })
    .catch(() => {
      // 取消退出
    })
}
</script>

<style scoped>
.volunteer-layout {
  height: 100vh;
  overflow: hidden;
}

.layout-container {
  height: 100%;
}

.aside {
  background-color: #001529;
  color: white;
  height: 100%;
  overflow: hidden;
  transition: width 0.3s;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background-color: #002140;
}

.logo {
  height: 32px;
  margin-right: 10px;
}

.title {
  color: white;
  font-size: 16px;
  font-weight: bold;
  white-space: nowrap;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 220px;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.header-icon {
  font-size: 20px;
  margin: 0 10px;
  cursor: pointer;
}

.badge {
  margin: 0 15px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  font-size: 14px;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
