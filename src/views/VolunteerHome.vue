<template>
  <div class="volunteer-home">
    <el-card class="welcome-card">
      <div class="welcome-header">
        <img
          src="http://60.204.157.137:9000/volunteer/volunteer-welcome.png"
          alt="欢迎图片"
          class="welcome-image"
        />
        <div class="welcome-text">
          <h1>欢迎来到志愿者服务平台</h1>
          <p>您可以在这里浏览志愿项目、参与志愿活动、查看服务时长和获取志愿证书</p>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="info-row">
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="info-card">
          <template #header>
            <div class="card-header">
              <span>我的服务</span>
              <el-button class="button" text>查看更多</el-button>
            </div>
          </template>
          <div class="info-content">
            <div class="info-item">
              <div class="info-label">累计服务时长</div>
              <div class="info-value">24<span class="info-unit">小时</span></div>
            </div>
            <div class="info-item">
              <div class="info-label">参与项目数</div>
              <div class="info-value">5<span class="info-unit">个</span></div>
            </div>
            <div class="info-item">
              <div class="info-label">获得证书</div>
              <div class="info-value">2<span class="info-unit">个</span></div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="info-card">
          <template #header>
            <div class="card-header">
              <span>近期活动</span>
              <el-button class="button" text>查看更多</el-button>
            </div>
          </template>
          <div class="activity-list">
            <div class="activity-item" v-for="(activity, index) in recentActivities" :key="index">
              <div class="activity-time">{{ activity.time }}</div>
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-status" :class="activity.statusClass">
                  {{ activity.status }}
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card shadow="hover" class="info-card">
          <template #header>
            <div class="card-header">
              <span>推荐项目</span>
              <el-button class="button" text>更多项目</el-button>
            </div>
          </template>
          <div class="project-list">
            <div class="project-item" v-for="(project, index) in recommendedProjects" :key="index">
              <el-image :src="project.image" class="project-image"></el-image>
              <div class="project-info">
                <div class="project-title">{{ project.title }}</div>
                <div class="project-org">{{ project.organization }}</div>
                <div class="project-time">{{ project.time }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const recentActivities = ref([
  {
    time: '2023-04-15',
    title: '社区清洁活动',
    status: '已完成',
    statusClass: 'status-completed',
  },
  {
    time: '2023-04-10',
    title: '敬老院慰问',
    status: '已完成',
    statusClass: 'status-completed',
  },
  {
    time: '2023-04-25',
    title: '植树活动',
    status: '待参加',
    statusClass: 'status-pending',
  },
])

const recommendedProjects = ref([
  {
    image: 'http://60.204.157.137:9000/volunteer/project1.jpg',
    title: '城市公园清洁日',
    organization: '绿色环保协会',
    time: '2023-05-01 09:00-12:00',
  },
  {
    image: 'http://60.204.157.137:9000/volunteer/project2.jpg',
    title: '关爱留守儿童',
    organization: '阳光志愿者协会',
    time: '2023-05-05 14:00-17:00',
  },
])
</script>

<style scoped>
.volunteer-home {
  padding: 0;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-header {
  display: flex;
  align-items: center;
}

.welcome-image {
  width: 120px;
  height: 120px;
  margin-right: 20px;
}

.welcome-text h1 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.welcome-text p {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.info-row {
  margin-bottom: 20px;
}

.info-card {
  margin-bottom: 20px;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-content {
  display: flex;
  flex-direction: column;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 14px;
  color: #606266;
}

.info-value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.info-unit {
  font-size: 14px;
  font-weight: normal;
  margin-left: 5px;
}

.activity-list {
  display: flex;
  flex-direction: column;
}

.activity-item {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-time {
  width: 80px;
  font-size: 14px;
  color: #909399;
}

.activity-content {
  flex: 1;
  display: flex;
  justify-content: space-between;
}

.activity-title {
  font-size: 14px;
  color: #303133;
}

.activity-status {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
}

.status-completed {
  background-color: #f0f9eb;
  color: #67c23a;
}

.status-pending {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.project-list {
  display: flex;
  flex-direction: column;
}

.project-item {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.project-item:last-child {
  border-bottom: none;
}

.project-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  margin-right: 10px;
}

.project-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.project-title {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.project-org {
  font-size: 12px;
  color: #606266;
  margin-bottom: 5px;
}

.project-time {
  font-size: 12px;
  color: #909399;
}
</style>
