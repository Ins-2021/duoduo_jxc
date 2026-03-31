<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <span>扎包二维码管理</span>
      </template>

      <el-form :inline="true" :model="queryParams" style="margin-bottom: 16px;">
        <el-form-item label="扎包号">
          <el-input v-model="queryParams.bundleNo" placeholder="请输入扎包号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="款号">
          <el-input v-model="queryParams.styleNo" placeholder="请输入款号" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="bundleNo" label="扎包号" width="150" />
        <el-table-column prop="styleNo" label="款号" width="120" />
        <el-table-column prop="color" label="颜色" width="80" />
        <el-table-column prop="size" label="尺码" width="70" />
        <el-table-column prop="quantity" label="数量" width="70" />
        <el-table-column prop="qrCodeNo" label="二维码编号" width="180" />
        <el-table-column label="二维码" width="100">
          <template #default="{ row }">
            <el-image v-if="row.qrCodeUrl" :src="row.qrCodeUrl" style="width: 60px; height: 60px" :preview-src-list="[row.qrCodeUrl]" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleGenerateQr(row)" v-if="!row.qrCodeNo">生成二维码</el-button>
            <el-button link type="primary" @click="handlePrintQr(row)" v-if="row.qrCodeNo">打印</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 16px; justify-content: flex-end;"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

defineOptions({ name: 'ProductionBundleQR' })

const loading = ref(false)
const total = ref(0)
const tableData = ref<any[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  bundleNo: '',
  styleNo: ''
})

const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    tableData.value = []
    total.value = 0
    loading.value = false
  }, 500)
}

const handleReset = () => {
  queryParams.bundleNo = ''
  queryParams.styleNo = ''
  queryParams.pageNum = 1
  handleQuery()
}

const handleGenerateQr = (row: any) => {
  ElMessage.success(`已为扎包 ${row.bundleNo} 生成二维码`)
  handleQuery()
}

const handlePrintQr = (row: any) => {
  ElMessage.info(`打印扎包 ${row.bundleNo} 的二维码`)
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>
