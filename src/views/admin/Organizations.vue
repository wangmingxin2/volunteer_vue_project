<template>
  <div class="admin-organizations">
    <el-card class="filter-card">
      <div class="filter-container">
        <el-form :inline="true" :model="queryParams" class="filter-form">
          <el-form-item label="组织名称">
            <el-input
              v-model="queryParams.name"
              placeholder="请输入组织名称"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="queryParams.status"
              placeholder="请选择状态"
              clearable
              style="width: 180px"
            >
              <el-option label="正常" value="1" />
              <el-option label="禁用" value="0" />
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
          <span>组织列表</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd">新增组织</el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="organizationList"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="组织ID" prop="orgId" width="80" align="center" />
        <el-table-column label="Logo" width="80" align="center">
          <template #default="{ row }">
            <el-avatar
              :size="40"
              :src="row.logo || defaultLogo"
              @click="previewLogo(row.logo)"
              class="clickable-avatar"
            />
          </template>
        </el-table-column>
        <el-table-column label="组织名称" prop="orgName" min-width="120" />
        <el-table-column label="联系人" prop="contactPerson" min-width="100">
          <template #default="{ row }">
            {{ row.contactPerson || '未设置' }}
          </template>
        </el-table-column>
        <el-table-column label="联系电话" prop="contactPhone" min-width="120">
          <template #default="{ row }">
            {{ row.contactPhone || '未设置' }}
          </template>
        </el-table-column>
        <el-table-column label="志愿者数量" prop="count" width="100" align="center" />
        <el-table-column label="状态" prop="status" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createdTime" min-width="160" />
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑组织对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '新增组织' : '编辑组织'"
      v-model="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="orgFormRef" :model="orgForm" :rules="rules" label-width="100px">
        <el-form-item label="Logo" prop="logo" class="avatar-upload-item">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:8080/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleLogoSuccess"
            :before-upload="beforeLogoUpload"
          >
            <img v-if="orgForm.logo" :src="orgForm.logo" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="avatar-tip">点击上传Logo</div>
        </el-form-item>
        <el-form-item label="组织名称" prop="orgName">
          <el-input v-model="orgForm.orgName" placeholder="请输入组织名称" />
        </el-form-item>
        <el-form-item label="组织描述" prop="description">
          <el-input
            v-model="orgForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入组织描述"
          />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="orgForm.contactPerson" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="orgForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系邮箱" prop="contactEmail">
          <el-input v-model="orgForm.contactEmail" placeholder="请输入联系邮箱" />
        </el-form-item>
        <el-form-item label="组织地址" prop="address">
          <el-input v-model="orgForm.address" placeholder="请输入组织地址" />
        </el-form-item>
        <el-form-item label="成立日期" prop="establishmentDate">
          <el-date-picker
            v-model="orgForm.establishmentDate"
            type="date"
            placeholder="选择成立日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="orgForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- Logo预览对话框 -->
    <el-dialog v-model="logoPreviewVisible" title="Logo预览" width="400px" center>
      <div class="avatar-preview-container">
        <img :src="previewImageUrl" class="avatar-preview" />
      </div>
    </el-dialog>

    <!-- 组织详情对话框 -->
    <el-dialog v-model="detailVisible" title="组织详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="组织ID">{{ detailInfo.orgId }}</el-descriptions-item>
        <el-descriptions-item label="组织名称">{{ detailInfo.orgName }}</el-descriptions-item>
        <el-descriptions-item label="Logo" :span="2">
          <el-image
            style="width: 100px; height: 100px"
            :src="detailInfo.logo || defaultLogo"
            :preview-src-list="detailInfo.logo ? [detailInfo.logo] : []"
            fit="cover"
          />
        </el-descriptions-item>
        <el-descriptions-item label="组织描述" :span="2">
          {{ detailInfo.description || '暂无描述' }}
        </el-descriptions-item>
        <el-descriptions-item label="联系人">
          {{ detailInfo.contactPerson || '未设置' }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ detailInfo.contactPhone || '未设置' }}
        </el-descriptions-item>
        <el-descriptions-item label="联系邮箱">
          {{ detailInfo.contactEmail || '未设置' }}
        </el-descriptions-item>
        <el-descriptions-item label="组织地址">
          {{ detailInfo.address || '未设置' }}
        </el-descriptions-item>
        <el-descriptions-item label="成立日期">
          {{ formatDate(detailInfo.establishmentDate) }}
        </el-descriptions-item>
        <el-descriptions-item label="志愿者数量">
          {{ detailInfo.count || 0 }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailInfo.status === 1 ? 'success' : 'danger'">
            {{ detailInfo.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ detailInfo.createdTime }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ detailInfo.updatedTime }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, UploadProps } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getOrganizationList,
  updateOrganizationStatus,
  addOrganization,
  updateOrganization,
  deleteOrganization,
} from '../../api/organization'
import type { Organization } from '../../api/organization'

// 默认Logo
const defaultLogo = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 查询参数
const queryParams = reactive({
  status: '',
  name: '',
  page: 1,
  size: 5,
})

