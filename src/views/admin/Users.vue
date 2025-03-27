<template>
  <div class="admin-users">
    <el-card class="filter-card">
      <div class="filter-container">
        <el-form :inline="true" :model="queryParams" class="filter-form">
          <el-form-item label="真实姓名">
            <el-input
              v-model="queryParams.realName"
              placeholder="请输入真实姓名"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="用户角色">
            <el-select
              v-model="queryParams.role"
              placeholder="请选择角色"
              clearable
              style="width: 180px"
            >
              <el-option label="超级管理员" value="1" />
              <el-option label="组织管理员" value="2" />
              <el-option label="志愿者" value="3" />
            </el-select>
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
          <span>用户列表</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd">新增用户</el-button>
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
        <el-table-column label="头像" width="80" align="center">
          <template #default="{ row }">
            <el-avatar
              :size="40"
              :src="row.avatar || defaultAvatar"
              @click="previewAvatar(row.avatar)"
              class="clickable-avatar"
            />
          </template>
        </el-table-column>
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
        <el-table-column label="性别" prop="gender" width="80" align="center">
          <template #default="{ row }">
            {{ formatGender(row.gender) }}
          </template>
        </el-table-column>
        <el-table-column label="手机号" prop="mobile" min-width="120">
          <template #default="{ row }">
            {{ row.mobile || '未设置' }}
          </template>
        </el-table-column>
        <el-table-column label="邮箱" prop="email" min-width="150">
          <template #default="{ row }">
            {{ row.email || '未设置' }}
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
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      v-model="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="userFormRef" :model="userForm" :rules="userFormRules" label-width="100px">
        <el-form-item label="头像" prop="avatar" class="avatar-upload-item">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:8080/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="avatar-tip">点击上传头像</div>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
            :disabled="dialogType === 'edit'"
          />
        </el-form-item>
        <el-form-item v-if="dialogType === 'add'" label="密码" prop="password">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item v-if="dialogType === 'add'" label="确认密码" prop="confirmPassword">
          <el-input
            v-model="userForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="用户角色" prop="typeId">
          <el-select v-model="userForm.typeId" placeholder="请选择角色" style="width: 100%">
            <el-option label="超级管理员" value="1" />
            <el-option label="组织管理员" value="2" />
            <el-option label="志愿者" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="userForm.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="userForm.gender">
            <el-radio value="M">男</el-radio>
            <el-radio value="F">女</el-radio>
            <el-radio value="O">保密</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthday">
          <el-date-picker
            v-model="userForm.birthday"
            type="date"
            placeholder="选择出生日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="userForm.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="userForm.address" placeholder="请输入地址" />
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

    <!-- 头像预览对话框 -->
    <el-dialog v-model="avatarPreviewVisible" title="头像预览" width="400px" center>
      <div class="avatar-preview-container">
        <img :src="previewImageUrl" class="avatar-preview" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, UploadProps } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getUserList, addUser, updateUser, deleteUser, updateUserStatus } from '../../api/user'

// 默认头像
const defaultAvatar = 'http://60.204.157.137:9000/volunteer/default-avatar.png'

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

// 头像预览
const avatarPreviewVisible = ref(false)
const previewImageUrl = ref('')

// 上传头像的请求头
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('satoken')}`,
}

// 用户表单
const userForm = reactive({
  userId: '',
  username: '',
  password: '',
  confirmPassword: '',
  typeId: '',
  realName: '',
  idCard: '',
  gender: 'M',
  birthday: '',
  email: '',
  mobile: '',
  address: '',
  avatar: '',
  status: 1,
})

// 格式化性别
const formatGender = (gender: string) => {
  if (gender === 'M') return '男'
  if (gender === 'F') return '女'
  return '保密'
}

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

const validateIdCard = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback()
    return
  }
  // 简单的身份证号验证
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  if (reg.test(value)) {
    callback()
  } else {
    callback(new Error('请输入正确的身份证号'))
  }
}

const userFormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' },
  ],
  password: [{ validator: validatePass, trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }],
  typeId: [{ required: true, message: '请选择用户角色', trigger: 'change' }],
  idCard: [{ validator: validateIdCard, trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  mobile: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
}

// 获取用户列表
const getList = async () => {
  try {
    loading.value = true
    const res = await getUserList(queryParams)
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
  queryParams.page = 1
  getList()
}

// 重置查询条件
const resetQuery = () => {
  queryParams.realName = ''
  queryParams.role = ''
  queryParams.status = ''
  queryParams.page = 1
  getList()
}

// 表格选择项变化
const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map((item) => item.userId)
}

// 每页条数变化
const handleSizeChange = (size: number) => {
  queryParams.size = size
  getList()
}

// 当前页变化
const handleCurrentChange = (page: number) => {
  queryParams.page = page
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
  userForm.idCard = ''
  userForm.gender = 'M'
  userForm.birthday = ''
  userForm.email = ''
  userForm.mobile = ''
  userForm.address = ''
  userForm.avatar = ''
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
  userForm.idCard = row.idCard || ''
  userForm.gender = row.gender || 'M'
  userForm.birthday = row.birthday || ''
  userForm.email = row.email || ''
  userForm.mobile = row.mobile || ''
  userForm.address = row.address || ''
  userForm.avatar = row.avatar || ''
  userForm.status = row.status
  dialogVisible.value = true
}

// 头像上传前的校验
const beforeAvatarUpload: UploadProps['beforeUpload'] = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

// 头像上传成功的回调
const handleAvatarSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 200) {
    userForm.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

// 预览头像
const previewAvatar = (url: string) => {
  if (!url) return
  previewImageUrl.value = url
  avatarPreviewVisible.value = true
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
      idCard: userForm.idCard,
      gender: userForm.gender,
      birthday: userForm.birthday,
      email: userForm.email,
      mobile: userForm.mobile,
      address: userForm.address,
      avatar: userForm.avatar,
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
    submitLoading.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
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
    // 确保 userId 和 status 都是数字类型
    const userId = parseInt(row.userId)
    const status = row.status

    const res = await updateUserStatus(userId, status)
    if (res.code === 200) {
      ElMessage.success(`${status === 1 ? '启用' : '禁用'}成功`)
    } else {
      // 如果失败，回滚状态
      row.status = status === 1 ? 0 : 1
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
