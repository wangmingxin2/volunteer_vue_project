<template>
  <div class="signup-management">
    <el-card class="filter-card">
      <div class="filter-container">
        <el-form :inline="true" :model="queryParams" class="filter-form">
          <el-form-item label="活动ID">
            <el-input
              v-model="queryParams.activityId"
              placeholder="请输入活动ID"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select
              v-model="queryParams.auditStatus"
              placeholder="请选择审核状态"
              clearable
              style="width: 200px"
            >
              <el-option label="待审核" value="0" />
              <el-option label="已通过" value="1" />
              <el-option label="已拒绝" value="2" />
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
          <span>活动报名管理</span>
          <el-button type="primary" @click="handleAdd">新增报名</el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="signupList"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="报名ID" prop="signupId" width="80" align="center" />
        <el-table-column
          label="活动名称"
          prop="activityName"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column label="报名时间" width="150" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.signupTime, 'YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="紧急联系人" prop="emergencyContact" width="100" />
        <el-table-column label="紧急联系电话" prop="emergencyPhone" width="120" />
        <el-table-column label="审核状态" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.auditStatus === 0" type="info">待审核</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === 2" type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="服务时长" width="100" align="center">
          <template #default="scope">
            {{ scope.row.serviceHours || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="320">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="viewDetail(scope.row)">
              查看
            </el-button>

            <el-button
              v-if="scope.row.auditStatus === 0"
              size="small"
              type="success"
              plain
              @click="handleAudit(scope.row)"
            >
              审核
            </el-button>

            <el-button
              v-if="scope.row.auditStatus === 1"
              size="small"
              type="warning"
              plain
              @click="handleRecordHours(scope.row)"
            >
              记录时长
            </el-button>

            <el-button size="small" type="info" plain @click="handleEdit(scope.row)">
              修改
            </el-button>

            <el-button size="small" type="danger" plain @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="报名详情" width="600px">
      <div v-loading="detailLoading" class="detail-container">
        <div class="info-item">
          <span class="label">报名ID：</span>
          <span>{{ currentSignup.signupId }}</span>
        </div>
        <div class="info-item">
          <span class="label">活动名称：</span>
          <span>{{ currentSignup.activityName }}</span>
        </div>
        <div class="info-item">
          <span class="label">组织名称：</span>
          <span>{{ currentSignup.orgName }}</span>
        </div>
        <div class="info-item">
          <span class="label">紧急联系人：</span>
          <span>{{ currentSignup.emergencyContact || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">紧急联系电话：</span>
          <span>{{ currentSignup.emergencyPhone || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">报名时间：</span>
          <span>{{ formatDate(currentSignup.signupTime, 'YYYY-MM-DD HH:mm:ss') || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">审核状态：</span>
          <span>
            <el-tag v-if="currentSignup.auditStatus === 0" type="info">待审核</el-tag>
            <el-tag v-else-if="currentSignup.auditStatus === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="currentSignup.auditStatus === 2" type="danger">已拒绝</el-tag>
          </span>
        </div>
        <div class="info-item">
          <span class="label">审核时间：</span>
          <span>{{ formatDate(currentSignup.auditTime, 'YYYY-MM-DD HH:mm:ss') || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">审核人：</span>
          <span>{{ currentSignup.auditBy || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">审核备注：</span>
          <span>{{ currentSignup.auditRemark || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">签到时间：</span>
          <span>{{ formatDate(currentSignup.signinTime, 'YYYY-MM-DD HH:mm:ss') || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">签到位置：</span>
          <span>{{ currentSignup.signinLocation || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">签退时间：</span>
          <span>{{ formatDate(currentSignup.signoutTime, 'YYYY-MM-DD HH:mm:ss') || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">服务时长：</span>
          <span>{{ currentSignup.serviceHours || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">状态：</span>
          <span>
            <el-tag :type="currentSignup.status === 1 ? 'success' : 'danger'">
              {{ currentSignup.status === 1 ? '正常' : '已取消' }}
            </el-tag>
          </span>
        </div>
      </div>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog v-model="auditVisible" title="审核报名" width="500px">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.auditStatus">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input
            v-model="auditForm.auditRemark"
            type="textarea"
            rows="3"
            placeholder="请输入审核备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitAudit">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 记录服务时长对话框 -->
    <el-dialog v-model="hoursVisible" title="记录服务时长" width="500px">
      <el-form :model="hoursForm" ref="hoursFormRef" label-width="100px">
        <el-form-item label="服务时长" prop="serviceHours">
          <el-input-number
            v-model="hoursForm.serviceHours"
            :min="0"
            :precision="1"
            :step="0.5"
            controls-position="right"
          />
          <span class="unit">小时</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="hoursVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitHours">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="formVisible"
      :title="formType === 'add' ? '新增报名' : '修改报名'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="dialog-form">
        <el-form-item label="活动ID" prop="activityId">
          <el-input v-model="form.activityId" placeholder="请输入活动ID" />
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人" />
        </el-form-item>
        <el-form-item label="紧急联系电话" prop="emergencyPhone">
          <el-input v-model="form.emergencyPhone" placeholder="请输入紧急联系电话" />
        </el-form-item>
        <el-form-item label="审核状态" prop="auditStatus">
          <el-select v-model="form.auditStatus" placeholder="请选择审核状态" style="width: 100%">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核时间" prop="auditTime">
          <el-date-picker
            v-model="form.auditTime"
            type="datetime"
            placeholder="请选择审核时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="审核人" prop="auditBy">
          <el-input v-model="form.auditBy" placeholder="请输入审核人" />
        </el-form-item>
        <el-form-item label="签到时间" prop="signinTime">
          <el-date-picker
            v-model="form.signinTime"
            type="datetime"
            placeholder="请选择签到时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="签到位置" prop="signinLocation">
          <el-input v-model="form.signinLocation" placeholder="请输入签到位置" />
        </el-form-item>
        <el-form-item label="签退时间" prop="signoutTime">
          <el-date-picker
            v-model="form.signoutTime"
            type="datetime"
            placeholder="请选择签退时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="服务时长" prop="serviceHours">
          <el-input-number
            v-model="form.serviceHours"
            :min="0"
            :max="100"
            :precision="1"
            :step="0.5"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="已取消" :value="0" />
            <el-option label="正常" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="formVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getSignupPage,
  auditSignup,
  recordServiceHours,
  addSignup,
  updateSignup,
  deleteSignup,
} from '../../api/signup'
import { formatDate } from '../../utils/format'

// 获取当前组织ID
const orgId = localStorage.getItem('orgId') || ''

// 页面状态
const loading = ref(false)
const signupList = ref([])
const total = ref(0)
const selectedRows = ref([])

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  activityId: '',
  auditStatus: '',
})

// 详情对话框
const detailVisible = ref(false)
const detailLoading = ref(false)
const currentSignup = ref({})

// 审核对话框
const auditVisible = ref(false)
const auditForm = reactive({
  signupId: '',
  auditStatus: 1,
  auditRemark: '',
})

// 记录服务时长对话框
const hoursVisible = ref(false)
const hoursFormRef = ref(null)
const hoursForm = reactive({
  signupId: '',
  serviceHours: 0,
})

// 表单相关
const formVisible = ref(false)
const formType = ref('add')
const formRef = ref(null)
const submitLoading = ref(false)

const form = reactive({
  signupId: '',
  activityId: '',
  orgId: orgId, // 使用当前组织ID
  userId: '',
  emergencyContact: '',
  emergencyPhone: '',
  auditStatus: 0,
  auditTime: '',
  auditBy: '',
  auditRemark: '',
  signinTime: '',
  signinLocation: '',
  signoutTime: '',
  serviceHours: 0,
  status: 1,
})

const rules = {
  activityId: [{ required: true, message: '请输入活动ID', trigger: 'blur' }],
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  emergencyContact: [{ required: true, message: '请输入紧急联系人', trigger: 'blur' }],
  emergencyPhone: [{ required: true, message: '请输入紧急联系电话', trigger: 'blur' }],
}

// 获取报名列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: queryParams.page,
      pageSize: queryParams.size,
      activityId: queryParams.activityId || undefined,
      orgId: orgId, // 始终使用当前组织ID
      auditStatus: queryParams.auditStatus || undefined,
    }

    const res = await getSignupPage(params)
    if (res.code === 200) {
      signupList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取报名列表失败')
    }
  } catch (error) {
    console.error('获取报名列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询按钮
const handleQuery = () => {
  queryParams.page = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryParams.activityId = ''
  queryParams.auditStatus = ''
  queryParams.page = 1
  getList()
}

// 修改分页大小
const handleSizeChange = (size: number) => {
  queryParams.size = size
  getList()
}

// 修改当前页
const handleCurrentChange = (page: number) => {
  queryParams.page = page
  getList()
}

// 多选框选中数据
const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection
}

// 查看详情
const viewDetail = (row: any) => {
  currentSignup.value = row
  detailVisible.value = true
}

// 打开审核对话框
const handleAudit = (row: any) => {
  auditForm.signupId = row.signupId
  auditForm.auditStatus = 1
  auditForm.auditRemark = ''
  auditVisible.value = true
}

// 提交审核
const submitAudit = async () => {
  submitLoading.value = true
  try {
    const res = await auditSignup(
      auditForm.signupId.toString(),
      auditForm.auditStatus,
      auditForm.auditRemark,
    )
    if (res.code === 200) {
      ElMessage.success('审核成功')
      auditVisible.value = false
      getList()
    } else {
      ElMessage.error(res.message || '审核失败')
    }
  } catch (error) {
    console.error('审核失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 打开记录服务时长对话框
const handleRecordHours = (row: any) => {
  hoursForm.signupId = row.signupId
  hoursForm.serviceHours = row.serviceHours || 0
  hoursVisible.value = true
}

// 提交服务时长
const submitHours = async () => {
  submitLoading.value = true
  try {
    const res = await recordServiceHours(hoursForm.signupId.toString(), hoursForm.serviceHours)
    if (res.code === 200) {
      ElMessage.success('记录服务时长成功')
      hoursVisible.value = false
      getList()
    } else {
      ElMessage.error(res.message || '记录服务时长失败')
    }
  } catch (error) {
    console.error('记录服务时长失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 打开新增对话框
const handleAdd = () => {
  formType.value = 'add'
  resetForm()
  form.orgId = orgId // 确保组织ID正确
  formVisible.value = true
}

// 打开编辑对话框
const handleEdit = (row: any) => {
  formType.value = 'edit'
  resetForm()
  // 填充表单数据
  Object.keys(form).forEach((key) => {
    if (row[key] !== undefined) {
      form[key] = row[key]
    }
  })
  form.orgId = orgId // 确保组织ID正确
  formVisible.value = true
}

// 重置表单
const resetForm = () => {
  form.signupId = ''
  form.activityId = ''
  form.orgId = orgId // 设置为当前组织ID
  form.userId = ''
  form.emergencyContact = ''
  form.emergencyPhone = ''
  form.auditStatus = 0
  form.auditTime = ''
  form.auditBy = ''
  form.auditRemark = ''
  form.signinTime = ''
  form.signinLocation = ''
  form.signoutTime = ''
  form.serviceHours = 0
  form.status = 1

  // 如果formRef已经挂载，则重置校验结果
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    submitLoading.value = true
    try {
      // 确保组织ID正确
      form.orgId = orgId

      let res
      if (formType.value === 'add') {
        // 新增
        res = await addSignup(form)
      } else {
        // 编辑
        res = await updateSignup(form.signupId.toString(), form)
      }

      if (res.code === 200) {
        ElMessage.success(formType.value === 'add' ? '新增成功' : '修改成功')
        formVisible.value = false
        getList()
      } else {
        ElMessage.error(res.message || (formType.value === 'add' ? '新增失败' : '修改失败'))
      }
    } catch (error) {
      console.error(formType.value === 'add' ? '新增报名失败:' : '修改报名失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 删除报名
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该报名记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const res = await deleteSignup(row.signupId.toString())
        if (res.code === 200) {
          ElMessage.success('删除成功')
          getList()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        console.error('删除报名失败:', error)
      }
    })
    .catch(() => {
      // 取消删除
    })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.signup-management {
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-container {
  max-height: 60vh;
  overflow-y: auto;
  padding: 10px;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
}

.label {
  font-weight: bold;
  width: 120px;
  flex-shrink: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.unit {
  margin-left: 10px;
  color: #606266;
}

.dialog-form {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;
}
</style>
