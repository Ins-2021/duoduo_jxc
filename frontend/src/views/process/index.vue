<template>
  <div class="process-container">
    <el-card shadow="never">
      <div class="toolbar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">
        <div><el-button type="primary">新增工序</el-button></div>
        <div style="display: flex; gap: 10px;">
          <el-input v-model="queryParams.keyword" placeholder="工序名称" clearable style="width: 200px" />
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </div>
      </div>
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column prop="processCode" label="工序编码" />
        <el-table-column prop="processName" label="工序名称" />
        <el-table-column prop="processType" label="类型" />
        <el-table-column prop="standardPrice" label="标准单价" />
        <el-table-column label="操作" width="150">
          <template #default>
            <el-button link type="primary">编辑</el-button>
            <el-button link type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :total="total" layout="total, prev, pager, next" @current-change="handleQuery" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getProcessList } from '@/api/process'

defineOptions({ name: 'ProcessManager' })

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getProcessList(queryParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {} finally {
    loading.value = false
  }
}

onMounted(() => { handleQuery() })
</script>
