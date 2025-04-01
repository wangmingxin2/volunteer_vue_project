<template>
  <div class="admin-dashboard">
    <el-card v-loading="loading">
      <template #header>
        <div class="dashboard-header">
          <h2 class="dashboard-title">管理员仪表盘</h2>
          <el-button type="primary" size="small" @click="refreshData">刷新数据</el-button>
        </div>
      </template>

      <!-- 顶部数据卡片 -->
      <el-row :gutter="20" class="data-overview">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="data-card">
            <div class="card-content">
              <el-icon class="card-icon user-icon"><User /></el-icon>
              <div class="card-info">
                <div class="card-title">用户总数</div>
                <div class="card-value">{{ statsData.totalUsers || 0 }}</div>
              </div>
            </div>
            <div class="card-footer">
              <span>志愿者: {{ statsData.totalVolunteers || 0 }}</span>
              <span>组织: {{ statsData.totalOrganizations || 0 }}</span>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="data-card">
            <div class="card-content">
              <el-icon class="card-icon activity-icon"><Calendar /></el-icon>
              <div class="card-info">
                <div class="card-title">活动总数</div>
                <div class="card-value">{{ statsData.totalActivities || 0 }}</div>
              </div>
            </div>
            <div class="card-footer">
              <span>进行中: {{ statsData.ongoingActivities || 0 }}</span>
              <span>已结束: {{ statsData.completedActivities || 0 }}</span>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="data-card">
            <div class="card-content">
              <el-icon class="card-icon signup-icon"><Document /></el-icon>
              <div class="card-info">
                <div class="card-title">报名总数</div>
                <div class="card-value">{{ statsData.totalSignups || 0 }}</div>
              </div>
            </div>
            <div class="card-footer">
              <span>通过: {{ statsData.approvedSignups || 0 }}</span>
              <span>待审核: {{ statsData.pendingSignups || 0 }}</span>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="data-card">
            <div class="card-content">
              <el-icon class="card-icon reflection-icon"><ChatLineSquare /></el-icon>
              <div class="card-info">
                <div class="card-title">心得总数</div>
                <div class="card-value">{{ statsData.totalReflections || 0 }}</div>
              </div>
            </div>
            <div class="card-footer">
              <span>本周新增: {{ statsData.weeklyNewReflections || 0 }}</span>
              <span>本月新增: {{ statsData.monthlyNewReflections || 0 }}</span>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 报名趋势图表 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="24">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>最近30天活动报名趋势</span>
              </div>
            </template>
            <div class="chart-container" ref="trendChartRef" style="min-height: 300px">
              <el-empty
                v-if="!loading && (!signupTrendData.dates || signupTrendData.dates.length === 0)"
                description="暂无报名趋势数据"
                :image-size="100"
              >
              </el-empty>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 最新数据列表 -->
      <el-row :gutter="20" class="data-lists">
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="list-card">
            <template #header>
              <div class="list-header">
                <span>最新注册用户</span>
                <el-button text @click="router.push('/admin/users')">查看全部</el-button>
              </div>
            </template>
            <el-table :data="latestUsers" style="width: 100%" size="small">
              <el-table-column prop="username" label="用户名" width="120"></el-table-column>
              <el-table-column prop="realName" label="真实姓名" width="120"></el-table-column>
              <el-table-column prop="typeIdText" label="用户类型" width="120">
                <template #default="scope">
                  <el-tag
                    size="small"
                    :type="
                      scope.row.typeId === '3'
                        ? 'success'
                        : scope.row.typeId === '2'
                          ? 'warning'
                          : 'primary'
                    "
                  >
                    {{
                      scope.row.typeId === '1'
                        ? '超级管理员'
                        : scope.row.typeId === '2'
                          ? '组织管理员'
                          : '志愿者'
                    }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTimeFormatted" label="注册时间">
                <template #default="scope">
                  {{ scope.row.createTimeFormatted || '无数据' }}
                </template>
              </el-table-column>
            </el-table>
            <el-empty
              v-if="!latestUsers || latestUsers.length === 0"
              description="暂无数据"
              :image-size="60"
            ></el-empty>
          </el-card>
        </el-col>

        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="list-card">
            <template #header>
              <div class="list-header">
                <span>最新活动</span>
                <el-button text @click="router.push('/admin/activities')">查看全部</el-button>
              </div>
            </template>
            <el-table :data="latestActivities" style="width: 100%" size="small">
              <el-table-column prop="activityName" label="活动名称" width="180"></el-table-column>
              <el-table-column prop="orgName" label="发起组织" width="120">
                <template #default="scope">
                  {{ scope.row.orgName || scope.row.orgId || '未知' }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getStatusType(scope.row.status)" size="small">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTimeFormatted" label="创建时间">
                <template #default="scope">
                  {{ scope.row.createTimeFormatted || '无数据' }}
                </template>
              </el-table-column>
            </el-table>
            <el-empty
              v-if="!latestActivities || latestActivities.length === 0"
              description="暂无数据"
              :image-size="60"
            ></el-empty>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Calendar, Document, ChatLineSquare } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboardData } from '../../api/dashboard'
