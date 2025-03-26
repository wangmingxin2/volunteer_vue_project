<template>
  <div class="organizations">
    <el-row class="page-header">
      <el-col :span="24">
        <h2>志愿服务组织</h2>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="org-list">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="i in 8" :key="i">
        <el-card class="org-card" shadow="hover">
          <div class="org-logo">
            <el-avatar
              :size="80"
              src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"
            />
          </div>
          <div class="org-info">
            <h3>志愿组织 {{ i }}</h3>
            <p class="org-description">这是一个志愿服务组织的简短介绍...</p>
            <div class="org-meta">
              <el-tag size="small">教育支持</el-tag>
              <el-tag size="small" type="success">环境保护</el-tag>
            </div>
            <div class="org-stats">
              <div class="stat-item">
                <span class="stat-value">{{ 100 + i * 10 }}</span>
                <span class="stat-label">志愿者</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ 20 + i * 2 }}</span>
                <span class="stat-label">项目</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ 1000 + i * 100 }}</span>
                <span class="stat-label">服务时长</span>
              </div>
            </div>
            <el-button type="primary" class="join-btn">加入组织</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-pagination
      class="pagination"
      :total="100"
      :page-size="20"
      :current-page="1"
      layout="total, prev, pager, next"
    />
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
.organizations {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  padding: 10px 0;
}

.org-list {
  margin-bottom: 20px;
}

.org-card {
  margin-bottom: 20px;
  text-align: center;
}

.org-logo {
  margin: 20px 0;
}

.org-info h3 {
  margin: 0 0 10px 0;
}

.org-description {
  color: #666;
  margin-bottom: 15px;
}

.org-meta {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-bottom: 15px;
}

.org-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 15px;
  padding: 10px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.join-btn {
  width: 80%;
  margin: 10px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
