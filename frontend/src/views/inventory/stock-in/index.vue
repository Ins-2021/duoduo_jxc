<template>
  <div class="stock-in-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="单据编号">
          <el-input v-model="queryForm.stockNo" placeholder="单据编号" clearable />
        </el-form-item>
        <el-form-item label="入库类型">
          <el-select v-model="queryForm.stockType" placeholder="入库类型" clearable>
            <el-option label="生产入库" value="生产入库" />
            <el-option label="退货入库" value="退货入库" />
            <el-option label="其他入库" value="其他入库" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已完成" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <el-button type="primary" v-perm="'inventory:stock-in:add'" @click="handleAdd">新增入库</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="stockNo" label="单据编号" width="180" />
        <el-table-column prop="stockType" label="入库类型" width="120" />
        <el-table-column prop="warehouseName" label="仓库" width="120" />
        <el-table-column prop="stockDate" label="入库日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else type="success">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-perm="'inventory:stock-in:audit'" link type="success" @click="handleApprove(row)" v-if="row.status === 0">审核</el-button>
            <el-button v-perm="'inventory:stock-in:delete'" link type="danger" @click="handleDelete(row)" v-if="row.status === 0">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="入库类型">
          <el-select v-model="formData.stockType">
            <el-option label="生产入库" value="生产入库" />
            <el-option label="退货入库" value="退货入库" />
            <el-option label="其他入库" value="其他入库" />
          </el-select>
        </el-form-item>
        <el-form-item label="仓库">
          <el-select v-model="formData.warehouseName">
            <el-option label="仓库A" value="仓库A" />
            <el-option label="仓库B" value="仓库B" />
          </el-select>
        </el-form-item>
        <el-form-item label="入库日期">
          <el-date-picker v-model="formData.stockDate" type="date" />
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
import { stockInOutApi } from '@/api/inventory'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  stockNo: '',
  stockType: '',
  status: undefined
})

const formData = reactive({
  stockId: undefined,
  stockNo: '',
  stockType: '其他入库',
  stockDirection: 1,
  warehouseName: '',
  stockDate: '',
  remark: ''
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const handleQuery = async () => {
  try {
    const res = await stockInOutApi.pageList({ ...queryForm, stockDirection: 1 })
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.stockNo = ''
  queryForm.stockType = ''
  queryForm.status = undefined
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增入库'
  Object.assign(formData, {
    stockId: undefined,
    stockNo: '',
    stockType: '其他入库',
    stockDirection: 1,
    warehouseName: '',
    stockDate: new Date().toISOString().slice(0, 10),
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row: any) => {
  try {
    const res = await stockInOutApi.getById(row.stockId)
    dialogTitle.value = '查看入库'
    Object.assign(formData, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleSubmit = async () => {
  try {
    if (formData.stockId) {
      await stockInOutApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await stockInOutApi.create(formData)
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
    await ElMessageBox.confirm('确定要审核该入库单吗？', '提示', { type: 'warning' })
    await stockInOutApi.approve(row.stockId)
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
    await ElMessageBox.confirm('确定要删除该入库单吗？', '提示', { type: 'warning' })
    await stockInOutApi.delete(row.stockId)
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