import { formatDate } from '../../utils/format'
import { ElMessage } from 'element-plus'
import { getUserList } from '../../api/user'
import { getActivityPage } from '../../api/activity'
import { getSignupPage } from '../../api/signup'

const router = useRouter()
const loading = ref(true)
const statsData = ref<any>({})
const latestUsers = ref<any[]>([])
const latestActivities = ref<any[]>([])
const signupTrendData = ref<any>({ dates: [], counts: [] })
const trendChartRef = ref()
let trendChart: echarts.ECharts | null = null

// 初始化仪表盘数据
const initDashboard = async () => {
  loading.value = true
  try {
    // 1. 获取基础统计数据
    const res = await getDashboardData()
    if (res.code === 200) {
      statsData.value = res.data
    } else {
      ElMessage.error(res.message || '获取仪表盘数据失败')
    }

    // 2. 获取最新用户数据
    const userRes = await getUserList({
      page: 1,
      size: 5,
      sort: 'createdTime,desc',
    })

    if (userRes.code === 200) {
      latestUsers.value = userRes.data.records || []
      // 确保日期格式化
      latestUsers.value.forEach((user) => {
        if (user.createdTime) {
          user.createTimeFormatted = formatDate(new Date(user.createdTime))
        }
      })
    }

    // 3. 获取最新活动数据
    const activityRes = await getActivityPage(1, 5, undefined, undefined, undefined)

    if (activityRes.code === 200) {
      latestActivities.value = activityRes.data.records || []
      // 按创建时间倒序排序
      latestActivities.value.sort(
        (a, b) => new Date(b.createdTime || 0).getTime() - new Date(a.createdTime || 0).getTime(),
      )
      // 只取前5条
      latestActivities.value = latestActivities.value.slice(0, 5)
      // 确保日期格式化和组织信息
      latestActivities.value.forEach((activity) => {
        if (activity.createdTime) {
          activity.createTimeFormatted = formatDate(new Date(activity.createdTime))
        }
      })
    }

    // 4. 获取报名数据，用于生成趋势图
    const signupRes = await getSignupPage({
      page: 1,
      size: 1000, // 获取足够多的数据来生成趋势
      sort: 'createTime,desc',
    })

    if (signupRes.code === 200) {
      processSignupTrendData(signupRes.data.records || [])
    }

    // 5. 延迟初始化图表，确保DOM已挂载
    setTimeout(() => {
      if (signupTrendData.value.dates.length > 0) {
        initTrendChart()
      }
    }, 100)
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理报名数据，生成趋势图所需的数据格式 - 完全重写此函数
const processSignupTrendData = (signups: any[]) => {
  // 准备数据结构
  const dateMap: Record<string, number> = {}
  const dates: string[] = []
  const counts: number[] = []

  // 获取过去30天的日期范围
  const today = new Date()
  const dateStrings: string[] = []

  // 先填充过去30天的日期和初始计数
  for (let i = 29; i >= 0; i--) {
    const date = new Date()
    date.setDate(today.getDate() - i)

    // 创建一个格式化的日期字符串 YYYY-MM-DD 格式
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const dateStr = `${year}-${month}-${day}`

    dateStrings.push(dateStr)
    dateMap[dateStr] = 0
  }

  // 统计每天的报名数量
  signups.forEach((signup) => {
    if (signup.createdTime) {
      // 从时间戳创建日期对象
      const signupDate = new Date(signup.createdTime)
      // 手动格式化为YYYY-MM-DD
      const year = signupDate.getFullYear()
      const month = String(signupDate.getMonth() + 1).padStart(2, '0')
      const day = String(signupDate.getDate()).padStart(2, '0')
      const dateStr = `${year}-${month}-${day}`

      if (dateMap[dateStr] !== undefined) {
        dateMap[dateStr]++
      }
    }
  })

  // 将日期和对应计数添加到数组
  dateStrings.forEach((dateStr) => {
    dates.push(dateStr)
    counts.push(dateMap[dateStr])
  })

  // 更新图表数据
  signupTrendData.value = {
    dates,
    counts,
  }
}

// 修改图表初始化代码
const initTrendChart = () => {
  // 确保图表容器已存在且已挂载
  if (!trendChartRef.value) {
    console.warn('图表容器尚未挂载')
    return
  }

  try {
    // 如果图表实例已存在，先销毁
    if (trendChart) {
      trendChart.dispose()
    }

    // 重新初始化图表
    trendChart = echarts.init(trendChartRef.value)

    // 确保日期格式正确
    console.log('X轴日期数据:', signupTrendData.value.dates)

    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
          label: {
            backgroundColor: '#6a7985',
          },
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: [
        {
          type: 'category',
          boundaryGap: false, // 折线图不需要留白
          data: signupTrendData.value.dates || [],
          axisTick: {
            alignWithLabel: true,
          },
          axisLabel: {
            rotate: 45,
            interval: 'auto',
            // 更新格式化函数，简单返回日期字符串
            formatter: function (value) {
              // 确认日期数据已正确格式化
              return value
            },
          },
        },
      ],
      yAxis: [
        {
          type: 'value',
          splitLine: {
            lineStyle: {
              type: 'dashed',
            },
          },
        },
      ],
      series: [
        {
          name: '报名人数',
          type: 'line', // 改为折线图
          smooth: true, // 平滑曲线
          symbolSize: 6, // 数据点大小
          itemStyle: {
            color: '#409EFF', // 主色调
          },
          lineStyle: {
            width: 3, // 线条宽度
          },
          areaStyle: {
            // 区域填充渐变
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: 'rgba(64, 158, 255, 0.5)',
              },
              {
                offset: 1,
                color: 'rgba(64, 158, 255, 0.1)',
              },
            ]),
          },
          data: signupTrendData.value.counts || [],
        },
      ],
    }

    trendChart.setOption(option)
  } catch (error) {
    console.error('初始化图表失败:', error)
  }
}

