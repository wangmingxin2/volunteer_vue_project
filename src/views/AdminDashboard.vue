<template>
  <div class="admin-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <div class="stat-title">总收入</div>
            <el-icon class="stat-icon money"><Money /></el-icon>
          </div>
          <div class="stat-value">¥ 126,560</div>
          <div class="stat-footer">
            <div class="stat-change up">
              <el-icon><CaretTop /></el-icon>
              <span>11.2%</span>
            </div>
            <div class="stat-period">较上月</div>
          </div>
          <div class="stat-chart">
            <el-progress :percentage="78" :show-text="false" stroke-width="3" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <div class="stat-title">志愿者数</div>
            <el-icon class="stat-icon users"><User /></el-icon>
          </div>
          <div class="stat-value">8,846</div>
          <div class="stat-footer">
            <div class="stat-change up">
              <el-icon><CaretTop /></el-icon>
              <span>5.6%</span>
            </div>
            <div class="stat-period">较上周</div>
          </div>
          <div class="stat-chart">
            <el-progress :percentage="65" :show-text="false" stroke-width="3" color="#67C23A" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <div class="stat-title">项目数</div>
            <el-icon class="stat-icon projects"><Document /></el-icon>
          </div>
          <div class="stat-value">6,560</div>
          <div class="stat-footer">
            <div class="stat-change down">
              <el-icon><CaretBottom /></el-icon>
              <span>2.3%</span>
            </div>
            <div class="stat-period">较上月</div>
          </div>
          <div class="stat-chart">
            <el-progress :percentage="45" :show-text="false" stroke-width="3" color="#E6A23C" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <div class="stat-title">完成率</div>
            <el-icon class="stat-icon rate"><PieChart /></el-icon>
          </div>
          <div class="stat-value">78%</div>
          <div class="stat-footer">
            <div class="stat-change up">
              <el-icon><CaretTop /></el-icon>
              <span>4.2%</span>
            </div>
            <div class="stat-period">较上周</div>
          </div>
          <div class="stat-chart">
            <el-progress :percentage="78" :show-text="false" stroke-width="3" color="#409EFF" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="chart-header">
              <div class="chart-title">月度统计</div>
              <div class="chart-actions">
                <el-radio-group v-model="chartType" size="small">
                  <el-radio-button label="week">周</el-radio-button>
                  <el-radio-button label="month">月</el-radio-button>
                  <el-radio-button label="year">年</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>
          <div class="chart-container" style="height: 300px">
            <!-- 这里放置图表组件 -->
            <div class="chart-placeholder">
              <el-empty description="图表加载中..." />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="data-row">
      <el-col :xs="24" :lg="16">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="data-header">
              <div class="data-title">最近活动</div>
              <el-button type="primary" size="small" plain>查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentActivities" style="width: 100%" :show-header="false">
            <el-table-column width="50">
              <template #default="scope">
                <el-avatar :size="32" :src="scope.row.avatar" />
              </template>
            </el-table-column>
            <el-table-column>
              <template #default="scope">
                <div class="activity-content">
                  <div class="activity-title">{{ scope.row.title }}</div>
                  <div class="activity-time">{{ scope.row.time }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column width="100" align="right">
              <template #default="scope">
                <el-tag :type="scope.row.tagType" size="small">{{ scope.row.tag }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="data-header">
              <div class="data-title">项目分类统计</div>
            </div>
          </template>
          <div class="pie-chart-container" style="height: 300px">
            <!-- 这里放置饼图组件 -->
            <div class="chart-placeholder">
              <el-empty description="图表加载中..." />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Money, User, Document, PieChart, CaretTop, CaretBottom } from '@element-plus/icons-vue'

const chartType = ref('month')

const recentActivities = [
  {
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    title: '张三完成了"社区清洁"项目',
    time: '2023-04-01 10:30',
    tag: '已完成',
    tagType: 'success',
  },
  {
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    title: '李四报名参加"关爱老人"项目',
    time: '2023-04-01 09:15',
    tag: '已报名',
    tagType: 'primary',
  },
  {
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    title: '王五取消了"环保宣传"项目',
    time: '2023-03-31 16:45',
    tag: '已取消',
    tagType: 'danger',
  },
  {
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    title: '赵六创建了"植树活动"项目',
    time: '2023-03-31 14:20',
    tag: '新项目',
    tagType: 'warning',
  },
  {
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    title: '志愿者协会发布了新公告',
    time: '2023-03-30 11:10',
    tag: '公告',
    tagType: 'info',
  },
]
</script>

<style scoped>
.admin-dashboard {
  padding: 0;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  height: 100%;
  position: relative;
  overflow: hidden;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.stat-icon {
  font-size: 24px;
  border-radius: 6px;
  padding: 8px;
  color: white;
}

.stat-icon.money {
  background-color: #409eff;
}

.stat-icon.users {
  background-color: #67c23a;
}

.stat-icon.projects {
  background-color: #e6a23c;
}

.stat-icon.rate {
  background-color: #f56c6c;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.stat-footer {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.stat-change {
  display: flex;
  align-items: center;
  margin-right: 10px;
  font-size: 14px;
}

.stat-change.up {
  color: #67c23a;
}

.stat-change.down {
  color: #f56c6c;
}

.stat-period {
  font-size: 12px;
  color: #909399;
}

.stat-chart {
  margin-top: 10px;
}

.chart-row,
.data-row {
  margin-bottom: 20px;
}

.chart-card,
.data-card {
  margin-bottom: 20px;
}

.chart-header,
.data-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title,
.data-title {
  font-size: 16px;
  font-weight: bold;
}

.chart-container,
.pie-chart-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.chart-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.activity-content {
  display: flex;
  flex-direction: column;
}

.activity-title {
  font-size: 14px;
  margin-bottom: 5px;
}

.activity-time {
  font-size: 12px;
  color: #909399;
}
</style>
