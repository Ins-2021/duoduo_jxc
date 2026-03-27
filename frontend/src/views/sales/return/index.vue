<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input
          v-model="queryParams.docNo"
          placeholder="退货单号"
          style="width: 200px"
          class="filter-item"
          @keyup.enter="handleQuery"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" class="filter-item">
          <el-option label="草稿" :value="10" />
          <el-option label="已审核" :value="20" />
        </el-select>
        <el-button class="filter-item" type="primary" @click="handleQuery">查询</el-button>
        <el-button class="filter-item" type="primary" plain @click="handleAdd" v-perm="['sales:return:add']">新增退货单</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column prop="docNo" label="单据编号" width="180" />
        <el-table-column prop="docDate" label="单据日期" width="120" />
        <el-table-column prop="customerName" label="客户名称" />
        <el-table-column prop="refundAmount" label="退款金额" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 20 ? 'success' : 'info'">
              {{ scope.row.status === 20 ? '已审核' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button v-if="scope.row.status === 10" type="primary" link @click="handleAudit(scope.row)" v-perm="['sales:return:audit']">审核</el-button>
            <el-button type="primary" link @click="handleDetail(scope.row)" v-perm="['sales:return:view']">详情</el-button>
            <el-button v-if="scope.row.status === 10" type="danger" link @click="handleDelete(scope.row)" v-perm="['sales:return:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-show="total > 0"
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>

    <el-dialog title="退货单详情" v-model="detailVisible" width="800px">
      <el-descriptions border :column="2">
        <el-descriptions-item label="单据编号">{{ currentDetail.docNo }}</el-descriptions-item>
        <el-descriptions-item label="单据日期">{{ currentDetail.docDate }}</el-descriptions-item>
        <el-descriptions-item label="退款金额">{{ currentDetail.refundAmount }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentDetail.remark }}</el-descriptions-item>
      </el-descriptions>
      
      <el-table :data="currentDetail.details" border style="width: 100%; margin-top: 20px;">
        <el-table-column prop="productInfo" label="商品信息" min-width="220" />
        <el-table-column prop="originQty" label="原销售数量" width="120" />
        <el-table-column prop="qty" label="退货数量" />
        <el-table-column prop="unitPrice" label="退货单价" />
        <el-table-column prop="lineAmount" label="退款总额" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSalesReturnOrderList, deleteSalesReturnOrder, auditSalesReturnOrder, getSalesReturnOrderDetail } from '@/api/sales-return'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  docNo: '',
  status: undefined
})

const detailVisible = ref(false)
const currentDetail = ref<any>({ details: [] })

const handleQuery = () => {
  loading.value = true
  getSalesReturnOrderList(queryParams).then((res: any) => {
    list.value = res.data.list
    total.value = res.data.total
  }).finally(() => {
    loading.value = false
  })
}

const handleAdd = () => {
  router.push('/sales/return/add')
}

const handleDetail = (row: any) => {
  getSalesReturnOrderDetail(row.orderId).then((res: any) => {
    currentDetail.value = res.data
    detailVisible.value = true
  })
}

const handleAudit = (row: any) => {
  ElMessageBox.confirm(`确认审核单据 ${row.docNo} 吗？`, '提示', { type: 'warning' }).then(() => {
    auditSalesReturnOrder(row.orderId).then(() => {
      ElMessage.success('审核成功')
      handleQuery()
    })
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除单据 ${row.docNo} 吗？`, '警告', { type: 'error' }).then(() => {
    deleteSalesReturnOrder(row.orderId).then(() => {
      ElMessage.success('删除成功')
      handleQuery()
    })
  })
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.filter-item {
  margin-right: 10px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
