<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input
          v-model="queryParams.keyword"
          placeholder="客户名称/商品名称"
          style="width: 200px"
          class="filter-item"
          @keyup.enter="handleQuery"
        />
        <el-date-picker
          v-model="queryParams.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 240px"
          class="filter-item"
        />
        <el-button class="filter-item" type="primary" @click="handleQuery">查询</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column prop="exchangeId" label="ID" width="80" />
        <el-table-column prop="customerName" label="客户名称" min-width="120" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="points" label="消耗积分" min-width="100">
          <template #default="scope">
            <span style="color: #f56c6c">{{ scope.row.points }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="兑换数量" min-width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
              {{ scope.row.status === 1 ? '已兑换' : '待发放' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="兑换时间" width="160" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button v-if="scope.row.status !== 1" type="primary" link @click="handleConfirm(scope.row)">确认发放</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  dateRange: [] as string[]
})

const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    list.value = []
    total.value = 0
    loading.value = false
  }, 300)
}

const handleConfirm = (row: any) => {
  ElMessageBox.confirm('确认发放该商品？', '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('发放成功')
    handleQuery()
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
