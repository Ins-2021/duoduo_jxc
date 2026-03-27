<template>
  <div class="booking-container">
    <el-card class="box-card" shadow="never">
      <el-tabs v-model="activeTab" class="booking-tabs" @tab-click="handleTabChange">
        <!-- =================== 按单据 =================== -->
        <el-tab-pane label="按单据" name="by-order">
          <!-- 工具栏 -->
          <div class="toolbar" :class="{ 'toolbar-expanded': isOrderSearchExpanded }">
            <div class="left-actions">
              <el-button type="primary" @click="$router.push('/sales/booking/add')">新增预定单</el-button>
              <el-button @click="handleApprove" v-perm="'sales:booking:audit'">审核</el-button>
              <el-button @click="handleUnapprove" v-perm="'sales:booking:audit'">反审核</el-button>
              <el-button @click="handleBatchDelete" type="danger" v-perm="'sales:booking:delete'">删除</el-button>
            </div>
            <div class="right-search">
              <!-- 基础搜索区 (始终显示) -->
              <div class="search-form-row">
                <el-input v-model="searchOrder.productName" placeholder="商品名称" clearable class="search-item" />
                <el-input v-model="searchOrder.orderNo" placeholder="单据编号" clearable class="search-item" />
                <el-select v-model="searchOrder.customer" placeholder="客户" clearable class="search-item">
                  <el-option label="赵尚青" value="zsq" />
                </el-select>
              </div>

              <!-- 展开的高级搜索区 -->
              <div v-show="isOrderSearchExpanded" class="expanded-search-area">
                <div class="search-form-row">
                  <el-date-picker
                    v-model="searchOrder.startDate"
                    type="datetime"
                    format="YYYY-MM-DD HH:mm:ss"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    placeholder="开始时间"
                    class="search-item"
                  />
                  <el-date-picker
                    v-model="searchOrder.endDate"
                    type="datetime"
                    format="YYYY-MM-DD HH:mm:ss"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    placeholder="结束时间"
                    class="search-item"
                  />
                  <el-select v-model="searchOrder.outStatus" placeholder="出库状态" clearable class="search-item">
                    <el-option v-for="o in outStockOptions" :key="o.value" :label="o.label" :value="o.value" />
                  </el-select>
                </div>
                <div class="search-form-row">
                  <el-select v-model="searchOrder.store" placeholder="平集仓库" clearable class="search-item">
                    <el-option label="平集仓库" value="1" />
                  </el-select>
                  <el-select v-model="searchOrder.creator" placeholder="制单人" clearable class="search-item">
                    <el-option label="景海静" value="jhj" />
                  </el-select>
                  <el-select v-model="searchOrder.account" placeholder="结算账户" clearable class="search-item">
                    <el-option label="现金" value="cash" />
                  </el-select>
                </div>
                <div class="search-form-row">
                  <el-input v-model="searchOrder.remark" placeholder="备注信息" clearable class="search-item" />
                  <el-input v-model="searchOrder.customerCategory" placeholder="客户分类" clearable class="search-item" />
                </div>
              </div>

              <!-- 操作按钮区 -->
              <div class="search-actions">
                <el-button type="primary" @click="handleSearch">搜索</el-button>
                <el-button>导出</el-button>
                <el-button link type="primary" @click="toggleOrderSearch">
                  {{ isOrderSearchExpanded ? '关闭筛选' : '展开筛选' }} 
                  <el-icon class="el-icon--right">
                    <ArrowUp v-if="isOrderSearchExpanded" />
                    <ArrowDown v-else />
                  </el-icon>
                </el-button>
                <el-button icon="Setting" circle @click="openSettingDialog('order')" />
              </div>
            </div>
          </div>

          <!-- 表格 -->
          <el-table 
            :data="orderData" 
            border 
            style="width: 100%" 
            show-summary 
            :summary-method="getOrderSummaries"
            @selection-change="handleSelectionChange"
          >
            <template v-for="col in orderColumns" :key="col.prop">
              <el-table-column
                v-if="col.visible"
                :prop="col.prop"
                :label="col.label"
                :type="col.type"
                :width="col.width"
                :min-width="col.minWidth"
                :fixed="col.fixed"
                align="center"
              >
                <!-- 针对特殊列的自定义插槽 -->
                <template #default="scope" v-if="col.prop === 'orderNo'">
                  <el-link type="primary" :underline="false">{{ scope.row.orderNo }}</el-link>
                </template>
                <template #default="scope" v-else-if="col.prop === 'status'">
                  <span style="color: #f56c6c; font-weight: bold;">{{ salesOrderStatusLabel(scope.row.status) }}</span>
                </template>
                <template #default="scope" v-else-if="col.prop === 'actions'">
                  <div class="action-buttons">
                    <el-button size="small" type="primary" @click="handleConvertToSales(scope.row)">转销售</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteOrder(scope.row)">删除</el-button>
                  </div>
                </template>
              </el-table-column>
            </template>
          </el-table>
          
          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="searchOrder.pageNum"
              v-model:page-size="searchOrder.pageSize"
              :page-sizes="[10, 20, 30, 50]"
              layout="prev, pager, next, jumper, total, sizes"
              :total="totalOrders"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
            />
          </div>
        </el-tab-pane>

        <!-- =================== 按明细 =================== -->
        <el-tab-pane label="按明细" name="by-detail">
          <!-- 工具栏 -->
          <div class="toolbar" style="justify-content: flex-end;" :class="{ 'toolbar-expanded': isDetailSearchExpanded }">
            <div class="right-search">
              <!-- 基础搜索区 (始终显示) -->
              <div class="search-form-row">
                <el-input v-model="searchDetail.keyword" placeholder="编号、名称、规格" clearable class="search-item" />
                <el-input v-model="searchDetail.category" placeholder="商品分类" clearable class="search-item" />
                <el-select v-model="searchDetail.customer" placeholder="客户" clearable class="search-item">
                  <el-option label="赵尚青" value="zsq" />
                </el-select>
              </div>

              <!-- 展开的高级搜索区 -->
              <div v-show="isDetailSearchExpanded" class="expanded-search-area">
                <div class="search-form-row">
                  <el-input v-model="searchDetail.spec" placeholder="规格型号" clearable class="search-item" />
                  <el-input v-model="searchDetail.productCode" placeholder="商品编号" clearable class="search-item" />
                  <el-input v-model="searchDetail.barcode" placeholder="条形码" clearable class="search-item" />
                </div>
                <div class="search-form-row">
                  <el-select v-model="searchDetail.brand" placeholder="品牌" clearable class="search-item">
                    <el-option label="品牌A" value="A" />
                  </el-select>
                  <el-input v-model="searchDetail.remark" placeholder="备注" clearable class="search-item" />
                  <el-input v-model="searchDetail.orderRemark" placeholder="单据备注" clearable class="search-item" />
                </div>
                <div class="search-form-row">
                  <el-input v-model="searchDetail.location" placeholder="货位" clearable class="search-item" />
                  <el-select v-model="searchDetail.store" placeholder="仓库" clearable class="search-item">
                    <el-option label="主仓库" value="1" />
                  </el-select>
                  <el-select v-model="searchDetail.creator" placeholder="制单人" clearable class="search-item">
                    <el-option label="景海静" value="jhj" />
                  </el-select>
                </div>
                <div class="search-form-row">
                  <el-input v-model="searchDetail.orderNo" placeholder="单据编号" clearable class="search-item" />
                  <el-date-picker
                    v-model="searchDetail.startDate"
                    type="datetime"
                    format="YYYY-MM-DD HH:mm:ss"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    placeholder="开始时间"
                    class="search-item"
                  />
                  <el-date-picker
                    v-model="searchDetail.endDate"
                    type="datetime"
                    format="YYYY-MM-DD HH:mm:ss"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    placeholder="结束时间"
                    class="search-item"
                  />
                </div>
                <div class="search-form-row">
                  <el-input v-model="searchDetail.customerCategory" placeholder="请选择客户分类" clearable class="search-item" />
                </div>
              </div>

              <!-- 操作按钮区 -->
              <div class="search-actions">
                <el-button type="primary" @click="handleDetailSearch">查询</el-button>
                <el-button>导出</el-button>
                <el-button link type="primary" @click="toggleDetailSearch">
                  {{ isDetailSearchExpanded ? '关闭筛选' : '展开筛选' }} 
                  <el-icon class="el-icon--right">
                    <ArrowUp v-if="isDetailSearchExpanded" />
                    <ArrowDown v-else />
                  </el-icon>
                </el-button>
                <el-button icon="Setting" circle @click="openSettingDialog('detail')" />
              </div>
            </div>
          </div>

          <!-- 表格 -->
          <el-table 
            :data="detailData" 
            border 
            style="width: 100%" 
            show-summary 
            :summary-method="getDetailSummaries"
          >
            <template v-for="col in detailColumns" :key="col.prop">
              <el-table-column
                v-if="col.visible"
                :prop="col.prop"
                :label="col.label"
                :type="col.type"
                :width="col.width"
                :min-width="col.minWidth"
                :fixed="col.fixed"
                align="center"
              >
                <template #default="scope" v-if="col.prop === 'orderNo'">
                  <el-link type="primary" :underline="false">{{ scope.row.orderNo }}</el-link>
                </template>
                <template #default="scope" v-else-if="col.prop === 'productName'">
                  <el-link type="primary" :underline="false">{{ scope.row.productName }}</el-link>
                </template>
                <template #default="scope" v-else-if="col.prop === 'qty'">
                  <span style="color: #409EFF; font-weight: bold;">{{ scope.row.qty }}</span>
                </template>
              </el-table-column>
            </template>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="searchDetail.pageNum"
              v-model:page-size="searchDetail.pageSize"
              :page-sizes="[10, 20, 30, 50]"
              layout="prev, pager, next, jumper, total, sizes"
              :total="detailTotal"
              @current-change="handlePageChange2"
              @size-change="handleSizeChange2"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 显示字段设置弹窗 -->
    <el-dialog v-model="settingDialogVisible" title="显示字段设置" width="600px" custom-class="column-setting-dialog">
      <div class="setting-tips">
        <p>按住上下拖拽可以改变字段顺序。</p>
        <p>注意：下面如果有<span style="color: #f56c6c;">字段配置名称为空的行、带商品名称或者相关操作的文字行</span>，不要隐藏和不要改变位置，否则会导致位置错误，其他的可以随意拖动和隐藏。</p>
      </div>
      
      <el-table 
        :data="currentSettingColumns" 
        row-key="prop" 
        border 
        size="small"
        ref="settingTableRef"
      >
        <el-table-column label="排序" width="60" align="center">
          <template #default>
            <el-icon class="drag-handle" style="cursor: move; font-size: 16px;"><Sort /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="originalLabel" label="字段名称" width="150" />
        <el-table-column label="字段配置">
          <template #default="scope">
            <el-input v-model="scope.row.label" size="small" :disabled="!scope.row.originalLabel" />
          </template>
        </el-table-column>
        <el-table-column label="显示" width="80" align="center">
          <template #default="scope">
            <el-checkbox v-model="scope.row.visible" :disabled="!scope.row.originalLabel || scope.row.prop === 'actions'" />
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="settingDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveSettings">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onMounted } from 'vue'
import Sortable from 'sortablejs'
import { getSalesOrderList, deleteSalesOrder, convertToSales, getSalesOrderDetailPage, auditSalesOrder, unauditSalesOrder } from '@/api/sales'
import { getDict } from '@/api/dict'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('by-order')

