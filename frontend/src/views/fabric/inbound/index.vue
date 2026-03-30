<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="入库单号">
          <el-input v-model="queryParams.inboundNo" placeholder="请输入入库单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已审核" :value="1" />
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
        <el-button type="primary" @click="handleAdd">新增入库</el-button>
      </template>

      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column type="index" label="序号" width="55" />
        <el-table-column prop="inboundNo" label="入库单号" />
        <el-table-column prop="fabricCode" label="面料编码" />
        <el-table-column prop="fabricName" label="面料名称" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="price" label="单价" />
        <el-table-column prop="totalAmount" label="金额" />
        <el-table-column prop="batchNo" label="批次号" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已审核' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" link type="primary" @click="handleApprove(row)">审核</el-button>
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

    <el-dialog v-model="dialogVisible" title="新增入库" width="600px">
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
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="批次号" prop="batchNo">
          <el-input v-model="form.batchNo" placeholder="请输入批次号" />
        </el-form-item>
        <el-form-item label="供应商" prop="supplierId">
          <el-select v-model="form.supplierId" placeholder="请选择供应商" filterable clearable>
            <el-option
              v-for="item in supplierOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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
import { getFabricInboundList, createFabricInbound, approveFabricInbound, deleteFabricInbound } from '@/api/fabric'
import { getFabricAll } from '@/api/fabric'
import { getWarehouseOptions } from '@/api/warehouse'
import { getSupplierOptions } from '@/api/supplier'

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  inboundNo: '',
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
  price: 0,
  batchNo: '',
  supplierId: null as number | null,
  remark: ''
})

const rules = {
  fabricId: [{ required: true, message: '请选择面料', trigger: 'change' }],
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

const fabricOptions = ref([])
const warehouseOptions = ref([])
const supplierOptions = ref([])

const getList = async () => {
  loading.value = true
  try {
    const res = await getFabricInboundList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadOptions = async () => {
  const [fabricRes, warehouseRes, supplierRes] = await Promise.all([
    getFabricAll(),
    getWarehouseOptions(),
    getSupplierOptions()
  ])
  fabricOptions.value = fabricRes.data || []
  warehouseOptions.value = warehouseRes.data || []
  supplierOptions.value = supplierRes.data || []
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.inboundNo = ''
  queryParams.status = null
  handleQuery()
}

const resetForm = () => {
  Object.assign(form, {
    fabricId: null,
    warehouseId: null,
    quantity: 0,
    price: 0,
    batchNo: '',
    supplierId: null,
    remark: ''
  })
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleApprove = (row: any) => {
  ElMessageBox.confirm('确认审核该入库单?', '提示', {
    type: 'warning'
  }).then(() => {
    approveFabricInbound(row.inboundId).then(() => {
      ElMessage.success('审核成功')
      getList()
    })
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该入库单?', '警告', {
    type: 'warning'
  }).then(() => {
    deleteFabricInbound(row.inboundId).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

const handleSubmit = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      createFabricInbound(form).then(() => {
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
