<template>
  <div class="organization-home">
    <el-card class="welcome-card">
      <div class="welcome-header">
        <img
          src="http://60.204.157.137:9000/volunteer/organization-welcome.png"
          alt="欢迎图片"
          class="welcome-image"
        />
        <div class="welcome-text">
          <h1>欢迎来到志愿组织管理平台</h1>
          <p>您可以在这里管理志愿项目、招募志愿者、记录活动和查看统计数据</p>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="info-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon-container">
            <el-icon class="stat-icon projects"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">12</div>
            <div class="stat-label">发布项目</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon-container">
            <el-icon class="stat-icon volunteers"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">156</div>
            <div class="stat-label">志愿者人数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon-container">
            <el-icon class="stat-icon activities"><Calendar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">28</div>
            <div class="stat-label">活动场次</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon-container">
            <el-icon class="stat-icon hours"><Timer /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">1,280</div>
            <div class="stat-label">服务总时长</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :lg="16">
        <el-card shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <span>近期活动</span>
              <el-button class="button" text>查看更多</el-button>
            </div>
          </template>
          <el-table :data="recentActivities" style="width: 100%">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="title" label="活动名称" />
            <el-table-column prop="location" label="地点" width="180" />
            <el-table-column prop="participants" label="参与人数" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.statusType">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <span>志愿者分布</span>
            </div>
          </template>
          <div class="chart-placeholder" style="height: 300px">
            <el-empty description="图表加载中..." />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :span="24">
        <el-card shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <span>待办事项</span>
              <el-button class="button" text>查看全部</el-button>
            </div>
          </template>
          <el-table :data="todoItems" style="width: 100%">
            <el-table-column prop="title" label="任务" />
            <el-table-column prop="deadline" label="截止日期" width="120" />
            <el-table-column prop="priority" label="优先级" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.priorityType">{{ scope.row.priority }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default>
                <el-button type="primary" size="small" plain>处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Document, User, Calendar, Timer } from '@element-plus/icons-vue'

const recentActivities = ref([
  {
    date: '2023-04-15',
    title: '社区清洁活动',
    location: '城市中心公园',
    participants: 25,
    status: '已完成',
    statusType: 'success',
  },
  {
    date: '2023-04-20',
    title: '敬老院慰问',
    location: '阳光敬老院',
    participants: 15,
    status: '已完成',
    statusType: 'success',
  },
  {
    date: '2023-04-25',
    title: '植树活动',
    location: '城市郊区林场',
    participants: 30,
    status: '进行中',
    statusType: 'warning',
  },
  {
    date: '2023-05-01',
    title: '五一公益跑',
    location: '滨江公园',
    participants: 50,
    status: '未开始',
    statusType: 'info',
  },
])

const todoItems = ref([
  {
    title: '审核新志愿者申请',
    deadline: '2023-04-30',
    priority: '高',
    priorityType: 'danger',
  },
  {
    title: '准备五一活动物资',
    deadline: '2023-04-28',
    priority: '中',
    priorityType: 'warning',
  },
  {
    title: '更新项目进展报告',
    deadline: '2023-05-05',
    priority: '低',
    priorityType: 'info',
  },
])
</script>

<style scoped>
.organization-home {
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

.stat-card {
  margin-bottom: 20px;
  height: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 20px;
}

.stat-icon-container {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 15px;
}

.stat-icon {
  font-size: 30px;
  color: white;
}

.stat-icon.projects {
  background-color: #409eff;
}

.stat-icon.volunteers {
  background-color: #67c23a;
}

.stat-icon.activities {
  background-color: #e6a23c;
}

.stat-icon.hours {
  background-color: #f56c6c;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.content-row {
  margin-bottom: 20px;
}

.content-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
