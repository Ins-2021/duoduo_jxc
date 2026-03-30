<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="扎包号">
          <el-input v-model="queryParams.bundleNo" placeholder="请输入扎包号" clearable />
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
        <el-table-column prop="bundleNo" label="扎包号" />
        <el-table-column prop="cutOrderNo" label="裁床单号" />
        <el-table-column prop="styleName" label="款号" />
        <el-table-column prop="color" label="颜色" />
        <el-table-column prop="size" label="尺码" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="currentProcess" label="当前工序" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已完成' : '进行中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        @change="getList"
      />
    </el-card>

    <el-dialog v-model="detailVisible" title="扎包流转详情" width="700px">
      <el-table :data="flowRecords" border>
        <el-table-column prop="processName" label="工序" />
        <el-table-column prop="workerName" label="工人" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="createTime" label="时间" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getBundleList, getBundle } from '@/api/mes'

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  bundleNo: ''
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const flowRecords = ref([])

const getList = async () => {
  loading.value = true
  try {
    const res = await getBundleList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.bundleNo = ''
  handleQuery()
}

const handleDetail = async (row: any) => {
  try {
    const res = await getBundle(row.bundleId)
    flowRecords.value = res.data?.flowRecords || []
    detailVisible.value = true
  } catch {
    flowRecords.value = []
  }
}

onMounted(() => {
  getList()
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
