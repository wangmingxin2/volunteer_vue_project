<template>
  <div class="home">
    <div class="main-content">
      <div class="carousel-section">
        <el-carousel height="400px">
          <el-carousel-item v-for="banner in banners" :key="banner.bannerId">
            <el-image :src="banner.imageUrl" fit="cover" class="carousel-image">
              <template #error>
                <div class="image-slot">
                  <el-icon><picture-filled /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="carousel-title">{{ banner.title }}</div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <div class="bottom-section">
        <div class="announcement-section">
          <div class="section-header">
            <h2>志愿动态</h2>
          </div>
          <ul class="announcement-list">
            <li v-for="item in announcements" :key="item.announcementId" class="announcement-item">
              <el-tooltip :content="item.title" placement="top" :show-after="500">
                <span class="announcement-title">{{ item.title }}</span>
              </el-tooltip>
              <span class="announcement-date">{{ formatDate(item.createdTime) }}</span>
            </li>
          </ul>
          <div class="pagination">
            <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next"
              @current-change="handlePageChange"
            />
          </div>
        </div>

        <div class="right-section">
          <div class="section-header">
            <h2>组织排行榜</h2>
          </div>
          <div class="org-ranking">
            <el-skeleton :rows="5" animated v-if="loading" />
            <ul class="org-list" v-else>
              <li v-for="(org, index) in topOrganizations" :key="org.orgId" class="org-item">
                <div class="org-rank">{{ index + 1 }}</div>
                <el-avatar :size="40" :src="org.logo" class="org-avatar" />
                <div class="org-info">
                  <div class="org-name">{{ org.orgName }}</div>
                  <div class="org-desc">{{ org.description }}</div>
                  <div class="org-count">志愿者: {{ org.count }}人</div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { PictureFilled } from '@element-plus/icons-vue'
import { getTopOrganizations } from '../api/organization'
import type { Organization } from '../api/organization'
import { getBanners } from '../api/banner'
import type { Banner } from '../api/banner'
import { getAnnouncementPage } from '../api/announcement'
import type { Announcement } from '../api/announcement'

const banners = ref<Banner[]>([])
const announcements = ref<Announcement[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const topOrganizations = ref<Organization[]>([])
const loading = ref(true)

const fetchBanners = async () => {
  try {
    const res = await getBanners()
    banners.value = res.data
  } catch (error) {
    console.error('获取轮播图数据失败:', error)
  }
}

const fetchAnnouncements = async (page: number) => {
  try {
    const res = await getAnnouncementPage(page, pageSize.value)
    announcements.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取公告数据失败:', error)
  }
}

const fetchTopOrganizations = async () => {
  try {
    loading.value = true
    const res = await getTopOrganizations()
    topOrganizations.value = res.data
  } catch (error) {
    console.error('获取组织排行榜失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchAnnouncements(page)
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

onMounted(() => {
  fetchBanners()
  fetchAnnouncements(1)
  fetchTopOrganizations()
})
</script>

<style scoped>
.main-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.carousel-section {
  width: 100%;
}

.bottom-section {
  display: flex;
  gap: 20px;
}

.announcement-section {
  flex: 1;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-height: 400px;
  display: flex;
  flex-direction: column;
  width: 0; /* 防止内容撑开容器 */
}

.right-section {
  flex: 1;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-height: 400px;
  width: 0; /* 防止内容撑开容器 */
}

.section-header {
  border-bottom: 2px solid #409eff;
  margin-bottom: 15px;
}

.section-header h2 {
  margin: 0;
  padding-bottom: 10px;
  font-size: 18px;
  color: #409eff;
}

.announcement-list {
  flex: 1;
  margin-bottom: 20px;
}

.announcement-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.announcement-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 15px;
  cursor: pointer;
}

.announcement-date {
  color: #999;
  font-size: 14px;
  white-space: nowrap;
}

.carousel-image {
  width: 100%;
  height: 100%;
}

.carousel-title {
  position: absolute;
  bottom: 20px;
  left: 20px;
  color: white;
  font-size: 24px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 30px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.org-ranking {
  padding: 10px 0;
}

.org-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.org-item {
  display: flex;
  align-items: flex-start;
  padding: 15px 10px;
  border-bottom: 1px solid #eee;
}

.org-rank {
  width: 24px;
  height: 24px;
  background-color: #f0f0f0;
  color: #666;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  margin-right: 15px;
  flex-shrink: 0;
}

.org-item:nth-child(1) .org-rank {
  background-color: #f5c518;
  color: white;
}

.org-item:nth-child(2) .org-rank {
  background-color: #c0c0c0;
  color: white;
}

.org-item:nth-child(3) .org-rank {
  background-color: #cd7f32;
  color: white;
}

.org-avatar {
  margin-right: 15px;
  flex-shrink: 0;
}

.org-info {
  flex: 1;
  min-width: 0; /* 防止内容撑开容器 */
}

.org-name {
  font-weight: bold;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.org-desc {
  color: #666;
  font-size: 14px;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.org-count {
  font-size: 14px;
  color: #999;
}
</style>
