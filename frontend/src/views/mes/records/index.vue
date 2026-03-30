<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="扎包号">
          <el-input v-model="queryParams.bundleNo" placeholder="请输入扎包号" clearable />
        </el-form-item>
        <el-form-item label="工人">
          <el-select v-model="queryParams.workerId" placeholder="请选择工人" filterable clearable>
            <el-option
              v-for="item in workerOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="queryParams.auditStatus" placeholder="请选择" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已审核" :value="1" />
          </el-select>
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
        <el-table-column prop="processName" label="工序" />
        <el-table-column prop="workerName" label="工人" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="price" label="单价" />
        <el-table-column prop="amount" label="金额" />
        <el-table-column prop="auditStatus" label="审核状态">
          <template #default="{ row }">
            <el-tag :type="row.auditStatus === 1 ? 'success' : 'warning'">
              {{ row.auditStatus === 1 ? '已审核' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button v-if="row.auditStatus === 0" link type="primary" @click="handleAudit(row)">审核</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPieceRecordList, auditPieceRecord } from '@/api/mes'
import { getStaffOptions } from '@/api/settings'

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  bundleNo: '',
  workerId: null as number | null,
  auditStatus: null as number | null
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const workerOptions = ref([])

const getList = async () => {
  loading.value = true
  try {
    const res = await getPieceRecordList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadOptions = async () => {
  try {
    const res = await getStaffOptions()
    workerOptions.value = res.data || []
  } catch {
    workerOptions.value = []
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.bundleNo = ''
  queryParams.workerId = null
  queryParams.auditStatus = null
  handleQuery()
}

const handleAudit = (row: any) => {
  ElMessageBox.confirm('确认审核通过?', '提示', {
    type: 'warning'
  }).then(() => {
    auditPieceRecord(row.id, 1).then(() => {
      ElMessage.success('审核成功')
      getList()
    })
  })
}

onMounted(() => {
  getList()
  loadOptions()
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
