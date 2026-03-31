<template>
  <div class="transfer-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="调拨单号">
          <el-input v-model="queryForm.transferNo" placeholder="调拨单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已审核" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <el-button type="primary" v-perm="'inventory:transfer:add'" @click="handleAdd">新增调拨</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="transferNo" label="调拨单号" width="180" />
        <el-table-column prop="outWarehouseName" label="调出仓库" width="120" />
        <el-table-column prop="inWarehouseName" label="调入仓库" width="120" />
        <el-table-column prop="transferDate" label="调拨日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="info">已审核</el-tag>
            <el-tag v-else type="success">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-perm="'inventory:transfer:audit'" link type="success" @click="handleApprove(row)" v-if="row.status === 0">审核</el-button>
            <el-button v-perm="'inventory:transfer:delete'" link type="danger" @click="handleDelete(row)" v-if="row.status === 0">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="调出仓库">
          <el-select v-model="formData.outWarehouseName">
            <el-option label="仓库A" value="仓库A" />
            <el-option label="仓库B" value="仓库B" />
          </el-select>
        </el-form-item>
        <el-form-item label="调入仓库">
          <el-select v-model="formData.inWarehouseName">
            <el-option label="仓库A" value="仓库A" />
            <el-option label="仓库B" value="仓库B" />
          </el-select>
        </el-form-item>
        <el-form-item label="调拨日期">
          <el-date-picker v-model="formData.transferDate" type="date" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { transferOrderApi } from '@/api/inventory'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  transferNo: '',
  status: undefined
})

const formData = reactive({
  transferId: undefined,
  outWarehouseName: '',
  inWarehouseName: '',
  transferDate: '',
  remark: ''
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const handleQuery = async () => {
  try {
    const res = await transferOrderApi.pageList(queryForm)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.transferNo = ''
  queryForm.status = undefined
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增调拨'
  Object.assign(formData, {
    transferId: undefined,
    outWarehouseName: '',
    inWarehouseName: '',
    transferDate: new Date(),
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row: any) => {
  try {
    const res = await transferOrderApi.getById(row.transferId)
    dialogTitle.value = '查看调拨'
    Object.assign(formData, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleSubmit = async () => {
  try {
    if (formData.transferId) {
      await transferOrderApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await transferOrderApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleApprove = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要审核该调拨单吗？', '提示', { type: 'warning' })
    await transferOrderApi.approve(row.transferId)
    ElMessage.success('审核成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('审核失败')
    }
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该调拨单吗？', '提示', { type: 'warning' })
    await transferOrderApi.delete(row.transferId)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.table-operations {
  margin-bottom: 16px;
}
</style>
