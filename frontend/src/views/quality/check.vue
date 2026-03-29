<template>
  <div class="quality-check-container">
    <el-card shadow="never">
      <div class="toolbar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">
        <div><el-button type="primary">新增质检记录</el-button></div>
        <div style="display: flex; gap: 10px;">
          <el-input v-model="queryParams.keyword" placeholder="质检单号" clearable style="width: 200px" />
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </div>
      </div>
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column prop="checkNo" label="质检单号" />
        <el-table-column prop="result" label="结果" />
        <el-table-column prop="checkQuantity" label="检验数量" />
        <el-table-column prop="qualifiedQuantity" label="合格数量" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getQualityCheckList } from '@/api/quality'

const loading = ref(false)
const tableData = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getQualityCheckList(queryParams)
    tableData.value = res.data.list || []
  } catch (e) {} finally { loading.value = false }
}
onMounted(() => handleQuery())
</script>
