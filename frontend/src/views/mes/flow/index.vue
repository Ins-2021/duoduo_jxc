<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <span>扎包流转管理</span>
      </template>

      <el-form :inline="true" :model="queryParams" style="margin-bottom: 16px;">
        <el-form-item label="扎包号">
          <el-input v-model="queryParams.bundleNo" placeholder="请输入扎包号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="流转状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="流转中" value="in_progress" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column prop="bundleNo" label="扎包号" width="150" />
        <el-table-column prop="styleNo" label="款号" width="120" />
        <el-table-column prop="color" label="颜色" width="80" />
        <el-table-column prop="size" label="尺码" width="70" />
        <el-table-column prop="quantity" label="数量" width="70" />
        <el-table-column prop="currentProcess" label="当前工序" width="120" />
        <el-table-column prop="currentWorker" label="当前工位" width="100" />
        <el-table-column prop="completedProcesses" label="已完成工序" width="150">
          <template #default="{ row }">
            <el-tag v-for="p in (row.completedProcesses || [])" :key="p" size="small" style="margin-right: 4px;">{{ p }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : 'primary'">
              {{ row.status === 'completed' ? '已完成' : '流转中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button link type="primary" @click="handleTransfer(row)" v-if="row.status !== 'completed'">流转</el-button>
            <el-button link type="primary" @click="handleHistory(row)">历史</el-button>
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

    <el-dialog v-model="transferDialogVisible" title="扎包流转" width="400px">
      <el-form :model="transferForm" label-width="100px">
        <el-form-item label="目标工序">
          <el-select v-model="transferForm.targetProcess" placeholder="请选择工序" style="width: 100%">
            <el-option label="车缝" value="sewing" />
            <el-option label="整烫" value="ironing" />
            <el-option label="包装" value="packing" />
          </el-select>
        </el-form-item>
        <el-form-item label="接收工人">
          <el-select v-model="transferForm.workerId" placeholder="请选择工人" style="width: 100%">
            <el-option label="张三" :value="1" />
            <el-option label="李四" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTransfer">确认流转</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

defineOptions({ name: 'MesFlow' })

const loading = ref(false)
const total = ref(0)
const tableData = ref<any[]>([])
const transferDialogVisible = ref(false)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  bundleNo: '',
  status: ''
})

const transferForm = reactive({
  bundleId: null as number | null,
  targetProcess: '',
  workerId: null as number | null
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
  queryParams.status = ''
  queryParams.pageNum = 1
  handleQuery()
}

const handleDetail = (row: any) => {
  ElMessage.info(`查看扎包 ${row.bundleNo} 详情`)
}

const handleTransfer = (row: any) => {
  transferForm.bundleId = row.bundleId
  transferForm.targetProcess = ''
  transferForm.workerId = null
  transferDialogVisible.value = true
}

const submitTransfer = () => {
  if (!transferForm.targetProcess) {
    ElMessage.warning('请选择目标工序')
    return
  }
  ElMessage.success('流转成功')
  transferDialogVisible.value = false
  handleQuery()
}

const handleHistory = (row: any) => {
  ElMessage.info(`查看扎包 ${row.bundleNo} 流转历史`)
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
