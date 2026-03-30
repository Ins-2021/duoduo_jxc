<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="form">
        <el-form-item label="扎包号">
          <el-input v-model="form.bundleNo" placeholder="请输入或扫描扎包号" clearable />
        </el-form-item>
        <el-form-item label="工序">
          <el-select v-model="form.processId" placeholder="请选择工序" filterable>
            <el-option
              v-for="item in processOptions"
              :key="item.processId"
              :label="item.processName"
              :value="item.processId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工人">
          <el-select v-model="form.workerId" placeholder="请选择工人" filterable>
            <el-option
              v-for="item in workerOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="form.quantity" :min="1" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交计件</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <span>今日计件记录</span>
      </template>
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column type="index" label="序号" width="55" />
        <el-table-column prop="bundleNo" label="扎包号" />
        <el-table-column prop="processName" label="工序" />
        <el-table-column prop="workerName" label="工人" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="price" label="单价" />
        <el-table-column prop="amount" label="金额" />
        <el-table-column prop="createTime" label="时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createPieceRecord, getPieceRecordList } from '@/api/mes'
import { getProcessList } from '@/api/process'
import { getStaffOptions } from '@/api/settings'

const form = reactive({
  bundleNo: '',
  processId: null as number | null,
  workerId: null as number | null,
  quantity: 1
})

const loading = ref(false)
const tableData = ref([])
const processOptions = ref([])
const workerOptions = ref([])

const getList = async () => {
  loading.value = true
  try {
    const today = new Date().toISOString().split('T')[0]
    const res = await getPieceRecordList({ pageNum: 1, pageSize: 50, startDate: today })
    tableData.value = res.data.records || []
  } finally {
    loading.value = false
  }
}

const loadOptions = async () => {
  try {
    const [processRes, workerRes] = await Promise.all([
      getProcessList({ pageNum: 1, pageSize: 100 }),
      getStaffOptions()
    ])
    processOptions.value = processRes.data?.records || processRes.data || []
    workerOptions.value = workerRes.data || []
  } catch {
    processOptions.value = []
    workerOptions.value = []
  }
}

const handleSubmit = async () => {
  if (!form.bundleNo) {
    ElMessage.warning('请输入扎包号')
    return
  }
  if (!form.processId) {
    ElMessage.warning('请选择工序')
    return
  }
  if (!form.workerId) {
    ElMessage.warning('请选择工人')
    return
  }
  
  try {
    await createPieceRecord({
      bundleNo: form.bundleNo,
      processId: form.processId,
      workerId: form.workerId,
      quantity: form.quantity
    })
    ElMessage.success('计件成功')
    form.bundleNo = ''
    form.quantity = 1
    getList()
  } catch {
    ElMessage.error('计件失败')
  }
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
