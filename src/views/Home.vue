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
              <span class="announcement-title">{{ item.title }}</span>
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
            <h2>热门项目</h2>
          </div>
          <div class="placeholder-content">
            <!-- 这里可以放置其他组件 -->
            <p>这里可以放置热门项目、统计数据或其他内容</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { PictureFilled } from '@element-plus/icons-vue'

interface Banner {
  bannerId: number
  title: string
  imageUrl: string
  description: string | null
  bannerType: number
  status: number
  createdBy: string | null
  createdTime: string
  updatedBy: string | null
  updatedTime: string
  deleted: number
}

interface Announcement {
  announcementId: number
  title: string
  createdTime: string
  // ... 其他属性
}

const banners = ref<Banner[]>([])
const announcements = ref<Announcement[]>([])
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)

const fetchBanners = async () => {
  try {
    const response = await axios.get('http://localhost:8080/banner')
    if (response.data.code === 200) {
      banners.value = response.data.data
    }
  } catch (error) {
    console.error('获取轮播图数据失败:', error)
  }
}

const fetchAnnouncements = async (page: number) => {
  try {
    const response = await axios.get('http://localhost:8080/announcement/page', {
      params: {
        status: 0,
        page: page,
        size: pageSize.value,
      },
    })
    if (response.data.code === 200) {
      announcements.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取公告数据失败:', error)
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
}

.right-section {
  flex: 1;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-height: 400px;
}

.placeholder-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
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
</style>
