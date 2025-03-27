<template>
  <div class="admin-users">
    <el-card class="filter-card">
      <div class="filter-container">
        <el-form :inline="true" :model="queryParams" class="filter-form">
          <el-form-item label="用户名">
            <el-input
              v-model="queryParams.username"
              placeholder="请输入用户名"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input
              v-model="queryParams.realName"
              placeholder="请输入真实姓名"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="用户角色">
            <el-select v-model="queryParams.typeId" placeholder="请选择角色" clearable>
              <el-option label="超级管理员" value="1" />
              <el-option label="组织管理员" value="2" />
              <el-option label="志愿者" value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
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
          <span>用户列表</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd">新增用户</el-button>
            <el-button
              type="danger"
              :disabled="selectedIds.length === 0"
              @click="handleBatchDelete"
            >
              批量删除
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="userList"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="用户ID" prop="userId" width="80" align="center" />
        <el-table-column label="用户名" prop="username" min-width="120" />
        <el-table-column label="真实姓名" prop="realName" min-width="120">
          <template #default="{ row }">
            {{ row.realName || '未设置' }}
          </template>
        </el-table-column>
        <el-table-column label="用户角色" prop="typeId" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.typeId === '1'" type="danger">超级管理员</el-tag>
            <el-tag v-else-if="row.typeId === '2'" type="warning">组织管理员</el-tag>
            <el-tag v-else-if="row.typeId === '3'" type="success">志愿者</el-tag>
            <el-tag v-else type="info">未知角色</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="邮箱" prop="email" min-width="150">
          <template #default="{ row }">
            {{ row.email || '未设置' }}
          </template>
        </el-table-column>
        <el-table-column label="手机号" prop="mobile" min-width="120">
          <template #default="{ row }">
            {{ row.mobile || '未设置' }}
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleResetPassword(row)">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="userFormRef" :model="userForm" :rules="userFormRules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
            :disabled="dialogType === 'edit'"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="dialogType === 'add'">
          <el-input
            v-model="userForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="用户角色" prop="typeId">
          <el-select v-model="userForm.typeId" placeholder="请选择角色">
            <el-option label="超级管理员" value="1" />
            <el-option label="组织管理员" value="2" />
            <el-option label="志愿者" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="userForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
            <el-radio :label="0">保密</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="userForm.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import {
  getUserList,
  addUser,
  updateUser,
  deleteUser,
  resetUserPassword,
  updateUserStatus,
} from '../api/user'

// 查询参数
const queryParams = reactive({
  status: '',
  role: '',
  realName: '',
  page: 1,
  size: 10,
})

// 用户列表数据
const userList = ref([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref<number[]>([])

// 表单相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const userFormRef = ref<FormInstance>()
const submitLoading = ref(false)

// 用户表单
const userForm = reactive({
  userId: '',
  username: '',
  password: '',
  confirmPassword: '',
  typeId: '',
  realName: '',
  gender: 1,
  email: '',
  mobile: '',
  status: 1,
})

// 表单校验规则
const validatePass = (rule: any, value: string, callback: any) => {
  if (dialogType.value === 'add' && !value) {
    callback(new Error('请输入密码'))
  } else if (value && value.length < 6) {
    callback(new Error('密码长度不能小于6位'))
  } else {
    if (userForm.confirmPassword) {
      userFormRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule: any, value: string, callback: any) => {
  if (dialogType.value === 'add' && !value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== userForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const userFormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [{ validator: validatePass, trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }],
  typeId: [{ required: true, message: '请选择用户角色', trigger: 'change' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  mobile: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
}

// 获取用户列表
const getList = async () => {
  try {
    loading.value = true
    // 转换查询参数以匹配后端接口
    const params = {
      status: queryParams.status || undefined,
      role: queryParams.typeId || undefined, // 将 typeId 映射为 role
      realName: queryParams.realName || undefined,
      page: queryParams.pageNum,
      size: queryParams.pageSize,
    }

    const res = await getUserList(params)
    if (res.code === 200) {
      userList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询按钮点击事件
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置查询条件
const resetQuery = () => {
  queryParams.username = ''
  queryParams.realName = ''
  queryParams.typeId = ''
  queryParams.status = ''
  queryParams.pageNum = 1
  getList()
}

// 表格选择项变化
const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map((item) => item.userId)
}

// 修改分页大小
const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  getList()
}

// 修改当前页
const handleCurrentChange = (page: number) => {
  queryParams.pageNum = page
  getList()
}

// 新增用户
const handleAdd = () => {
  dialogType.value = 'add'
  userForm.userId = ''
  userForm.username = ''
  userForm.password = ''
  userForm.confirmPassword = ''
  userForm.typeId = ''
  userForm.realName = ''
  userForm.gender = 1
  userForm.email = ''
  userForm.mobile = ''
  userForm.status = 1
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  userForm.userId = row.userId
  userForm.username = row.username
  userForm.password = ''
  userForm.confirmPassword = ''
  userForm.typeId = row.typeId
  userForm.realName = row.realName || ''
  userForm.gender = row.gender || 1
  userForm.email = row.email || ''
  userForm.mobile = row.mobile || ''
  userForm.status = row.status
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (!userFormRef.value) return

  try {
    await userFormRef.value.validate()
    submitLoading.value = true

    const formData = {
      userId: userForm.userId,
      username: userForm.username,
      password: userForm.password,
      typeId: userForm.typeId,
      realName: userForm.realName,
      gender: userForm.gender,
      email: userForm.email,
      mobile: userForm.mobile,
      status: userForm.status,
    }

    if (dialogType.value === 'add') {
      // 新增用户
      const res = await addUser(formData)
      if (res.code === 200) {
        ElMessage.success('新增用户成功')
        dialogVisible.value = false
        getList()
      } else {
        ElMessage.error(res.message || '新增用户失败')
      }
    } else {
      // 编辑用户
      delete formData.password // 编辑时不传密码
      const res = await updateUser(formData)
      if (res.code === 200) {
        ElMessage.success('编辑用户成功')
        dialogVisible.value = false
        getList()
      } else {
        ElMessage.error(res.message || '编辑用户失败')
      }
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 删除用户
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除用户 ${row.username} 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const res = await deleteUser(row.userId)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          getList()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        console.error('删除用户失败:', error)
      }
    })
    .catch(() => {
      // 取消删除
    })
}

// 修改用户状态
const handleStatusChange = async (row: any) => {
  try {
    const res = await updateUserStatus(row.userId, row.status)
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
    console.error('修改用户状态失败:', error)
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.admin-users {
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
</style>