// 刷新数据
const refreshData = () => {
  initDashboard()
}

// 根据活动状态获取对应的标签类型
const getStatusType = (status: number) => {
  switch (status) {
    case 0:
      return 'info' // 未开始
    case 1:
      return 'success' // 进行中
    case 2:
      return 'primary' // 已结束
    case 3:
      return 'danger' // 已取消
    default:
      return 'info'
  }
}

// 根据活动状态获取对应的文本
const getStatusText = (status: number) => {
  switch (status) {
    case 0:
      return '未开始'
    case 1:
      return '进行中'
    case 2:
      return '已结束'
    case 3:
      return '已取消'
    default:
      return '未知'
  }
}

// 使用自定义日期格式化函数或导入的函数
const formatLocalDate = (date: Date, format = 'yyyy-MM-dd') => {
  if (!date) return ''

  try {
    return formatDate(date, format)
  } catch (e) {
    return date.toISOString().split('T')[0] // 备用格式化
  }
}

// 组件销毁时清理图表实例
onUnmounted(() => {
  if (trendChart) {
    trendChart.dispose()
    trendChart = null
  }
  // 移除窗口大小变化事件监听
  window.removeEventListener('resize', handleResize)
})

// 单独定义resize处理函数，便于移除事件监听
const handleResize = () => {
  if (trendChart) {
    trendChart.resize()
  }
}

// 使用正确的方式添加窗口大小变化事件监听
onMounted(() => {
  window.addEventListener('resize', handleResize)
  initDashboard()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 0;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-title {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.data-overview {
  margin-bottom: 20px;
}

.data-card {
  height: 120px;
  margin-bottom: 15px;
}

.card-content {
  display: flex;
  align-items: center;
}

.card-icon {
  font-size: 48px;
  margin-right: 15px;
  padding: 10px;
  border-radius: 8px;
}

.user-icon {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.activity-icon {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.signup-icon {
  background-color: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.reflection-icon {
  background-color: rgba(144, 147, 153, 0.1);
  color: #909399;
}

.card-info {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
  font-size: 12px;
  color: #909399;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  margin-bottom: 15px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
}

.chart-placeholder {
  width: 100%;
  height: 100%;
}

.data-lists {
  margin-bottom: 20px;
}

.list-card {
  margin-bottom: 15px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-table) {
  margin-top: 10px;
}

:deep(.el-table .cell) {
  padding: 8px 0;
}

@media (max-width: 768px) {
  .card-icon {
    font-size: 36px;
    padding: 8px;
  }

  .card-value {
    font-size: 20px;
  }

  .chart-container {
    height: 250px;
  }
}
</style>
