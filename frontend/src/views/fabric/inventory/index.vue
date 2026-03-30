<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="面料编码">
          <el-input v-model="queryParams.fabricCode" placeholder="请输入面料编码" clearable />
        </el-form-item>
        <el-form-item label="面料名称">
          <el-input v-model="queryParams.fabricName" placeholder="请输入面料名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column type="index" label="序号" width="55" />
        <el-table-column prop="fabricCode" label="面料编码" />
        <el-table-column prop="fabricName" label="面料名称" />
        <el-table-column prop="warehouseName" label="仓库" />
        <el-table-column prop="quantity" label="库存数量" />
        <el-table-column prop="lockedQuantity" label="锁定数量" />
        <el-table-column prop="availableQuantity" label="可用数量">
          <template #default="{ row }">
            {{ (row.quantity || 0) - (row.lockedQuantity || 0) }}
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" />
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        @change="getList"
      />
    </el-card>

    <el-card class="alert-card" style="margin-top: 20px">
      <template #header>
        <span>库存预警</span>
      </template>
      <el-table :data="alerts" border>
        <el-table-column prop="fabricCode" label="面料编码" />
        <el-table-column prop="fabricName" label="面料名称" />
        <el-table-column prop="warehouseName" label="仓库" />
        <el-table-column prop="quantity" label="当前库存" />
        <el-table-column prop="minStock" label="最低库存" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag type="danger">库存不足</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getFabricInventoryList, getFabricAlerts } from '@/api/fabric'

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  fabricCode: '',
  fabricName: ''
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const alerts = ref([])

const getList = async () => {
  loading.value = true
  try {
    const res = await getFabricInventoryList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const getAlerts = async () => {
  try {
    const res = await getFabricAlerts()
    alerts.value = res.data || []
  } catch {
    alerts.value = []
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.fabricCode = ''
  queryParams.fabricName = ''
  handleQuery()
}

onMounted(() => {
  getList()
  getAlerts()
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
