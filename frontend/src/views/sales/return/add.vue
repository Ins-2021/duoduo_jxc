<template>
  <div class="return-order-container">
    <el-card class="box-card">
      <div class="top-bar">
        <div class="left-actions">
          <span class="page-title"><el-icon><Tickets /></el-icon> 销售退货单</span>
          <el-button type="primary" size="small" @click="loadSourceOrder">加载原销售单</el-button>
          <el-button size="small" @click="fillAllReturnQty">整单可退数量</el-button>
        </div>
        <div class="right-actions">
          <el-button @click="router.back()">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="saving">保存</el-button>
        </div>
      </div>

      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" class="form-area">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="退货日期" prop="docDate">
              <el-date-picker v-model="formData.docDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="原销售单" prop="originSalesId">
              <div class="source-order-input">
                <el-input v-model="formData.originSalesId" placeholder="请输入原销售单 ID" @keyup.enter="loadSourceOrder" />
                <el-button :loading="sourceLoading" @click="loadSourceOrder">读取</el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="客户">
              <el-input :model-value="formData.customerName" readonly placeholder="加载原销售单后自动带出" />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="退款方式" prop="refundMethod">
              <el-select v-model="formData.refundMethod" style="width: 100%">
                <el-option label="现金" value="现金" />
                <el-option label="微信" value="微信" />
                <el-option label="支付宝" value="支付宝" />
                <el-option label="银行转账" value="银行转账" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="原单编号">
              <el-input :model-value="sourceOrder?.docNo || ''" readonly placeholder="加载后显示" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="原单日期">
              <el-input :model-value="sourceOrder?.docDate || ''" readonly placeholder="加载后显示" />
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="备注">
              <el-input v-model="formData.remark" placeholder="请输入备注信息" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div class="source-tip" v-if="sourceOrder">
        当前按原销售单 {{ sourceOrder.docNo }} 发起退货，仅支持可退数量范围内的部分退货
      </div>

      <div class="detail-table-area">
        <el-table :data="formData.details" border style="width: 100%" empty-text="请先加载原销售单">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="productInfo" label="商品信息" min-width="220" />
          <el-table-column prop="qty" label="原销售数量" width="110" align="center" />
          <el-table-column prop="returnedQty" label="已退数量" width="110" align="center" />
          <el-table-column prop="availableQty" label="可退数量" width="110" align="center" />
          <el-table-column label="本次退货" width="140" align="center">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.returnQty"
                :min="0"
                :max="scope.row.availableQty"
                controls-position="right"
                @change="updateLineAmount(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="退货单价" width="140" align="center">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.unitPrice"
                :min="0"
                :precision="2"
                controls-position="right"
                @change="updateLineAmount(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="退款金额" width="140" align="center">
            <template #default="scope">
              <span>{{ scope.row.lineAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="备注" min-width="160">
            <template #default="scope">
              <el-input v-model="scope.row.remark" placeholder="行备注" />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="summary-section">
        <div>已选退货行数：{{ selectedDetails.length }}</div>
        <div>退款总额：<span class="amount-value">{{ totalRefundAmount }}</span> 元</div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { createSalesReturnOrder, getSalesReturnSourceDetail } from '@/api/sales-return'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const sourceLoading = ref(false)
const saving = ref(false)
const sourceOrder = ref<any>(null)

const formData = reactive({
  docDate: new Date().toISOString().split('T')[0],
  customerId: undefined as number | undefined,
  customerName: '',
  originSalesId: '',
  refundMethod: '微信',
  remark: '',
  details: [] as any[]
})

const rules = {
  docDate: [{ required: true, message: '请选择退货日期', trigger: 'change' }],
  originSalesId: [{ required: true, message: '请先加载原销售单', trigger: 'blur' }]
}

const selectedDetails = computed(() => {
  return formData.details.filter(item => Number(item.returnQty) > 0)
})

const totalRefundAmount = computed(() => {
  const total = selectedDetails.value.reduce((sum, item) => sum + Number(item.lineAmount || 0), 0)
  return total.toFixed(2)
})

const updateLineAmount = (row: any) => {
  const qty = Math.min(Number(row.returnQty) || 0, Number(row.availableQty) || 0)
  row.returnQty = qty
  row.lineAmount = (qty * Number(row.unitPrice || 0)).toFixed(2)
}

const mapSourceDetails = (details: any[]) => {
  formData.details = (details || []).map(item => ({
    detailId: item.detailId,
    spuId: item.spuId,
    skuId: item.skuId,
    productInfo: item.productInfo || item.productName || `SPU:${item.spuId} SKU:${item.skuId}`,
    qty: Number(item.qty || 0),
    returnedQty: Number(item.returnedQty || 0),
    availableQty: Number(item.availableQty || 0),
    returnQty: 0,
    unitPrice: Number(item.unitPrice || 0),
    lineAmount: '0.00',
    warehouseId: item.warehouseId,
    remark: item.remark || ''
  }))
}

const loadSourceOrder = async () => {
  const sourceId = Number(formData.originSalesId)
  if (!sourceId) {
    ElMessage.warning('请输入正确的原销售单 ID')
    return
  }
  sourceLoading.value = true
  try {
    const res: any = await getSalesReturnSourceDetail(sourceId)
    sourceOrder.value = res.data
    formData.originSalesId = String(res.data.orderId)
    formData.customerId = res.data.customerId
    formData.customerName = res.data.customerName || ''
    mapSourceDetails(res.data.details || [])
    if (!formData.details.some(item => item.availableQty > 0)) {
      ElMessage.warning('该销售单已无可退数量')
    }
  } finally {
    sourceLoading.value = false
  }
}

const fillAllReturnQty = () => {
  if (!formData.details.length) {
    ElMessage.warning('请先加载原销售单')
    return
  }
  formData.details.forEach(item => {
    item.returnQty = Number(item.availableQty || 0)
    updateLineAmount(item)
  })
}

const submitForm = async () => {
  await formRef.value?.validate()
  if (!sourceOrder.value) {
    ElMessage.warning('请先加载原销售单')
    return
  }
  if (!selectedDetails.value.length) {
    ElMessage.warning('请至少录入一行退货数量')
    return
  }

  saving.value = true
  try {
    await createSalesReturnOrder({
      docDate: formData.docDate,
      customerId: formData.customerId,
      originSalesId: Number(formData.originSalesId),
      refundMethod: formData.refundMethod,
      remark: formData.remark,
      refundAmount: Number(totalRefundAmount.value),
      details: selectedDetails.value.map(item => ({
        originSalesDetailId: item.detailId,
        spuId: item.spuId,
        skuId: item.skuId,
        originQty: item.qty,
        qty: Number(item.returnQty),
        unitPrice: Number(item.unitPrice),
        lineAmount: Number(item.lineAmount),
        warehouseId: item.warehouseId,
        remark: item.remark
      }))
    })
    ElMessage.success('退货单保存成功')
    router.push('/sales/return')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  const sourceOrderId = route.query.sourceOrderId
  if (sourceOrderId) {
    formData.originSalesId = String(sourceOrderId)
    loadSourceOrder()
  }
})
</script>

<style scoped>
.return-order-container {
  height: 100%;
}

.box-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.el-card__body) {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 44px;
  padding: 0 16px;
  border-bottom: 1px solid #e6e6e6;
}

.left-actions,
.right-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title {
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 6px;
}

.form-area {
  padding: 16px 16px 4px;
  border-bottom: 1px solid #ebeef5;
}

.source-order-input {
  width: 100%;
  display: flex;
  gap: 8px;
}

.source-order-input .el-input {
  flex: 1;
}

.source-tip {
  padding: 10px 16px 0;
  color: #606266;
  font-size: 13px;
}

.detail-table-area {
  flex: 1;
  padding: 16px;
  overflow: auto;
}

.summary-section {
  display: flex;
  justify-content: flex-end;
  gap: 24px;
  padding: 14px 16px;
  border-top: 1px solid #ebeef5;
  font-size: 14px;
}

.amount-value {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}
</style>
