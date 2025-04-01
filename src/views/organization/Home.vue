<template>
  <div class="organization-home">
    <el-card class="welcome-card">
      <h1 class="welcome-title">欢迎来到志愿组织管理平台</h1>
      <div class="welcome-content">
        <p class="welcome-text">
          志愿服务是现代社会文明的重要标志，是公民参与社会生活、服务社会的重要方式。通过志愿服务，我们可以传递爱心、奉献社会、促进和谐。作为志愿组织，您肩负着引导和组织志愿者参与社会公益活动的重要使命，为构建互助友爱的和谐社会贡献力量。
        </p>
        <p class="welcome-text">
          本平台为您提供便捷的志愿活动管理工具，帮助您高效组织活动、管理报名、收集反馈，让志愿服务更加专业化、规范化、系统化。
        </p>
      </div>
    </el-card>

    <el-row :gutter="20" class="feature-cards">
      <el-col :xs="24" :sm="12" :md="8">
        <el-card
          class="feature-card"
          shadow="hover"
          @click="router.push('/organization/activities')"
        >
          <template #header>
            <div class="card-header">
              <el-icon><Calendar /></el-icon>
              <span>活动管理</span>
            </div>
          </template>
          <div class="card-content">
            <p>创建和管理志愿活动，设置活动时间、地点、名额等信息</p>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <el-card
          class="feature-card"
          shadow="hover"
          @click="router.push('/organization/signup-management')"
        >
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>报名管理</span>
            </div>
          </template>
          <div class="card-content">
            <p>审核志愿者报名申请，管理活动参与情况，记录服务时长</p>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <el-card
          class="feature-card"
          shadow="hover"
          @click="router.push('/organization/reflection-management')"
        >
          <template #header>
            <div class="card-header">
              <el-icon><ChatLineSquare /></el-icon>
              <span>心得管理</span>
            </div>
          </template>
          <div class="card-content">
            <p>查看志愿者活动心得，了解活动效果和志愿者感受</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Calendar, Document, ChatLineSquare } from '@element-plus/icons-vue'

const router = useRouter()
</script>

<style scoped>
.organization-home {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 30px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.welcome-title {
  color: #409eff;
  text-align: center;
  font-size: 28px;
  margin-bottom: 20px;
}

.welcome-content {
  max-width: 900px;
  margin: 0 auto;
}

.welcome-text {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
  text-indent: 2em;
  margin-bottom: 16px;
}

.feature-cards {
  margin-top: 30px;
}

.feature-card {
  height: 100%;
  cursor: pointer;
  transition: all 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
}

.card-header .el-icon {
  margin-right: 10px;
  font-size: 20px;
}

.card-content {
  color: #606266;
}

@media (max-width: 768px) {
  .welcome-title {
    font-size: 24px;
  }

  .welcome-text {
    font-size: 14px;
  }

  .feature-card {
    margin-bottom: 15px;
  }
}
</style>
