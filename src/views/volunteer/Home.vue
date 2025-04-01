<template>
  <div class="volunteer-home">
    <el-card class="welcome-card">
      <h1 class="welcome-title">欢迎来到志愿者服务平台</h1>
      <div class="welcome-content">
        <p class="welcome-text">
          志愿服务是一种无私奉献、助人为乐的崇高精神，是促进社会文明进步的重要力量。作为志愿者，您用实际行动传递着爱心和温暖，为社会注入正能量，为需要帮助的人群提供支持，共同建设更加和谐美好的社会。
        </p>
        <p class="welcome-text">
          本平台为您提供便捷的志愿服务工具，帮助您发现适合的志愿活动、记录服务经历、分享活动心得，让志愿服务更加便捷、高效、有意义。
        </p>
      </div>
    </el-card>

    <el-row :gutter="20" class="feature-cards">
      <el-col :xs="24" :sm="12" :md="8">
        <el-card
          class="feature-card"
          shadow="hover"
          @click="router.push('/volunteer/activity-signup')"
        >
          <template #header>
            <div class="card-header">
              <el-icon><Plus /></el-icon>
              <span>活动报名</span>
            </div>
          </template>
          <div class="card-content">
            <p>浏览并报名参加各类志愿服务活动，贡献您的爱心与力量</p>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="feature-card" shadow="hover" @click="router.push('/volunteer/activities')">
          <template #header>
            <div class="card-header">
              <el-icon><List /></el-icon>
              <span>我的活动</span>
            </div>
          </template>
          <div class="card-content">
            <p>查看您已报名的志愿活动，追踪报名状态，记录服务时长</p>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="feature-card" shadow="hover" @click="router.push('/volunteer/reflections')">
          <template #header>
            <div class="card-header">
              <el-icon><Edit /></el-icon>
              <span>心得记录</span>
            </div>
          </template>
          <div class="card-content">
            <p>记录和分享您的志愿服务体验和感悟，传递志愿精神</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Plus, List, Edit } from '@element-plus/icons-vue'

const router = useRouter()
</script>

<style scoped>
.volunteer-home {
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
