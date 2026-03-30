<template>
  <div class="transaction-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="仓库">
          <el-select v-model="queryForm.warehouseId" placeholder="选择仓库" clearable style="width: 150px">
            <el-option
              v-for="item in warehouseList"
              :key="item.warehouseId"
              :label="item.warehouseName"
              :value="item.warehouseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品">
          <el-select v-model="queryForm.skuId" placeholder="选择商品" clearable filterable style="width: 180px">
            <el-option
              v-for="item in skuList"
              :key="item.skuId"
              :label="item.skuName"
              :value="item.skuId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="流水类型">
          <el-select v-model="queryForm.transType" placeholder="流水类型" clearable style="width: 120px">
            <el-option label="入库" :value="1" />
            <el-option label="出库" :value="2" />
            <el-option label="锁定" :value="3" />
            <el-option label="解锁" :value="4" />
            <el-option label="盘点调整" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="单据号">
          <el-input v-model="queryForm.billNo" placeholder="单据号" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="transactionId" label="流水ID" width="80" />
        <el-table-column prop="warehouseName" label="仓库" width="120" />
        <el-table-column prop="skuCode" label="商品编码" width="120" />
        <el-table-column prop="skuName" label="商品名称" width="150" />
        <el-table-column prop="transTypeName" label="流水类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTransTypeTagType(row.transType)">{{ row.transTypeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="qty" label="变动数量" width="100" align="center">
          <template #default="{ row }">
            <span :class="getQtyClass(row.transType)">{{ row.qty }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="beforeQty" label="变动前库存" width="100" align="center" />
        <el-table-column prop="afterQty" label="变动后库存" width="100" align="center" />
        <el-table-column prop="billType" label="单据类型" width="120" />
        <el-table-column prop="billNo" label="单据号" width="180" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="createTime" label="操作时间" width="160" />
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getInventoryTransactionList } from '@/api/inventory'
import { getWarehouseOptions } from '@/api/warehouse'
import { getProductSkuPage } from '@/api/product'

// 查询表单
const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  warehouseId: undefined as number | undefined,
  skuId: undefined as number | undefined,
  transType: undefined as number | undefined,
  billNo: ''
})

// 日期范围
const dateRange = ref<[string, string] | null>(null)

// 表格数据
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// 仓库列表
const warehouseList = ref<Array<{ warehouseId: number; warehouseName: string }>>([])

// 商品列表
const skuList = ref<Array<{ skuId: number; skuName: string }>>([])

// 获取流水类型标签样式
const getTransTypeTagType = (transType: number) => {
  switch (transType) {
    case 1: return 'success'
    case 2: return 'danger'
    case 3: return 'warning'
    case 4: return 'info'
    case 5: return 'primary'
    default: return ''
  }
}

// 获取数量样式类
const getQtyClass = (transType: number) => {
  switch (transType) {
    case 1:
    case 4:
      return 'text-success'
    case 2:
    case 3:
    case 5:
      return 'text-danger'
    default:
      return ''
  }
}

// 查询列表
const handleQuery = async () => {
  loading.value = true
  try {
    const params: any = {
      ...queryForm,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1]
    }
    const res = await getInventoryTransactionList(params)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const handleReset = () => {
  queryForm.warehouseId = undefined
  queryForm.skuId = undefined
  queryForm.transType = undefined
  queryForm.billNo = ''
  queryForm.pageNum = 1
  dateRange.value = null
  handleQuery()
}

// 加载仓库列表
const loadWarehouseList = async () => {
  try {
    const res = await getWarehouseOptions()
    warehouseList.value = res.data || []
  } catch (error) {
    console.error('加载仓库列表失败', error)
  }
}

// 加载商品列表
const loadSkuList = async () => {
  try {
    const res = await getProductSkuPage({ pageNum: 1, pageSize: 1000 })
    skuList.value = res.data?.list || []
  } catch (error) {
    console.error('加载商品列表失败', error)
  }
}

onMounted(() => {
  loadWarehouseList()
  loadSkuList()
  handleQuery()
})
</script>

<style scoped>
.transaction-page {
  padding: 16px;
}

.text-success {
  color: #67c23a;
  font-weight: bold;
}

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
