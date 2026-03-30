<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input
          v-model="queryParams.keyword"
          placeholder="员工姓名/工号"
          style="width: 200px"
          class="filter-item"
          @keyup.enter="handleQuery"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" class="filter-item">
          <el-option label="在职" :value="1" />
          <el-option label="离职" :value="0" />
        </el-select>
        <el-button class="filter-item" type="primary" @click="handleQuery">查询</el-button>
        <el-button class="filter-item" type="primary" plain @click="handleAdd">新增</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column prop="staffId" label="ID" width="80" />
        <el-table-column prop="staffNo" label="工号" min-width="100" />
        <el-table-column prop="staffName" label="姓名" min-width="120" />
        <el-table-column prop="deptName" label="部门" min-width="120" />
        <el-table-column prop="postName" label="岗位" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-show="total > 0"
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="工号" prop="staffNo">
          <el-input v-model="form.staffNo" placeholder="请输入工号" />
        </el-form-item>
        <el-form-item label="姓名" prop="staffName">
          <el-input v-model="form.staffName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择部门" style="width: 100%">
            <el-option v-for="item in deptList" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
          </el-select>
        </el-form-item>
        <el-form-item label="岗位" prop="postId">
          <el-select v-model="form.postId" placeholder="请选择岗位" style="width: 100%">
            <el-option v-for="item in postList" :key="item.postId" :label="item.postName" :value="item.postId" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="0">离职</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { listDepts, pagePosts } from '@/api/system'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const deptList = ref([])
const postList = ref([])
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: undefined as number | undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const form = reactive({
  staffId: undefined as number | undefined,
  staffNo: '',
  staffName: '',
  deptId: undefined as number | undefined,
  postId: undefined as number | undefined,
  phone: '',
  status: 1
})
const rules = {
  staffName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    list.value = []
    total.value = 0
    loading.value = false
  }, 300)
}

const loadDeptList = () => {
  listDepts().then((res: any) => {
    deptList.value = res.data || []
  })
}

const loadPostList = () => {
  pagePosts({ pageNum: 1, pageSize: 100 }).then((res: any) => {
    postList.value = res.data?.list || res.data?.records || []
  })
}

const resetForm = () => {
  form.staffId = undefined
  form.staffNo = ''
  form.staffName = ''
  form.deptId = undefined
  form.postId = undefined
  form.phone = ''
  form.status = 1
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增员工'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  resetForm()
  form.staffId = row.staffId
  form.staffNo = row.staffNo
  form.staffName = row.staffName
  form.deptId = row.deptId
  form.postId = row.postId
  form.phone = row.phone
  form.status = row.status
  dialogTitle.value = '编辑员工'
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认要删除员工"${row.staffName}"吗？`, '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      ElMessage.success(form.staffId ? '修改成功' : '新增成功')
      dialogVisible.value = false
      handleQuery()
    }
  })
}

onMounted(() => {
  loadDeptList()
  loadPostList()
  handleQuery()
})
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.filter-item {
  margin-right: 10px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
