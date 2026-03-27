<template>
  <div class="alert-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="商品名称">
          <el-input v-model="queryForm.productName" placeholder="商品名称" clearable />
        </el-form-item>
        <el-form-item label="仓库">
          <el-select v-model="queryForm.warehouseName" placeholder="仓库" clearable>
            <el-option label="仓库A" value="仓库A" />
            <el-option label="仓库B" value="仓库B" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警类型">
          <el-select v-model="queryForm.alertType" placeholder="预警类型" clearable>
            <el-option label="库存不足" value="库存不足" />
            <el-option label="库存积压" value="库存积压" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="productName" label="商品名称" width="180" />
        <el-table-column prop="warehouseName" label="仓库" width="120" />
        <el-table-column prop="currentStock" label="当前库存" width="120" />
        <el-table-column prop="minStock" label="最小库存" width="120" />
        <el-table-column prop="maxStock" label="最大库存" width="120" />
        <el-table-column prop="alertType" label="预警类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.alertType === '库存不足'" type="danger">库存不足</el-tag>
            <el-tag v-else type="warning">库存积压</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertLevel" label="预警等级" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.alertLevel === '高'" type="danger">高</el-tag>
            <el-tag v-else-if="row.alertLevel === '中'" type="warning">中</el-tag>
            <el-tag v-else type="info">低</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">未处理</el-tag>
            <el-tag v-else type="success">已处理</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="success" @click="handleProcess(row)" v-if="row.status === 0">处理</el-button>
          </template>
        </el-table-column>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { inventoryAlertApi } from '@/api/inventory'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  productName: '',
  warehouseName: '',
  alertType: ''
})

const tableData = ref([])
const total = ref(0)

const handleQuery = async () => {
  try {
    const res = await inventoryAlertApi.pageList(queryForm)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.productName = ''
  queryForm.warehouseName = ''
  queryForm.alertType = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleProcess = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要处理该预警吗？', '提示', { type: 'warning' })
    await inventoryAlertApi.process(row.alertId)
    ElMessage.success('处理成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('处理失败')
    }
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.table-operations {
  margin-bottom: 16px;
}
</style>
