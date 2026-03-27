<template>
  <div class="inventory-container">
    <el-card class="box-card">
      <div class="filter-container">
        <el-input v-model="listQuery.skuCode" placeholder="商品编码" style="width: 200px;" class="filter-item" @keyup.enter="handleFilter" />
        <el-input v-model="listQuery.skuName" placeholder="商品名称" style="width: 200px;" class="filter-item" @keyup.enter="handleFilter" />
        <el-select v-model="listQuery.warehouseId" placeholder="选择仓库" clearable style="width: 150px;" class="filter-item">
          <el-option v-for="item in warehouseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-select v-model="listQuery.categoryId" placeholder="商品分类" clearable style="width: 150px;" class="filter-item">
          <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button class="filter-item" type="primary" icon="Search" @click="handleFilter">
          搜索
        </el-button>
        <el-button class="filter-item" icon="Refresh" @click="handleReset">
          重置
        </el-button>
      </div>

      <el-table
        v-loading="listLoading"
        :data="list"
        border
        fit
        highlight-current-row
        style="width: 100%; margin-top: 20px;"
      >
        <el-table-column label="商品编码" prop="skuCode" width="150px" align="center">
        </el-table-column>
        <el-table-column label="商品名称" prop="skuName" min-width="180px">
          <template #default="{row}">
            {{ row.skuName }}
            <div style="color: #999; font-size: 12px;">
              <span v-if="row.attr1">{{ row.attr1 }}</span>
              <span v-if="row.attr2"> / {{ row.attr2 }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="仓库" prop="warehouseName" width="120px" align="center">
        </el-table-column>
        <el-table-column label="当前库存" prop="qty" width="100px" align="center">
          <template #default="{row}">
            <el-tag v-if="row.qty <= row.warningQty" type="danger" size="small">{{ row.qty }}</el-tag>
            <span v-else>{{ row.qty }}</span>
          </template>
        </el-table-column>
        <el-table-column label="库存预警" prop="warningQty" width="100px" align="center">
        </el-table-column>
        <el-table-column label="进货价" prop="purchasePrice" width="100px" align="center">
          <template #default="{row}">
            {{ row.purchasePrice ? row.purchasePrice.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column label="零售价" prop="retailPrice" width="100px" align="center">
          <template #default="{row}">
            {{ row.retailPrice ? row.retailPrice.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column label="库存金额" width="120px" align="center">
          <template #default="{row}">
            {{ (row.qty * (row.purchasePrice || 0)).toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-show="total>0"
        :total="total"
        v-model:current-page="listQuery.pageNum"
        v-model:page-size="listQuery.pageSize"
        @current-change="getList"
        style="margin-top: 20px; text-align: right;"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getInventoryList } from '@/api/inventory'
import { getWarehouseOptions } from '@/api/options'
import { getCategoryOptions } from '@/api/options'

const list = ref([])
const total = ref(0)
const listLoading = ref(true)
const listQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  skuCode: undefined,
  skuName: undefined,
  warehouseId: undefined,
  categoryId: undefined
})

const warehouseOptions = ref<any[]>([])
const categoryOptions = ref<any[]>([])

const getList = async () => {
  listLoading.value = true
  try {
    const res = await getInventoryList(listQuery)
    list.value = res.data.list
    total.value = res.data.total
  } finally {
    listLoading.value = false
  }
}

const handleFilter = () => {
  listQuery.pageNum = 1
  getList()
}

const handleReset = () => {
  listQuery.skuCode = undefined
  listQuery.skuName = undefined
  listQuery.warehouseId = undefined
  listQuery.categoryId = undefined
  listQuery.pageNum = 1
  getList()
}

onMounted(async () => {
  try {
    warehouseOptions.value = await getWarehouseOptions()
    categoryOptions.value = await getCategoryOptions()
  } catch {
    warehouseOptions.value = []
    categoryOptions.value = []
  }
  getList()
})
</script>

<style scoped>
.filter-container {
  padding-bottom: 10px;
}
.filter-item {
  margin-right: 10px;
  margin-bottom: 10px;
}
</style>