type ColumnConfig = {
  prop: string
  originalLabel: string
  label: string
  visible: boolean
  type?: string
  width?: number
  minWidth?: number
  fixed?: any
}

const orderColumns = ref<ColumnConfig[]>([
  { prop: 'selection', originalLabel: '', label: '', type: 'selection', visible: true, width: 55, fixed: false },
  { prop: 'store', originalLabel: '所属门店', label: '所属门店', visible: true, width: 100 },
  { prop: 'docDate', originalLabel: '单据日期', label: '单据日期', visible: true, width: 120 },
  { prop: 'docNo', originalLabel: '单据编号', label: '单据编号', visible: true, width: 180 },
  { prop: 'customerId', originalLabel: '客户ID', label: '客户ID', visible: true, width: 100 },
  { prop: 'products', originalLabel: '销售预定商品', label: '销售预定商品', visible: true, minWidth: 200 },
  { prop: 'totalAmount', originalLabel: '总计金额(元)', label: '总计金额(元)', visible: true, width: 120 },
  { prop: 'discountAmount', originalLabel: '折扣金额(元)', label: '折扣金额(元)', visible: true, width: 120 },
  { prop: 'actualAmount', originalLabel: '折后金额(元)', label: '折后金额(元)', visible: true, width: 120 },
  { prop: 'paidAmount', originalLabel: '已收金额(元)', label: '已收金额(元)', visible: true, width: 100 },
  { prop: 'operatorId', originalLabel: '制单人ID', label: '制单人ID', visible: true, width: 100 },
  { prop: 'remark', originalLabel: '备注信息', label: '备注信息', visible: true, minWidth: 120 },
  { prop: 'status', originalLabel: '状态', label: '状态', visible: true, width: 100, fixed: 'right' },
  { prop: 'actions', originalLabel: '相关操作', label: '相关操作', visible: true, width: 260, fixed: 'right' }
])

