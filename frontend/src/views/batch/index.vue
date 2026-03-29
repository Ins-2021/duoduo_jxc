<template>
  <div class="batch-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="批次号">
          <el-input v-model="queryForm.batchNo" placeholder="批次号" clearable />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="批次号/商品" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <el-button type="primary" @click="handleAdd">新增批次</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="batchNo" label="批次号" width="200" />
        <el-table-column prop="skuId" label="SKU ID" width="100" />
        <el-table-column prop="warehouseId" label="仓库ID" width="100" />
        <el-table-column prop="productionDate" label="生产日期" width="120" />
        <el-table-column prop="expiryDate" label="到期日" width="120" />
        <el-table-column prop="inboundDate" label="入库日期" width="120" />
        <el-table-column prop="qty" label="数量" width="100" />
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="SKU ID">
          <el-input-number v-model="formData.skuId" :min="1" />
        </el-form-item>
        <el-form-item label="仓库ID">
          <el-input-number v-model="formData.warehouseId" :min="1" />
        </el-form-item>
        <el-form-item label="生产日期">
          <el-date-picker v-model="formData.productionDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="到期日">
          <el-date-picker v-model="formData.expiryDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="入库日期">
          <el-date-picker v-model="formData.inboundDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="formData.qty" :min="0" />
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
import { getBatchList, addBatch, updateBatch, deleteBatch } from '@/api/batch'
import type { BatchDTO } from '@/api/batch'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  batchNo: '',
  keyword: ''
})

const formData = reactive<BatchDTO>({
  batchId: undefined,
  batchNo: '',
  skuId: undefined,
  warehouseId: undefined,
  productionDate: '',
  expiryDate: '',
  inboundDate: '',
  qty: 0,
  remark: ''
})

const tableData = ref<BatchDTO[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const handleQuery = async () => {
  try {
    const res = await getBatchList(queryForm)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.batchNo = ''
  queryForm.keyword = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增批次'
  Object.assign(formData, {
    batchId: undefined, batchNo: '', skuId: undefined, warehouseId: undefined,
    productionDate: '', expiryDate: '', inboundDate: '', qty: 0, remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: BatchDTO) => {
  dialogTitle.value = '编辑批次'
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (formData.batchId) {
      await updateBatch(formData.batchId, formData)
      ElMessage.success('更新成功')
    } else {
      await addBatch(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row: BatchDTO) => {
  try {
    await ElMessageBox.confirm('确定要删除该批次吗？', '提示', { type: 'warning' })
    await deleteBatch(row.batchId!)
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
