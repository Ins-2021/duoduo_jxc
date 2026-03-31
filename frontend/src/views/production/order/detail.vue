<template>
  <div class="app-container">
    <div class="sticky-header" style="margin-bottom: 20px;">
      <el-page-header @back="goBack">
        <template #content>
          <span class="text-large font-600 mr-3">生产订单详情</span>
        </template>
        <template #extra>
          <div class="flex items-center" style="display: flex; gap: 10px;">
            <el-button @click="goBack">返回</el-button>
            <el-button type="primary" @click="handleEdit" v-perm="'production:order:edit'">编辑</el-button>
            <el-button type="success" @click="showInboundDialog" v-perm="'production:order:inbound'" v-if="order.status === 'pending' || order.status === 'producing'">生产入库</el-button>
          </div>
        </template>
      </el-page-header>
    </div>

    <el-skeleton :loading="loading" animated>
      <template #default>
        <el-card shadow="never" style="margin-bottom: 16px;">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>基本信息</span>
              <el-tag :type="getStatusType(order.status)">{{ getStatusName(order.status) }}</el-tag>
            </div>
          </template>
          <el-descriptions :column="4" border>
            <el-descriptions-item label="生产单号">{{ order.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="款号">{{ order.styleNo }}</el-descriptions-item>
            <el-descriptions-item label="品名">{{ order.styleName }}</el-descriptions-item>
            <el-descriptions-item label="优先级">
              <el-tag :type="getPriorityType(order.priority)" size="small">{{ getPriorityName(order.priority) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="数量">{{ order.quantity }}</el-descriptions-item>
            <el-descriptions-item label="完成数">{{ order.completedQuantity || 0 }}</el-descriptions-item>
            <el-descriptions-item label="进度">
              <el-progress :percentage="progress" :stroke-width="10" style="width: 150px;" />
            </el-descriptions-item>
            <el-descriptions-item label="交期">{{ order.deadline }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="3">{{ order.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" style="margin-bottom: 16px;">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>面料需求分析</span>
              <el-button type="primary" size="small" @click="loadMaterialRequirement" :loading="materialLoading">刷新</el-button>
            </div>
          </template>
          <el-table :data="materialData.materials || []" border v-loading="materialLoading">
            <el-table-column prop="fabricCode" label="面料编码" width="120" />
            <el-table-column prop="fabricName" label="面料名称" width="150" />
            <el-table-column prop="fabricType" label="面料类型" width="100" />
            <el-table-column prop="unitUsage" label="单件用量" width="100" />
            <el-table-column prop="wastageRate" label="损耗率" width="80">
              <template #default="{ row }">{{ row.wastageRate ? (row.wastageRate * 100).toFixed(0) + '%' : '-' }}</template>
            </el-table-column>
            <el-table-column prop="requiredQty" label="需求量" width="100">
              <template #default="{ row }">{{ row.requiredQty?.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="availableQty" label="可用库存" width="100">
              <template #default="{ row }">{{ row.availableQty?.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="shortageQty" label="缺口" width="100">
              <template #default="{ row }">
                <span :style="{ color: row.shortageQty > 0 ? 'red' : 'green' }">
                  {{ row.shortageQty?.toFixed(2) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="unit" label="单位" width="60" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.sufficient ? 'success' : 'danger'" size="small">
                  {{ row.sufficient ? '充足' : '不足' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="materialData.hasShortage" style="margin-top: 10px; color: #E6A23C;">
            <el-icon><WarningFilled /></el-icon>
            部分面料库存不足，请及时采购
          </div>
        </el-card>
      </template>
    </el-skeleton>

    <el-dialog v-model="inboundDialogVisible" title="生产入库" width="500px">
      <el-form :model="inboundForm" label-width="100px">
        <el-form-item label="入库仓库" required>
          <el-select v-model="inboundForm.warehouseId" placeholder="请选择仓库" style="width: 100%">
            <el-option v-for="wh in warehouseOptions" :key="wh.value" :label="wh.label" :value="wh.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="入库明细" required>
          <div v-for="(item, index) in inboundForm.items" :key="index" style="display: flex; gap: 10px; margin-bottom: 10px;">
            <el-input v-model="item.skuCode" placeholder="SKU编码" style="width: 150px;" disabled />
            <el-input-number v-model="item.quantity" :min="1" placeholder="数量" style="width: 120px;" />
            <el-button type="danger" @click="inboundForm.items.splice(index, 1)" :icon="Delete" circle />
          </div>
          <el-button type="primary" link @click="addInboundItem">添加明细</el-button>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="inboundForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="inboundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleInbound" :loading="inboundLoading">确认入库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, WarningFilled } from '@element-plus/icons-vue'
import { getProductionOrder, calculateMaterialRequirement, productionInbound } from '@/api/production'
import { getWarehouseOptions } from '@/api/options'

defineOptions({ name: 'ProductionOrderDetail' })

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const order = ref<any>({})
const materialLoading = ref(false)
const materialData = ref<any>({})
const inboundDialogVisible = ref(false)
const inboundLoading = ref(false)
const warehouseOptions = ref<any[]>([])

const inboundForm = ref({
  warehouseId: null as number | null,
  items: [] as { skuId: number | null; skuCode: string; quantity: number }[],
  remark: ''
})

const progress = computed(() => {
  if (!order.value.quantity) return 0
  return Math.round(((order.value.completedQuantity || 0) / order.value.quantity) * 100)
})

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    producing: 'primary',
    completed: 'success',
    cancelled: 'danger'
  }
  return map[status] || 'info'
}

const getStatusName = (status: string) => {
  const map: Record<string, string> = {
    pending: '待开工',
    producing: '生产中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

const getPriorityType = (priority: string) => {
  const map: Record<string, string> = {
    low: 'info',
    normal: 'primary',
    high: 'warning',
    urgent: 'danger'
  }
  return map[priority] || 'info'
}

const getPriorityName = (priority: string) => {
  const map: Record<string, string> = {
    low: '低',
    normal: '普通',
    high: '高',
    urgent: '紧急'
  }
  return map[priority] || priority
}

const goBack = () => {
  router.back()
}

const handleEdit = () => {
  router.push(`/production/order/edit/${route.params.id}`)
}

const fetchData = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('缺少订单ID')
    return
  }
  loading.value = true
  try {
    const res = await getProductionOrder(Number(id))
    order.value = res.data || {}
  } catch (error) {
    console.error(error)
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

const loadMaterialRequirement = async () => {
  const id = route.params.id
  if (!id) return
  materialLoading.value = true
  try {
    const res = await calculateMaterialRequirement(Number(id))
    materialData.value = res.data || {}
  } catch (error) {
    console.error(error)
  } finally {
    materialLoading.value = false
  }
}

const loadWarehouseOptions = async () => {
  try {
    const res = await getWarehouseOptions()
    warehouseOptions.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const showInboundDialog = () => {
  inboundForm.value = {
    warehouseId: null,
    items: [],
    remark: ''
  }
  addInboundItem()
  inboundDialogVisible.value = true
}

const addInboundItem = () => {
  inboundForm.value.items.push({
    skuId: null,
    skuCode: '',
    quantity: 1
  })
}

const handleInbound = async () => {
  if (!inboundForm.value.warehouseId) {
    ElMessage.warning('请选择入库仓库')
    return
  }
  const validItems = inboundForm.value.items.filter(item => item.skuId && item.quantity > 0)
  if (validItems.length === 0) {
    ElMessage.warning('请添加入库明细')
    return
  }

  try {
    await ElMessageBox.confirm('确认执行生产入库？', '提示', { type: 'warning' })
    inboundLoading.value = true
    await productionInbound(Number(route.params.id), {
      warehouseId: inboundForm.value.warehouseId,
      items: validItems.map(item => ({
        skuId: item.skuId,
        quantity: item.quantity
      })),
      remark: inboundForm.value.remark
    })
    ElMessage.success('入库成功')
    inboundDialogVisible.value = false
    fetchData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '入库失败')
    }
  } finally {
    inboundLoading.value = false
  }
}

onMounted(() => {
  fetchData()
  loadMaterialRequirement()
  loadWarehouseOptions()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>
