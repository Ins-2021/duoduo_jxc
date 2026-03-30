<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="返工单号">
          <el-input v-model="queryParams.reworkNo" placeholder="请输入返工单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
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
        <el-button type="primary" @click="handleAdd">新增返工单</el-button>
      </template>

      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column type="index" label="序号" width="55" />
        <el-table-column prop="reworkNo" label="返工单号" />
        <el-table-column prop="productionOrderNo" label="生产单号" />
        <el-table-column prop="bundleNo" label="扎包号" />
        <el-table-column prop="defectType" label="不良类型" />
        <el-table-column prop="quantity" label="返工数量" />
        <el-table-column prop="responsiblePerson" label="责任人" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type || 'info'">
              {{ statusMap[row.status]?.label || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" link type="primary" @click="handleProcess(row)">开始处理</el-button>
            <el-button v-if="row.status === 1" link type="success" @click="handleComplete(row)">完成</el-button>
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

    <el-dialog v-model="dialogVisible" title="新增返工单" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="生产单号" prop="productionOrderNo">
          <el-input v-model="form.productionOrderNo" placeholder="请输入生产单号" />
        </el-form-item>
        <el-form-item label="扎包号" prop="bundleNo">
          <el-input v-model="form.bundleNo" placeholder="请输入扎包号" />
        </el-form-item>
        <el-form-item label="不良类型" prop="defectType">
          <el-select v-model="form.defectType" placeholder="请选择不良类型">
            <el-option label="断线" value="断线" />
            <el-option label="跳线" value="跳线" />
            <el-option label="污渍" value="污渍" />
            <el-option label="尺寸不符" value="尺寸不符" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="返工数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="责任人" prop="responsiblePerson">
          <el-input v-model="form.responsiblePerson" placeholder="请输入责任人" />
        </el-form-item>
        <el-form-item label="返工原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" placeholder="请输入返工原因" />
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
import { getReworkList, createRework, updateRework, deleteRework } from '@/api/mes'

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待处理', type: 'danger' },
  1: { label: '处理中', type: 'warning' },
  2: { label: '已完成', type: 'success' }
}

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  reworkNo: '',
  status: null as number | null
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({
  productionOrderNo: '',
  bundleNo: '',
  defectType: '',
  quantity: 1,
  responsiblePerson: '',
  reason: ''
})

const rules = {
  defectType: [{ required: true, message: '请选择不良类型', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入返工数量', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getReworkList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.reworkNo = ''
  queryParams.status = null
  handleQuery()
}

const resetForm = () => {
  Object.assign(form, {
    productionOrderNo: '',
    bundleNo: '',
    defectType: '',
    quantity: 1,
    responsiblePerson: '',
    reason: ''
  })
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleProcess = (row: any) => {
  ElMessageBox.confirm('确认开始处理该返工单?', '提示', {
    type: 'warning'
  }).then(() => {
    updateRework(row.reworkId, { status: 1 }).then(() => {
      ElMessage.success('操作成功')
      getList()
    })
  })
}

const handleComplete = (row: any) => {
  ElMessageBox.confirm('确认完成该返工单?', '提示', {
    type: 'warning'
  }).then(() => {
    updateRework(row.reworkId, { status: 2 }).then(() => {
      ElMessage.success('操作成功')
      getList()
    })
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该返工单?', '警告', {
    type: 'warning'
  }).then(() => {
    deleteRework(row.reworkId).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

const handleSubmit = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      createRework(form).then(() => {
        ElMessage.success('创建成功')
        dialogVisible.value = false
        getList()
      })
    }
  })
}

onMounted(() => {
  getList()
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
