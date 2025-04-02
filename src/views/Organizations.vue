<template>
  <div class="organizations-container">
    <el-row class="page-header">
      <el-col :span="24">
        <h2>志愿服务组织</h2>
      </el-col>
    </el-row>

    <!-- 组织列表区域 -->
    <div class="organization-list" v-loading="loading">
      <!-- 无数据时显示空状态 -->
      <el-empty v-if="organizationList.length === 0 && !loading" description="暂无组织数据" />

      <!-- 组织卡片网格 -->
      <el-row :gutter="20">
        <el-col
          v-for="org in organizationList"
          :key="org.orgId"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          class="organization-col"
        >
          <el-card class="organization-card" shadow="hover">
            <div class="organization-logo">
              <el-avatar
                :size="80"
                :src="org.logo || 'http://60.204.157.137:9000/volunteer/logo-default.jpg'"
                class="logo-image"
              />
            </div>
            <div class="organization-info">
              <h3 class="organization-title">{{ org.orgName }}</h3>

              <div class="organization-data">
                <div class="data-item">
                  <span class="data-value">{{ org.count || 0 }}</span>
                  <span class="data-label">志愿者</span>
                </div>
              </div>

              <p class="organization-description">
                {{ org.description || '暂无描述' }}
              </p>

              <div class="organization-actions">
                <el-button type="primary" @click="viewOrganizationDetail(org)">查看详情</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 分页 -->
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[8, 16, 24, 32]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </div>

    <!-- 组织详情对话框 -->
    <el-dialog v-model="detailVisible" title="组织详情" width="700px" destroy-on-close>
      <div class="organization-detail-dialog" v-loading="detailLoading">
        <div class="org-header">
          <div class="org-avatar">
            <el-avatar
              :size="100"
              :src="currentOrg.logo || 'http://60.204.157.137:9000/volunteer/logo-default.jpg'"
            />
          </div>
          <h2 class="detail-title">{{ currentOrg.orgName }}</h2>
          <el-tag type="success">正常</el-tag>
        </div>

        <div class="detail-stats">
          <div class="stat-item">
            <span class="stat-value">{{ currentOrg.count || 0 }}</span>
            <span class="stat-label">志愿者</span>
          </div>
        </div>

        <div class="detail-item">
          <span class="detail-label">组织ID：</span>
          <span class="detail-value">{{ currentOrg.orgId }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">成立日期：</span>
          <span class="detail-value">{{ formatDate(currentOrg.establishmentDate) || '暂无' }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">联系人：</span>
          <span class="detail-value">{{ currentOrg.contactPerson || '暂无' }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">联系电话：</span>
          <span class="detail-value">{{ currentOrg.contactPhone || '暂无' }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">联系邮箱：</span>
          <span class="detail-value">{{ currentOrg.contactEmail || '暂无' }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">地址：</span>
          <span class="detail-value">{{ currentOrg.address || '暂无' }}</span>
        </div>

        <div class="detail-item description">
          <span class="detail-label">组织介绍：</span>
          <div class="detail-value description-content">
            {{ currentOrg.description || '暂无组织介绍' }}
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getOrganizationList, type Organization } from '../api/organization'
import { formatDate } from '../utils/format'

const router = useRouter()
const loading = ref(false)
const detailLoading = ref(false)
const detailVisible = ref(false)
const organizationList = ref<Organization[]>([])
const total = ref(0)
const currentOrg = ref<Organization>({} as Organization)

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 8,
  status: 1, // 默认查询状态正常的组织
})

// 在页面加载时获取组织列表
onMounted(() => {
  getList()
})

// 获取组织列表数据
const getList = async () => {
  loading.value = true
  try {
    const params = {
      page: queryParams.page,
      size: queryParams.pageSize,
      status: queryParams.status,
    }

    const response = await getOrganizationList(params)

    if (response.code === 200) {
      organizationList.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取组织列表失败')
    }
  } catch (error) {
    console.error('获取组织列表失败:', error)
    ElMessage.error('获取组织列表失败')
  } finally {
    loading.value = false
  }
}

// 查看组织详情
const viewOrganizationDetail = (org: Organization) => {
  currentOrg.value = JSON.parse(JSON.stringify(org))
  detailVisible.value = true
}

// 处理每页数量变化
const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  getList()
}

// 处理页码变化
const handleCurrentChange = (page: number) => {
  queryParams.page = page
  getList()
}
</script>

<style scoped>
.organizations-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  padding: 10px 0;
  color: #409eff;
}

.organization-list {
  margin-top: 20px;
}

.organization-col {
  margin-bottom: 20px;
}

.organization-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: all 0.3s;
  text-align: center;
  padding: 15px;
}

.organization-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.organization-logo {
  margin: 10px auto 20px;
}

.logo-image {
  border: 1px solid #eee;
}

.organization-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.organization-title {
  margin: 0 0 10px 0;
  font-size: 18px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.organization-data {
  display: flex;
  justify-content: center;
  margin: 15px 0;
  padding: 10px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.data-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.data-value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.data-label {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.organization-description {
  margin: 15px 0;
  color: #606266;
  text-align: left;
  height: 80px;
  overflow: auto;
  padding: 0 5px;
  line-height: 1.5;
}

.organization-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: auto;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* 详情对话框样式 */
.organization-detail-dialog {
  padding: 0 20px;
}

.org-header {
  text-align: center;
  margin-bottom: 20px;
}

.org-avatar {
  margin-bottom: 15px;
}

.detail-title {
  margin: 10px 0;
  font-size: 22px;
  color: #303133;
}

.detail-stats {
  display: flex;
  justify-content: center;
  margin: 20px 0;
  gap: 40px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.detail-item {
  margin-bottom: 15px;
  display: flex;
}

.detail-label {
  width: 100px;
  color: #606266;
  font-weight: bold;
}

.detail-value {
  flex: 1;
  color: #333;
}

.description {
  align-items: flex-start;
}

.description-content {
  white-space: pre-line;
  line-height: 1.6;
  max-height: 200px;
  overflow: auto;
  padding-right: 10px;
}

@media (max-width: 768px) {
  .organization-actions {
    flex-direction: column;
  }

  .detail-item {
    flex-direction: column;
  }

  .detail-label {
    width: 100%;
    margin-bottom: 5px;
  }
}
</style>
