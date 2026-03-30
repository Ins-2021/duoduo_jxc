<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="申请单号">
          <el-input v-model="queryParams.requisitionNo" placeholder="请输入申请单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待审批" :value="0" />
            <el-option label="已审批" :value="1" />
            <el-option label="已发料" :value="2" />
            <el-option label="已拒绝" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <el-button type="primary" @click="handleAdd">新增申请</el-button>
      </template>

      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column type="index" label="序号" width="55" />
        <el-table-column prop="requisitionNo" label="申请单号" />
        <el-table-column prop="fabricCode" label="面料编码" />
        <el-table-column prop="fabricName" label="面料名称" />
        <el-table-column prop="quantity" label="申请数量" />
        <el-table-column prop="purpose" label="用途" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type || 'info'">
              {{ statusMap[row.status]?.label || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" link type="primary" @click="handleApprove(row, true)">通过</el-button>
            <el-button v-if="row.status === 0" link type="warning" @click="handleApprove(row, false)">拒绝</el-button>
            <el-button v-if="row.status === 1" link type="success" @click="handleIssue(row)">发料</el-button>
            <el-button v-if="row.status === 0" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        @change="getList"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增领料申请" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="面料" prop="fabricId">
          <el-select v-model="form.fabricId" placeholder="请选择面料" filterable>
            <el-option
              v-for="item in fabricOptions"
              :key="item.fabricId"
              :label="`${item.fabricCode} - ${item.fabricName}`"
              :value="item.fabricId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="仓库" prop="warehouseId">
          <el-select v-model="form.warehouseId" placeholder="请选择仓库">
            <el-option
              v-for="item in warehouseOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="用途" prop="purpose">
          <el-input v-model="form.purpose" type="textarea" placeholder="请输入用途说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getFabricRequisitionList, createFabricRequisition, approveFabricRequisition, issueFabricRequisition, deleteFabricRequisition } from '@/api/fabric'
import { getFabricAll } from '@/api/fabric'
import { getWarehouseOptions } from '@/api/warehouse'

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待审批', type: 'warning' },
  1: { label: '已审批', type: 'success' },
  2: { label: '已发料', type: 'info' },
  3: { label: '已拒绝', type: 'danger' }
}

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  requisitionNo: '',
  status: null as number | null
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({
  fabricId: null as number | null,
  warehouseId: null as number | null,
  quantity: 0,
  purpose: ''
})

const rules = {
  fabricId: [{ required: true, message: '请选择面料', trigger: 'change' }],
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

const fabricOptions = ref([])
const warehouseOptions = ref([])

const getList = async () => {
  loading.value = true
  try {
    const res = await getFabricRequisitionList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadOptions = async () => {
  const [fabricRes, warehouseRes] = await Promise.all([
    getFabricAll(),
    getWarehouseOptions()
  ])
  fabricOptions.value = fabricRes.data || []
  warehouseOptions.value = warehouseRes.data || []
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.requisitionNo = ''
  queryParams.status = null
  handleQuery()
}

const resetForm = () => {
  Object.assign(form, {
    fabricId: null,
    warehouseId: null,
    quantity: 0,
    purpose: ''
  })
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleApprove = (row: any, approved: boolean) => {
  const msg = approved ? '确认通过该申请?' : '确认拒绝该申请?'
  ElMessageBox.prompt(msg, '审批', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '请输入审批意见（选填）'
  }).then(({ value }) => {
    approveFabricRequisition(row.requisitionId, value || '', approved).then(() => {
      ElMessage.success('审批成功')
      getList()
    })
  }).catch(() => {})
}

const handleIssue = (row: any) => {
  ElMessageBox.confirm('确认发料?', '提示', {
    type: 'warning'
  }).then(() => {
    issueFabricRequisition(row.requisitionId).then(() => {
      ElMessage.success('发料成功')
      getList()
    })
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该申请?', '警告', {
    type: 'warning'
  }).then(() => {
    deleteFabricRequisition(row.requisitionId).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

const handleSubmit = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      createFabricRequisition(form).then(() => {
        ElMessage.success('创建成功')
        dialogVisible.value = false
        getList()
      })
    }
  })
}

onMounted(() => {
  getList()
  loadOptions()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
</style>
