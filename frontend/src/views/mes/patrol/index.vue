<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="日期">
          <el-date-picker
            v-model="queryParams.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <el-button type="primary" @click="handleAdd">新增巡检记录</el-button>
      </template>

      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column type="index" label="序号" width="55" />
        <el-table-column prop="patrolNo" label="巡检单号" />
        <el-table-column prop="productionOrderNo" label="生产单号" />
        <el-table-column prop="processName" label="工序" />
        <el-table-column prop="inspectorName" label="检验员" />
        <el-table-column prop="checkQuantity" label="检查数量" />
        <el-table-column prop="defectQuantity" label="不良数量" />
        <el-table-column prop="defectRate" label="不良率">
          <template #default="{ row }">
            {{ row.defectRate ? (row.defectRate * 100).toFixed(2) + '%' : '0%' }}
          </template>
        </el-table-column>
        <el-table-column prop="patrolTime" label="巡检时间" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="生产单号" prop="productionOrderId">
          <el-input v-model="form.productionOrderNo" placeholder="请输入生产单号" />
        </el-form-item>
        <el-form-item label="工序" prop="processId">
          <el-select v-model="form.processId" placeholder="请选择工序" filterable>
            <el-option
              v-for="item in processOptions"
              :key="item.processId"
              :label="item.processName"
              :value="item.processId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="检查数量" prop="checkQuantity">
          <el-input-number v-model="form.checkQuantity" :min="1" />
        </el-form-item>
        <el-form-item label="不良数量" prop="defectQuantity">
          <el-input-number v-model="form.defectQuantity" :min="0" />
        </el-form-item>
        <el-form-item label="不良类型" prop="defectType">
          <el-input v-model="form.defectType" placeholder="如：断线、跳线、污渍等" />
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
import { getPatrolCheckList, createPatrolCheck, updatePatrolCheck, deletePatrolCheck } from '@/api/mes'
import { getProcessList } from '@/api/process'

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  date: ''
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const dialogVisible = ref(false)
const dialogTitle = ref('新增巡检记录')
const formRef = ref<FormInstance>()
const form = reactive({
  patrolId: null as number | null,
  productionOrderNo: '',
  processId: null as number | null,
  checkQuantity: 1,
  defectQuantity: 0,
  defectType: '',
  remark: ''
})

const rules = {
  processId: [{ required: true, message: '请选择工序', trigger: 'change' }],
  checkQuantity: [{ required: true, message: '请输入检查数量', trigger: 'blur' }]
}

const processOptions = ref([])

const getList = async () => {
  loading.value = true
  try {
    const res = await getPatrolCheckList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadOptions = async () => {
  try {
    const res = await getProcessList({ pageNum: 1, pageSize: 100 })
    processOptions.value = res.data?.records || res.data || []
  } catch {
    processOptions.value = []
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.date = ''
  handleQuery()
}

const resetForm = () => {
  Object.assign(form, {
    patrolId: null,
    productionOrderNo: '',
    processId: null,
    checkQuantity: 1,
    defectQuantity: 0,
    defectType: '',
    remark: ''
  })
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增巡检记录'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  resetForm()
  dialogTitle.value = '编辑巡检记录'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该巡检记录?', '警告', {
    type: 'warning'
  }).then(() => {
    deletePatrolCheck(row.patrolId).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

const handleSubmit = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      const api = form.patrolId ? updatePatrolCheck(form.patrolId, form) : createPatrolCheck(form)
      api.then(() => {
        ElMessage.success('保存成功')
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
