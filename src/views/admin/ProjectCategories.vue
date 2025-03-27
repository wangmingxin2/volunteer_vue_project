<template>
  <div class="project-categories">
    <el-card class="filter-card">
      <div class="filter-container">
        <el-form :inline="true" :model="queryParams" class="filter-form">
          <el-form-item label="分类名称">
            <el-input
              v-model="queryParams.name"
              placeholder="请输入分类名称"
              clearable
              @keyup.enter="handleQuery"
            />
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
          <span>分类列表</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd">新增分类</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="categoryList" border>
        <el-table-column label="分类ID" prop="categoryId" width="80" align="center" />
        <el-table-column label="分类名称" prop="name" min-width="120" />
        <el-table-column label="分类描述" prop="description" min-width="200">
          <template #default="{ row }">
            {{ row.description || '暂无描述' }}
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sort" width="80" align="center" />
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

    <!-- 添加/编辑分类对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '新增分类' : '编辑分类'"
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryFormRules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="categoryForm.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status">
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
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'

// 查询参数
const queryParams = reactive({
  name: '',
  status: '',
  page: 1,
  size: 10,
})

// 分类列表数据
const categoryList = ref([])
const total = ref(0)
const loading = ref(false)

// 表单相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const categoryFormRef = ref<FormInstance>()
const submitLoading = ref(false)

// 分类表单
const categoryForm = reactive({
  categoryId: '',
  name: '',
  description: '',
  sort: 0,
  status: 1,
})

// 表单验证规则
const categoryFormRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' },
  ],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }],
}

// 获取分类列表
const getList = async () => {
  try {
    loading.value = true
    // 模拟数据，实际项目中应该调用API
    setTimeout(() => {
      categoryList.value = [
        {
          categoryId: 1,
          name: '环保类',
          description: '环境保护相关的志愿服务项目',
          sort: 1,
          status: 1,
          createdTime: '2023-05-15 10:00:00',
        },
        {
          categoryId: 2,
          name: '教育类',
          description: '教育支持相关的志愿服务项目',
          sort: 2,
          status: 1,
          createdTime: '2023-05-15 10:30:00',
        },
        {
          categoryId: 3,
          name: '社区服务类',
          description: '社区服务相关的志愿服务项目',
          sort: 3,
          status: 1,
          createdTime: '2023-05-15 11:00:00',
        },
      ]
      total.value = 3
      loading.value = false
    }, 500)
  } catch (error) {
    console.error('获取分类列表失败:', error)
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

// 新增分类
const handleAdd = () => {
  dialogType.value = 'add'
  categoryForm.categoryId = ''
  categoryForm.name = ''
  categoryForm.description = ''
  categoryForm.sort = 0
  categoryForm.status = 1
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  categoryForm.categoryId = row.categoryId
  categoryForm.name = row.name
  categoryForm.description = row.description || ''
  categoryForm.sort = row.sort
  categoryForm.status = row.status
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (!categoryFormRef.value) return

  try {
    await categoryFormRef.value.validate()
    submitLoading.value = true

    // 模拟提交，实际项目中应该调用API
    setTimeout(() => {
      ElMessage.success(dialogType.value === 'add' ? '新增分类成功' : '编辑分类成功')
      dialogVisible.value = false
      getList()
      submitLoading.value = false
    }, 500)
  } catch (error) {
    console.error('表单验证失败:', error)
    submitLoading.value = false
  }
}

// 删除分类
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除分类 ${row.name} 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      // 模拟删除，实际项目中应该调用API
      ElMessage.success('删除成功')
      getList()
    })
    .catch(() => {
      // 取消删除
    })
}

// 修改分类状态
const handleStatusChange = async (row: any) => {
  try {
    // 模拟API调用，实际项目中应该调用API
    ElMessage.success(`${row.status === 1 ? '启用' : '禁用'}成功`)
  } catch (error) {
    // 如果出错，回滚状态
    row.status = row.status === 1 ? 0 : 1
    console.error('修改分类状态失败:', error)
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.project-categories {
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
