<template>
  <div class="message-management">
    <el-card class="filter-card">
      <div class="filter-container">
        <el-form :inline="true" :model="queryParams" class="filter-form">
          <el-form-item label="消息状态">
            <el-select
              v-model="queryParams.isRead"
              placeholder="请选择消息状态"
              clearable
              style="width: 200px"
            >
              <el-option label="未读" :value="0" />
              <el-option label="已读" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>我的消息</span>
          <div class="header-operations">
            <el-button type="primary" size="small" @click="handleAllRead"> 全部已读 </el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="messageList" border>
        <el-table-column label="消息ID" prop="notificationId" width="80" align="center" />
        <el-table-column label="标题" prop="title" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="message-title" :class="{ unread: row.isRead === 0 }">
              <el-badge v-if="row.isRead === 0" is-dot />
              {{ row.title }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="内容" prop="content" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isRead === 0" type="info">未读</el-tag>
            <el-tag v-else-if="row.isRead === 1" type="success">已读</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="接收时间" width="150" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createdTime, 'YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" icon="View" @click="handleView(row)"
              >查看</el-button
            >
            <el-button type="primary" link size="small" icon="Delete" @click="handleDelete(row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 查看消息对话框 -->
    <el-dialog v-model="viewDialogVisible" title="查看消息" width="600px">
      <div class="message-detail">
        <h2 class="message-detail-title">{{ currentMessage.title }}</h2>
        <div class="message-detail-meta">
          <span>发送时间：{{ formatDate(currentMessage.createdTime, 'YYYY-MM-DD HH:mm:ss') }}</span>
          <span
            >状态：
            <el-tag v-if="currentMessage.isRead === 0" type="info" size="small">未读</el-tag>
            <el-tag v-else-if="currentMessage.isRead === 1" type="success" size="small"
              >已读</el-tag
            >
          </span>
        </div>
        <div class="message-detail-content">
          <p>{{ currentMessage.content }}</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, Delete } from '@element-plus/icons-vue'
import { formatDate } from '../../utils/format'
import {
  getMessagePage,
  deleteMessage,
  markAllAsRead,
  markMessageAsRead,
  type MessageItem,
} from '../../api/message'

// 状态和数据
const loading = ref(false)
const total = ref(0)
const messageList = ref<MessageItem[]>([])
const viewDialogVisible = ref(false)
const currentMessage = ref<MessageItem>({} as MessageItem)

// 从本地存储获取用户ID
const userId = localStorage.getItem('userId') || ''

// 查询参数
const queryParams = reactive({
  userId,
  isRead: undefined as number | undefined,
  page: 1,
  size: 10,
})

// 方法
const handleQuery = () => {
  getList()
}

const resetQuery = () => {
  queryParams.isRead = undefined
  queryParams.page = 1
  getList()
}

const handleSizeChange = (size: number) => {
  queryParams.size = size
  getList()
}

const handleCurrentChange = (page: number) => {
  queryParams.page = page
  getList()
}

const handleView = async (row: MessageItem) => {
  currentMessage.value = row
  viewDialogVisible.value = true

  // 如果消息是未读状态，查看后自动标记为已读
  if (row.isRead === 0) {
    try {
      const res = await markMessageAsRead(row.notificationId)
      if (res.code === 200) {
        // 更新本地状态
        row.isRead = 1
        getList()
      }
    } catch (error) {
      console.error('自动标记已读失败:', error)
    }
  }
}

const handleAllRead = async () => {
  // 检查是否有未读消息
  const hasUnread = messageList.value.some((message) => message.isRead === 0)
  if (!hasUnread) {
    ElMessage.info('没有未读消息')
    return
  }

  // 添加确认对话框
  ElMessageBox.confirm('确定将所有未读消息标记为已读吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(async () => {
      try {
        const res = await markAllAsRead(userId)
        if (res.code === 200) {
          ElMessage.success('已将所有消息标记为已读')
          getList()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error) {
        console.error('全部标记已读失败:', error)
        ElMessage.error('操作失败')
      }
    })
    .catch(() => {
      // 用户取消操作
    })
}

const handleDelete = (row: MessageItem) => {
  ElMessageBox.confirm('确认删除该消息吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const res = await deleteMessage(row.notificationId)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          getList()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        console.error('删除消息失败:', error)
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {
      // 取消删除
    })
}

const getList = async () => {
  loading.value = true
  try {
    const params = {
      userId: queryParams.userId,
      isRead: queryParams.isRead,
      page: queryParams.page,
      size: queryParams.size,
    }

    const res = await getMessagePage(params)
    if (res.code === 200) {
      messageList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取消息列表失败')
    }
  } catch (error) {
    console.error('获取消息列表失败:', error)
    ElMessage.error('获取消息列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.message-management {
  padding: 10px;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-operations {
  display: flex;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.message-title {
  display: flex;
  align-items: center;
  gap: 5px;
}

.message-title.unread {
  font-weight: bold;
}

.message-detail {
  padding: 10px;
}

.message-detail-title {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 20px;
  color: #333;
}

.message-detail-meta {
  margin-bottom: 20px;
  color: #666;
  display: flex;
  justify-content: space-between;
}

.message-detail-content {
  white-space: pre-wrap;
  line-height: 1.6;
  background-color: #f8f8f8;
  padding: 15px;
  border-radius: 4px;
  min-height: 100px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
