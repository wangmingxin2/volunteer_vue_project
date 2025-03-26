<template>
  <div class="projects">
    <el-row class="page-header">
      <el-col :span="24">
        <h2>志愿服务项目</h2>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="project-list">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="i in 8" :key="i">
        <el-card class="project-card" shadow="hover">
          <img
            src="https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png"
            class="project-image"
          />
          <div class="project-info">
            <h3>示例项目 {{ i }}</h3>
            <p class="project-description">这是一个志愿服务项目的简短描述...</p>
            <div class="project-meta">
              <span>
                <el-icon><calendar /></el-icon>
                2024-03-26
              </span>
              <span>
                <el-icon><user /></el-icon>
                已报名: 10人
              </span>
            </div>
            <el-button type="primary" class="apply-btn">立即报名</el-button>
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

<script setup lang="ts">
import { Calendar, User } from '@element-plus/icons-vue'
</script>

<style scoped>
.projects {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  padding: 10px 0;
}

.project-list {
  margin-bottom: 20px;
}

.project-card {
  margin-bottom: 20px;
}

.project-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.project-info {
  padding: 10px 0;
}

.project-info h3 {
  margin: 0 0 10px 0;
}

.project-description {
  color: #666;
  margin-bottom: 10px;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  color: #999;
  margin-bottom: 15px;
}

.project-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.apply-btn {
  width: 100%;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
