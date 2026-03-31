<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="手动输入" name="manual">
          <el-form :inline="true" :model="form">
            <el-form-item label="扎包号">
              <el-input v-model="form.bundleNo" placeholder="请输入扎包号" clearable style="width: 200px" />
            </el-form-item>
            <el-form-item label="工序">
              <el-select v-model="form.processId" placeholder="请选择工序" filterable style="width: 150px">
                <el-option
                  v-for="item in processOptions"
                  :key="item.processId"
                  :label="item.processName"
                  :value="item.processId"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="工人">
              <el-select v-model="form.workerId" placeholder="请选择工人" filterable style="width: 150px">
                <el-option
                  v-for="item in workerOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="数量">
              <el-input-number v-model="form.quantity" :min="1" :max="9999" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmit" :loading="submitting">提交计件</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="扫码计件" name="scan">
          <div class="scan-container">
            <QrScanner ref="scannerRef" @scanned="handleScanned" @error="handleScanError" />
            <el-form :inline="true" :model="scanForm" class="scan-form">
              <el-form-item label="工序">
                <el-select v-model="scanForm.processId" placeholder="请选择工序" filterable style="width: 150px">
                  <el-option
                    v-for="item in processOptions"
                    :key="item.processId"
                    :label="item.processName"
                    :value="item.processId"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="工人">
                <el-select v-model="scanForm.workerId" placeholder="请选择工人" filterable style="width: 150px">
                  <el-option
                    v-for="item in workerOptions"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="数量">
                <el-input-number v-model="scanForm.quantity" :min="1" :max="9999" />
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>今日计件记录</span>
          <el-button type="primary" link @click="getList">刷新</el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column type="index" label="序号" width="55" />
        <el-table-column prop="bundleNo" label="扎包号" width="150" />
        <el-table-column prop="processName" label="工序" width="120" />
        <el-table-column prop="workerName" label="工人" width="100" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">
            {{ row.price ? `¥${row.price}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">
            {{ row.amount ? `¥${row.amount}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="scanAt" label="扫码时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.scanAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import QrScanner from '@/components/QrScanner.vue'
import { scan, scanWithQrCode, getTodayRecords } from '@/api/scan'
import { getProcessList } from '@/api/process'
import { getStaffOptions } from '@/api/settings'

const activeTab = ref('manual')
const scannerRef = ref<InstanceType<typeof QrScanner> | null>(null)
const submitting = ref(false)
const loading = ref(false)
const tableData = ref<any[]>([])
const processOptions = ref<any[]>([])
const workerOptions = ref<any[]>([])

const form = reactive({
  bundleNo: '',
  processId: null as number | null,
  workerId: null as number | null,
  quantity: 1
})

const scanForm = reactive({
  processId: null as number | null,
  workerId: null as number | null,
  quantity: 1
})

const getList = async () => {
  if (!form.workerId && !scanForm.workerId) {
    loading.value = false
    return
  }
  loading.value = true
  try {
    const workerId = form.workerId || scanForm.workerId
    if (workerId) {
      const res = await getTodayRecords(workerId)
      tableData.value = res.data || []
    }
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
  
  submitting.value = true
  try {
    await scan({
      bundleNo: form.bundleNo,
      processId: form.processId,
      workerId: form.workerId,
      quantity: form.quantity
    })
    ElMessage.success('计件成功')
    form.bundleNo = ''
    form.quantity = 1
    getList()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '计件失败')
  } finally {
    submitting.value = false
  }
}

const handleScanned = async (qrContent: string) => {
  if (!scanForm.processId) {
    ElMessage.warning('请先选择工序')
    return
  }
  if (!scanForm.workerId) {
    ElMessage.warning('请先选择工人')
    return
  }

  submitting.value = true
  try {
    await scanWithQrCode(qrContent, scanForm.workerId, scanForm.processId, scanForm.quantity)
    ElMessage.success('扫码计件成功')
    getList()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '扫码计件失败')
  } finally {
    submitting.value = false
  }
}

const handleScanError = (message: string) => {
  ElMessage.error(message)
}

const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString()
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'CONFIRMED': return 'success'
    case 'PENDING': return 'warning'
    case 'REJECTED': return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'CONFIRMED': return '已确认'
    case 'PENDING': return '待确认'
    case 'REJECTED': return '已拒绝'
    default: return status
  }
}

onMounted(() => {
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
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.scan-container {
  max-width: 500px;
  margin: 0 auto;
}
.scan-form {
  margin-top: 20px;
  text-align: center;
}
</style>