const detailColumns = ref<ColumnConfig[]>([
  { prop: 'selection', originalLabel: '', label: '', type: 'selection', visible: true, width: 55 },
  { prop: 'docNo', originalLabel: '单据编号', label: '单据编号', visible: true, width: 180 },
  { prop: 'qty', originalLabel: '数量', label: '数量', visible: true, width: 80 }
])

const settingDialogVisible = ref(false)
const currentSettingType = ref<'order'|'detail'>('order')
const currentSettingColumns = ref<ColumnConfig[]>([])
const settingTableRef = ref<any>(null)
let sortableInstance: Sortable | null = null

const openSettingDialog = (type: 'order'|'detail') => {
  currentSettingType.value = type
  currentSettingColumns.value = JSON.parse(JSON.stringify(type === 'order' ? orderColumns.value : detailColumns.value))
  settingDialogVisible.value = true

  nextTick(() => {
    const tbody = settingTableRef.value.$el.querySelector('.el-table__body-wrapper tbody')
    if (sortableInstance) sortableInstance.destroy()
    sortableInstance = Sortable.create(tbody, {
      handle: '.drag-handle',
      animation: 150,
      onEnd: ({ newIndex, oldIndex }) => {
        if (newIndex === undefined || oldIndex === undefined) return
        const targetRow = currentSettingColumns.value.splice(oldIndex, 1)[0]
        currentSettingColumns.value.splice(newIndex, 0, targetRow)
      }
    })
  })
}

