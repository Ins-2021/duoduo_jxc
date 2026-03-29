<template>
  <div class="quality-container">
    <el-card shadow="never">
      <div class="toolbar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">
        <div><el-button type="primary">新增标准</el-button></div>
        <div style="display: flex; gap: 10px;">
          <el-input v-model="queryParams.keyword" placeholder="标准名称" clearable style="width: 200px" />
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </div>
      </div>
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column prop="standardName" label="标准名称" />
        <el-table-column prop="standardType" label="类型" />
        <el-table-column prop="passStandard" label="合格标准" />
        <el-table-column label="操作" width="150">
          <template #default>
            <el-button link type="primary">编辑</el-button>
            <el-button link type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getQualityStandardList } from '@/api/quality'

const loading = ref(false)
const tableData = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getQualityStandardList(queryParams)
    tableData.value = res.data.list || []
  } catch (e) {} finally { loading.value = false }
}
onMounted(() => handleQuery())
</script>
