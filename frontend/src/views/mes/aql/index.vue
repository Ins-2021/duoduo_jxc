<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="AQL等级">
          <el-input v-model="queryParams.level" placeholder="请输入AQL等级" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="calc-card" style="margin-bottom: 20px">
      <template #header>
        <span>AQL抽检计算</span>
      </template>
      <el-form :inline="true" :model="calcForm">
        <el-form-item label="批量大小">
          <el-input-number v-model="calcForm.batchSize" :min="1" />
        </el-form-item>
        <el-form-item label="AQL等级">
          <el-select v-model="calcForm.level" placeholder="请选择AQL等级">
            <el-option label="0.065" value="0.065" />
            <el-option label="0.10" value="0.10" />
            <el-option label="0.15" value="0.15" />
            <el-option label="0.25" value="0.25" />
            <el-option label="0.40" value="0.40" />
            <el-option label="0.65" value="0.65" />
            <el-option label="1.0" value="1.0" />
            <el-option label="1.5" value="1.5" />
            <el-option label="2.5" value="2.5" />
            <el-option label="4.0" value="4.0" />
            <el-option label="6.5" value="6.5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCalculate">计算</el-button>
        </el-form-item>
      </el-form>
      <el-descriptions v-if="calcResult" :column="3" border>
        <el-descriptions-item label="样本量">{{ calcResult.sampleSize }}</el-descriptions-item>
        <el-descriptions-item label="接收数">{{ calcResult.acceptNum }}</el-descriptions-item>
        <el-descriptions-item label="拒收数">{{ calcResult.rejectNum }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <el-button type="primary" @click="handleAdd">新增AQL标准</el-button>
      </template>

      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column type="index" label="序号" width="55" />
        <el-table-column prop="batchRange" label="批量范围" />
        <el-table-column prop="level" label="AQL等级" />
        <el-table-column prop="sampleSize" label="样本量" />
        <el-table-column prop="acceptNum" label="接收数" />
        <el-table-column prop="rejectNum" label="拒收数" />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="批量范围" prop="batchRange">
          <el-input v-model="form.batchRange" placeholder="如：2-8" />
        </el-form-item>
        <el-form-item label="AQL等级" prop="level">
          <el-select v-model="form.level" placeholder="请选择AQL等级">
            <el-option label="0.065" value="0.065" />
            <el-option label="0.10" value="0.10" />
            <el-option label="0.15" value="0.15" />
            <el-option label="0.25" value="0.25" />
            <el-option label="0.40" value="0.40" />
            <el-option label="0.65" value="0.65" />
            <el-option label="1.0" value="1.0" />
            <el-option label="1.5" value="1.5" />
            <el-option label="2.5" value="2.5" />
            <el-option label="4.0" value="4.0" />
            <el-option label="6.5" value="6.5" />
          </el-select>
        </el-form-item>
        <el-form-item label="样本量" prop="sampleSize">
          <el-input-number v-model="form.sampleSize" :min="1" />
        </el-form-item>
        <el-form-item label="接收数" prop="acceptNum">
          <el-input-number v-model="form.acceptNum" :min="0" />
        </el-form-item>
        <el-form-item label="拒收数" prop="rejectNum">
          <el-input-number v-model="form.rejectNum" :min="1" />
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
import { getAqlList, createAql, updateAql, deleteAql, calculateAql } from '@/api/mes'

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  level: ''
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const calcForm = reactive({
  batchSize: 100,
  level: '2.5'
})
const calcResult = ref<any>(null)

const dialogVisible = ref(false)
const dialogTitle = ref('新增AQL标准')
const formRef = ref<FormInstance>()
const form = reactive({
  aqlId: null as number | null,
  batchRange: '',
  level: '',
  sampleSize: 1,
  acceptNum: 0,
  rejectNum: 1
})

const rules = {
  batchRange: [{ required: true, message: '请输入批量范围', trigger: 'blur' }],
  level: [{ required: true, message: '请选择AQL等级', trigger: 'change' }],
  sampleSize: [{ required: true, message: '请输入样本量', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getAqlList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleCalculate = async () => {
  try {
    const res = await calculateAql(calcForm.batchSize, calcForm.level)
    calcResult.value = res.data
  } catch {
    ElMessage.error('计算失败')
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.level = ''
  handleQuery()
}

const resetForm = () => {
  Object.assign(form, {
    aqlId: null,
    batchRange: '',
    level: '',
    sampleSize: 1,
    acceptNum: 0,
    rejectNum: 1
  })
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增AQL标准'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  resetForm()
  dialogTitle.value = '编辑AQL标准'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该AQL标准?', '警告', {
    type: 'warning'
  }).then(() => {
    deleteAql(row.aqlId).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

const handleSubmit = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      const api = form.aqlId ? updateAql(form.aqlId, form) : createAql(form)
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
