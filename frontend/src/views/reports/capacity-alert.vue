<template>
  <div class="app-container">
    <!-- 预警列表 -->
    <el-card shadow="never" style="margin-bottom: 16px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>
            <el-icon><Warning /></el-icon>
            产能预警
            <el-badge :value="activeAlerts.length" :max="99" type="danger" style="margin-left: 8px;" />
          </span>
          <el-button type="primary" size="small" @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="activeAlerts" v-loading="loading" border stripe>
        <el-table-column prop="alertType" label="预警类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getAlertTypeTag(row.alertType)">
              {{ getAlertTypeText(row.alertType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertLevel" label="预警级别" width="100">
          <template #default="{ row }">
            <el-tag :type="row.alertLevel === 'critical' ? 'danger' : 'warning'" effect="dark">
              {{ row.alertLevel === 'critical' ? '严重' : '警告' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="预警信息" />
        <el-table-column prop="factoryName" label="工厂" width="120" />
        <el-table-column prop="processName" label="工序" width="120" />
        <el-table-column prop="metricValue" label="当前值" width="100" align="right">
          <template #default="{ row }">
            {{ row.metricValue?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="threshold" label="阈值" width="100" align="right">
          <template #default="{ row }">
            {{ row.threshold?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="acknowledge(row)">确认</el-button>
            <el-button type="success" link @click="resolve(row)">解决</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 产能状态 -->
    <el-card shadow="never" style="margin-bottom: 16px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>工序产能状态</span>
          <el-select v-model="selectedFactory" placeholder="选择工厂" style="width: 180px;" @change="loadCapacityStatus">
            <el-option label="全部工厂" :value="undefined" />
            <el-option v-for="factory in factoryOptions" :key="factory.id" :label="factory.name" :value="factory.id" />
          </el-select>
        </div>
      </template>

      <el-row :gutter="16">
        <el-col :span="6" v-for="status in capacityStatus" :key="status.processId">
          <el-card shadow="hover" :class="['capacity-card', status.status]">
            <div class="capacity-header">
              <span class="process-name">{{ status.processName }}</span>
              <el-tag :type="getStatusTag(status.status)" size="small">
                {{ getStatusText(status.status) }}
              </el-tag>
            </div>
            <div class="capacity-body">
              <div class="capacity-item">
                <span class="label">日产能</span>
                <span class="value">{{ status.dailyCapacity }}件</span>
              </div>
              <div class="capacity-item">
                <span class="label">当前负荷</span>
                <span class="value">{{ status.currentLoad }}件</span>
              </div>
              <div class="capacity-item">
                <span class="label">利用率</span>
                <span class="value" :class="status.status">
                  {{ status.utilizationRate?.toFixed(1) }}%
                </span>
              </div>
              <div class="capacity-item">
                <span class="label">积压</span>
                <span class="value">{{ status.backlogQuantity }}件 ({{ status.backlogDays }}天)</span>
              </div>
            </div>
            <el-progress
              :percentage="Math.min(100, status.utilizationRate || 0)"
              :color="getProgressColor(status.utilizationRate)"
              :stroke-width="8"
            />
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 延期风险订单 -->
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>
            <el-icon><Timer /></el-icon>
            延期风险订单
            <el-badge :value="delayRisks.length" :max="99" type="warning" style="margin-left: 8px;" />
          </span>
        </div>
      </template>

      <el-table :data="delayRisks" v-loading="loading" border stripe>
        <el-table-column prop="orderNo" label="订单编号" width="140" />
        <el-table-column prop="styleNo" label="款式编号" width="120" />
        <el-table-column prop="styleName" label="款式名称" />
        <el-table-column prop="deadline" label="交期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.deadline) }}
          </template>
        </el-table-column>
        <el-table-column prop="remainingDays" label="剩余天数" width="100" align="center">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.remainingDays < 3 }">
              {{ row.remainingDays }}天
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="150">
          <template #default="{ row }">
            <el-progress :percentage="row.progress" :color="getProgressColorByRisk(row.riskLevel)" />
          </template>
        </el-table-column>
        <el-table-column prop="delayProbability" label="延期概率" width="100" align="right">
          <template #default="{ row }">
            <el-tag :type="getRiskTag(row.riskLevel)" size="small">
              {{ (row.delayProbability * 100).toFixed(0) }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="suggestion" label="建议措施" />
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewOrderDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 解决对话框 -->
    <el-dialog v-model="resolveDialogVisible" title="解决预警" width="500px">
      <el-form :model="resolveForm" label-width="80px">
        <el-form-item label="解决方案">
          <el-input
            v-model="resolveForm.resolution"
            type="textarea"
            :rows="4"
            placeholder="请输入解决方案..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resolveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmResolve" :loading="submitting">确认解决</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActiveAlerts, getProcessCapacityStatus, getDelayRiskOrders, acknowledgeAlert, resolveAlert } from '@/api/capacity-alert'
import type { CapacityAlert, CapacityStatus, DelayRisk } from '@/types/capacity-alert'

defineOptions({ name: 'CapacityAlert' })

const loading = ref(false)
const submitting = ref(false)
const activeAlerts = ref<CapacityAlert[]>([])
const capacityStatus = ref<CapacityStatus[]>([])
const delayRisks = ref<DelayRisk[]>([])
const selectedFactory = ref<number>()
const resolveDialogVisible = ref(false)
const currentAlert = ref<CapacityAlert | null>(null)
const resolveForm = ref({ resolution: '' })

// 工厂选项
const factoryOptions = ref([
  { id: 1, name: '工厂A' },
  { id: 2, name: '工厂B' }
])

// 格式化日期
const formatDate = (date: string | undefined) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 格式化日期时间
const formatDateTime = (datetime: string | undefined) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

// 获取预警类型标签
const getAlertTypeTag = (type: string) => {
  const map: Record<string, string> = {
    'utilization': 'primary',
    'backlog': 'warning',
    'delay': 'danger'
  }
  return map[type] || 'info'
}

// 获取预警类型文本
const getAlertTypeText = (type: string) => {
  const map: Record<string, string> = {
    'utilization': '产能利用率',
    'backlog': '积压',
    'delay': '延期'
  }
  return map[type] || type
}

// 获取状态标签
const getStatusTag = (status: string) => {
  const map: Record<string, string> = {
    'normal': 'success',
    'warning': 'warning',
    'critical': 'danger'
  }
  return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'normal': '正常',
    'warning': '警告',
    'critical': '严重'
  }
  return map[status] || status
}

// 获取进度条颜色
const getProgressColor = (rate: number | undefined) => {
  if (!rate) return '#67C23A'
  if (rate >= 90) return '#F56C6C'
  if (rate >= 80) return '#E6A23C'
  return '#67C23A'
}

// 根据风险等级获取进度条颜色
const getProgressColorByRisk = (riskLevel: string) => {
  const map: Record<string, string> = {
    'high': '#F56C6C',
    'medium': '#E6A23C',
    'low': '#67C23A'
  }
  return map[riskLevel] || '#67C23A'
}

// 获取风险标签
const getRiskTag = (riskLevel: string) => {
  const map: Record<string, string> = {
    'high': 'danger',
    'medium': 'warning',
    'low': 'success'
  }
  return map[riskLevel] || 'info'
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadAlerts(),
      loadCapacityStatus(),
      loadDelayRisks()
    ])
  } finally {
    loading.value = false
  }
}

// 加载预警
const loadAlerts = async () => {
  const res = await getActiveAlerts(selectedFactory.value)
  activeAlerts.value = res.data
}

// 加载产能状态
const loadCapacityStatus = async () => {
  const res = await getProcessCapacityStatus(selectedFactory.value)
  capacityStatus.value = res.data
}

// 加载延期风险
const loadDelayRisks = async () => {
  const res = await getDelayRiskOrders(selectedFactory.value)
  delayRisks.value = res.data
}

// 刷新数据
const refreshData = () => {
  loadData()
}

// 确认预警
const acknowledge = async (row: CapacityAlert) => {
  try {
    await ElMessageBox.confirm('确认已查看该预警？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'info'
    })
    await acknowledgeAlert(row.alertId, 1) // TODO: 使用当前用户ID
    ElMessage.success('已确认')
    loadAlerts()
  } catch (error) {
    // 用户取消
  }
}

// 解决预警
const resolve = (row: CapacityAlert) => {
  currentAlert.value = row
  resolveForm.value.resolution = ''
  resolveDialogVisible.value = true
}

// 确认解决
const confirmResolve = async () => {
  if (!resolveForm.value.resolution.trim()) {
    ElMessage.warning('请输入解决方案')
    return
  }
  submitting.value = true
  try {
    await resolveAlert(currentAlert.value!.alertId, 1, resolveForm.value.resolution) // TODO: 使用当前用户ID
    ElMessage.success('已解决')
    resolveDialogVisible.value = false
    loadAlerts()
  } finally {
    submitting.value = false
  }
}

// 查看订单详情
const viewOrderDetail = (row: DelayRisk) => {
  console.log('查看订单详情:', row)
  // TODO: 跳转到订单详情页
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.capacity-card {
  margin-bottom: 16px;
}

.capacity-card.warning {
  border: 1px solid #E6A23C;
}

.capacity-card.critical {
  border: 1px solid #F56C6C;
}

.capacity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.process-name {
  font-weight: bold;
  font-size: 16px;
}

.capacity-body {
  margin-bottom: 12px;
}

.capacity-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
  font-size: 14px;
}

.capacity-item .label {
  color: #606266;
}

.capacity-item .value {
  font-weight: 500;
}

.capacity-item .value.critical {
  color: #F56C6C;
}

.capacity-item .value.warning {
  color: #E6A23C;
}

.text-danger {
  color: #F56C6C;
  font-weight: bold;
}
</style>