// 组织列表数据
const organizationList = ref<Organization[]>([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref<string[]>([])

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const orgFormRef = ref<FormInstance>()
const submitLoading = ref(false)

// Logo预览
const logoPreviewVisible = ref(false)
const previewImageUrl = ref('')

// 组织详情
const detailVisible = ref(false)
const detailInfo = ref<Organization>({} as Organization)

// 组织表单数据
const orgForm = reactive({
  orgId: '',
  managerId: '',
  orgName: '',
  description: '',
  logo: '',
  contactPerson: '',
  contactPhone: '',
  contactEmail: '',
  address: '',
  establishmentDate: '',
  status: 1,
})

// 表单验证规则
const rules = {
  orgName: [
    { required: true, message: '请输入组织名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' },
  ],
  contactPerson: [{ max: 20, message: '长度不能超过20个字符', trigger: 'blur' }],
  contactPhone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  contactEmail: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
}

// 添加上传Logo所需的请求头
const uploadHeaders = {
  satoken: localStorage.getItem('satoken') || '',
}

// 获取组织列表
const getList = async () => {
  try {
    loading.value = true
    const params = {
      status: queryParams.status || undefined,
      name: queryParams.name || undefined,
      page: queryParams.page,
      size: queryParams.size,
    }

    const res = await getOrganizationList(params)
    if (res.code === 200) {
      organizationList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取组织列表失败')
    }
  } catch (error) {
    console.error('获取组织列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询按钮点击事件
const handleQuery = () => {
  queryParams.page = 1
  getList()
}

// 重置查询条件
const resetQuery = () => {
  queryParams.name = ''
  queryParams.status = ''
  queryParams.page = 1
  getList()
}

// 表格选择项变化
const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map((item) => item.orgId)
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

// 新增组织
const handleAdd = () => {
  dialogType.value = 'add'
  orgForm.orgId = ''
  orgForm.managerId = ''
  orgForm.orgName = ''
  orgForm.description = ''
  orgForm.logo = ''
  orgForm.contactPerson = ''
  orgForm.contactPhone = ''
  orgForm.contactEmail = ''
  orgForm.address = ''
  orgForm.establishmentDate = ''
  orgForm.status = 1
  dialogVisible.value = true
}

// 编辑组织
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  orgForm.orgId = row.orgId
  orgForm.managerId = row.managerId
  orgForm.orgName = row.orgName
  orgForm.description = row.description || ''
  orgForm.logo = row.logo || ''
  orgForm.contactPerson = row.contactPerson || ''
  orgForm.contactPhone = row.contactPhone || ''
  orgForm.contactEmail = row.contactEmail || ''
  orgForm.address = row.address || ''
  orgForm.establishmentDate = row.establishmentDate || ''
  orgForm.status = row.status
  dialogVisible.value = true
}

// 查看组织详情
const handleView = (row: any) => {
  detailInfo.value = { ...row }
  detailVisible.value = true
}

// Logo上传前的校验
const beforeLogoUpload: UploadProps['beforeUpload'] = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('上传Logo只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传Logo大小不能超过 2MB!')
    return false
  }
  return true
}

// Logo上传成功的回调
const handleLogoSuccess = (res: any) => {
  if (res.code === 200) {
    orgForm.logo = res.data
    ElMessage.success('Logo上传成功')
  } else {
    ElMessage.error(res.message || 'Logo上传失败')
  }
}

// 预览Logo
const previewLogo = (url: string) => {
  if (!url) return
  previewImageUrl.value = url
  logoPreviewVisible.value = true
}

// 格式化日期
const formatDate = (date: string | null | undefined) => {
  if (!date) return '未设置'
  return new Date(date).toLocaleDateString()
}

// 提交表单
const submitForm = async () => {
  if (!orgFormRef.value) return

  try {
    await orgFormRef.value.validate()
    submitLoading.value = true

    const formData = { ...orgForm }

    if (dialogType.value === 'add') {
      // 新增组织
      const res = await addOrganization(formData)
      if (res.code === 200) {
        ElMessage.success('新增组织成功')
        dialogVisible.value = false
        getList()
      } else {
        ElMessage.error(res.message || '新增组织失败')
      }
    } else {
      // 编辑组织
      const res = await updateOrganization(formData)
      if (res.code === 200) {
        ElMessage.success('编辑组织成功')
        dialogVisible.value = false
        getList()
      } else {
        ElMessage.error(res.message || '编辑组织失败')
      }
    }
    submitLoading.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
    submitLoading.value = false
  }
}

// 删除组织
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除组织 ${row.orgName} 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const res = await deleteOrganization(row.orgId)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          getList()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        console.error('删除组织失败:', error)
      }
    })
    .catch(() => {
      // 取消删除
    })
}

// 修改组织状态
const handleStatusChange = async (row: any) => {
  try {
    const res = await updateOrganizationStatus(row.orgId, row.status)
    if (res.code === 200) {
      ElMessage.success(`${row.status === 1 ? '启用' : '禁用'}成功`)
    } else {
      // 如果失败，回滚状态
      row.status = row.status === 1 ? 0 : 1
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    // 如果出错，回滚状态
    row.status = row.status === 1 ? 0 : 1
    console.error('修改组织状态失败:', error)
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.admin-organizations {
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

.header-buttons {
  display: flex;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.avatar-upload-item {
  display: flex;
  justify-content: center;
}

.avatar-uploader {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  text-align: center;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.avatar-tip {
  font-size: 12px;
  color: #606266;
  margin-top: 5px;
  text-align: center;
}

.clickable-avatar {
  cursor: pointer;
  transition: transform 0.3s;
}

.clickable-avatar:hover {
  transform: scale(1.1);
}

.avatar-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-preview {
  max-width: 100%;
  max-height: 400px;
}
</style>
