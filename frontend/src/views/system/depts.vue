<template>
  <div class="app-container">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="部门名称" prop="deptName">
          <el-input
            v-model="queryParams.deptName"
            placeholder="请输入部门名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="部门状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-perm="'system:dept:add'"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="info"
            plain
            icon="Sort"
            @click="toggleExpandAll"
          >展开/折叠</el-button>
        </el-col>
      </el-row>

      <!-- 部门表格 -->
      <el-table
        v-if="refreshTable"
        v-loading="loading"
        :data="deptList"
        row-key="deptId"
        :default-expand-all="isExpandAll"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="deptName" label="部门名称" width="260"></el-table-column>
        <el-table-column prop="orderNum" label="排序" width="200"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="200" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-perm="'system:dept:edit'">修改</el-button>
            <el-button link type="primary" icon="Plus" @click="handleAdd(scope.row)" v-perm="'system:dept:add'">新增</el-button>
            <el-button v-if="scope.row.parentId !== 0" link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-perm="'system:dept:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加或修改部门对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="deptRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="24" v-if="form.parentId !== 0">
            <el-form-item label="上级部门" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="deptOptions"
                :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
                value-key="deptId"
                placeholder="选择上级部门"
                check-strictly
                class="w-full"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model="form.deptName" placeholder="请输入部门名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, toRefs, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listDepts, listDeptsExclude, getDept, createDept, updateDept, deleteDept } from '@/api/system'

const deptList = ref<any[]>([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const title = ref('')
const deptOptions = ref<any[]>([])
const isExpandAll = ref(true)
const refreshTable = ref(true)
const deptRef = ref()

const data = reactive({
  form: {} as any,
  queryParams: {
    deptName: undefined,
    status: undefined
  },
  rules: {
    parentId: [{ required: true, message: '上级部门不能为空', trigger: 'blur' }],
    deptName: [{ required: true, message: '部门名称不能为空', trigger: 'blur' }],
    orderNum: [{ required: true, message: '显示排序不能为空', trigger: 'blur' }],
    email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }],
    phone: [{ pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号码', trigger: 'blur' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listDepts(queryParams.value).then((res: any) => {
    deptList.value = res.data
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    deptId: undefined,
    parentId: 0,
    deptName: undefined,
    orderNum: 0,
    leader: undefined,
    phone: undefined,
    email: undefined,
    status: 1
  }
  if (deptRef.value) {
    deptRef.value.resetFields()
  }
}

function handleQuery() {
  getList()
}

function resetQuery() {
  queryParams.value.deptName = undefined
  queryParams.value.status = undefined
  handleQuery()
}

function handleAdd(row?: any) {
  reset()
  listDepts().then((res: any) => {
    deptOptions.value = res.data
  })
  if (row && row.deptId) {
    form.value.parentId = row.deptId
  }
  open.value = true
  title.value = '添加部门'
}

function toggleExpandAll() {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  nextTick(() => {
    refreshTable.value = true
  })
}

function handleUpdate(row: any) {
  reset()
  listDeptsExclude(row.deptId).then((res: any) => {
    deptOptions.value = res.data
  })
  getDept(row.deptId).then((res: any) => {
    form.value = res.data
    open.value = true
    title.value = '修改部门'
  })
}

function submitForm() {
  deptRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.deptId) {
        updateDept(form.value).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        createDept(form.value).then(() => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row: any) {
  ElMessageBox.confirm(`是否确认删除名称为"${row.deptName}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return deleteDept(row.deptId)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
.w-full {
  width: 100%;
}
</style>