const saveSettings = () => {
  if (currentSettingType.value === 'order') {
    orderColumns.value = [...currentSettingColumns.value]
  } else {
    detailColumns.value = [...currentSettingColumns.value]
  }
  settingDialogVisible.value = false
}

// --- 按单据状态 ---
const isOrderSearchExpanded = ref(false)
const toggleOrderSearch = () => {
  isOrderSearchExpanded.value = !isOrderSearchExpanded.value
}

const searchOrder = reactive<any>({
  docNo: '',
  orderType: 3, // 预订单
  pageNum: 1,
  pageSize: 10
})

const orderData = ref([])
const totalOrders = ref(0)
const salesOrderStatusMap = ref<Record<string, string>>({})
const outStockOptions = ref<any[]>([])

const salesOrderStatusLabel = (value: any) => {
  const v = value == null ? '' : String(value)
  return salesOrderStatusMap.value[v] ?? v
}

const fetchOrders = async () => {
  try {
    const res = await getSalesOrderList(searchOrder)
    orderData.value = res.data.list
    totalOrders.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

onMounted(async () => {
  try {
    const [statusRes, outRes] = await Promise.all([getDict('sales_order_status'), getDict('out_stock_status')])
    const map: Record<string, string> = {}
    ;(statusRes.data || []).forEach((i: any) => {
      map[String(i.value)] = i.label
    })
    salesOrderStatusMap.value = map
    outStockOptions.value = outRes.data || []
  } catch {
    salesOrderStatusMap.value = {}
    outStockOptions.value = []
  }
})

const handleSearch = () => {
  searchOrder.pageNum = 1
  fetchOrders()
}

const handleDetailSearch = () => {
  searchDetail.pageNum = 1
  fetchDetailData()
}

const selectedOrders = ref<any[]>([])

const handleSelectionChange = (val: any[]) => {
  selectedOrders.value = val
}

const handleApprove = async () => {
  if (selectedOrders.value.length === 0) {
    ElMessage.warning('请选择要审核的单据')
    return
  }
  try {
    for (const order of selectedOrders.value) {
      await auditSalesOrder(order.orderId)
    }
    ElMessage.success('批量审核成功')
    fetchOrders()
  } catch (e) {
    console.error(e)
  }
}

const handleUnapprove = async () => {
  if (selectedOrders.value.length === 0) {
    ElMessage.warning('请选择要反审核的单据')
    return
  }
  try {
    for (const order of selectedOrders.value) {
      await unauditSalesOrder(order.orderId)
    }
    ElMessage.success('批量反审核成功')
    fetchOrders()
  } catch (e) {
    console.error(e)
  }
}

const handleBatchDelete = async () => {
  if (selectedOrders.value.length === 0) {
    ElMessage.warning('请选择要删除的单据')
    return
  }
  ElMessageBox.confirm('确认删除选中的单据吗?', '提示', { type: 'warning' }).then(async () => {
    try {
      for (const order of selectedOrders.value) {
        await deleteSalesOrder(order.orderId)
      }
      ElMessage.success('批量删除成功')
      fetchOrders()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

const handleDeleteOrder = (row: any) => {
  ElMessageBox.confirm('确认删除该预订单吗?', '提示', { type: 'warning' }).then(async () => {
    await deleteSalesOrder(row.orderId)
    ElMessage.success('删除成功')
    fetchOrders()
  })
}

const handleConvertToSales = async (row: any) => {
  try {
    await convertToSales(row.orderId)
    ElMessage.success('转销售成功')
    fetchOrders()
  } catch(e) {
    console.error(e)
  }
}

const handleTabChange = (tab: any) => {
  if (tab.props.name === 'by-order') {
    fetchOrders()
  } else {
    fetchDetailData()
  }
}

onMounted(() => {
  fetchOrders()
  fetchDetailData()
})

const currentPage = ref(1)
const pageSize = ref(10)
const handlePageChange = (val: number) => {
  searchOrder.pageNum = val
  fetchOrders()
}

const handleSizeChange = (val: number) => {
  searchOrder.pageSize = val
  searchOrder.pageNum = 1
  fetchOrders()
}

// 按单据合计逻辑
const getOrderSummaries = (param: any) => {
  const { columns, data } = param
  const sums: string[] = []
  columns.forEach((column: any, index: number) => {
    if (index === 1) {
      sums[index] = '本页合计'
      return
    }
    if (['totalAmount', 'actualAmount', 'paidAmount'].includes(column.property)) {
      const values = data.map((item: any) => Number(item[column.property]))
      if (!values.every((value: number) => Number.isNaN(value))) {
        const sum = values.reduce((prev: number, curr: number) => {
          const value = Number(curr)
          if (!Number.isNaN(value)) {
            return prev + curr
          } else {
            return prev
          }
        }, 0)
        sums[index] = sum.toFixed(2)
      } else {
        sums[index] = '0.00'
      }
    } else {
      sums[index] = ''
    }
  })
  return sums
}

// --- 按明细状态 ---
const isDetailSearchExpanded = ref(false)
const toggleDetailSearch = () => {
  isDetailSearchExpanded.value = !isDetailSearchExpanded.value
}

const searchDetail = reactive<any>({
  keyword: '',
  pageNum: 1,
  pageSize: 10
})

const detailData = ref<any[]>([])
const detailTotal = ref(0)
const fetchDetailData = async () => {
  try {
    const res = await getSalesOrderDetailPage({
      ...searchDetail,
      orderType: 3 // 预订单明细
    })
    detailData.value = res.data.list || []
    detailTotal.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  }
}

const handlePageChange2 = (val: number) => {
  searchDetail.pageNum = val
  fetchDetailData()
}

const handleSizeChange2 = (val: number) => {
  searchDetail.pageSize = val
  searchDetail.pageNum = 1
  fetchDetailData()
}

// 按明细合计逻辑
const getDetailSummaries = (param: any) => {
  const { columns, data } = param
  const sums: string[] = []
  columns.forEach((column: any, index: number) => {
    if (index === 1) {
      sums[index] = '本页合计'
      return
    }
    if (column.property === 'qty') {
      const values = data.map((item: any) => Number(item[column.property]))
      if (!values.every((value: number) => Number.isNaN(value))) {
        sums[index] = String(values.reduce((prev: number, curr: number) => prev + curr, 0)) + '.00'
      } else {
        sums[index] = '0.00'
      }
    } else {
      sums[index] = ''
    }
  })
  return sums
}
</script>

<style scoped>
.booking-container {
  height: 100%;
}
.box-card {
  height: 100%;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  transition: all 0.3s ease;
}
.toolbar-expanded {
  /* 可以在这里添加展开时的特殊样式 */
}
.left-actions {
  display: flex;
  gap: 8px;
}
.right-search {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}
.search-form-row {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
.search-item {
  width: 150px;
}
.expanded-search-area {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}
.search-actions {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: flex-end;
  width: 100%;
}
.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-start;
}

.action-buttons {
  display: flex;
  flex-wrap: nowrap;
  gap: 4px;
  justify-content: center;
}

.action-buttons .el-button {
  margin-left: 0;
  padding: 5px 8px;
}

:deep(.el-table__footer-wrapper tbody td.el-table__cell) {
  color: #f56c6c;
  font-weight: bold;
}

/* 字段设置弹窗样式 */
:deep(.column-setting-dialog .el-dialog__body) {
  padding-top: 10px;
  padding-bottom: 10px;
}
.setting-tips {
  margin-bottom: 15px;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}
.setting-tips p {
  margin: 0 0 5px 0;
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
