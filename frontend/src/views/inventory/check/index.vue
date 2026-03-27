<template>
  <div class="check-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="盘点单号">
          <el-input v-model="queryForm.checkNo" placeholder="盘点单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="状态" clearable>
            <el-option label="待盘点" :value="0" />
            <el-option label="盘点中" :value="1" />
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
        <el-button type="primary" v-perm="'inventory:check:add'" @click="handleAdd">新增盘点</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="checkNo" label="盘点单号" width="180" />
        <el-table-column prop="warehouseName" label="仓库" width="120" />
        <el-table-column prop="checkDate" label="盘点日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待盘点</el-tag>
            <el-tag v-else-if="row.status === 1" type="info">盘点中</el-tag>
            <el-tag v-else type="success">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-perm="'inventory:check:audit'" link type="success" @click="handleComplete(row)" v-if="row.status === 1">完成</el-button>
            <el-button v-perm="'inventory:check:delete'" link type="danger" @click="handleDelete(row)" v-if="row.status === 0">删除</el-button>
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
        <el-form-item label="仓库">
          <el-select v-model="formData.warehouseName">
            <el-option label="仓库A" value="仓库A" />
            <el-option label="仓库B" value="仓库B" />
          </el-select>
        </el-form-item>
        <el-form-item label="盘点日期">
          <el-date-picker v-model="formData.checkDate" type="date" />
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
import { inventoryCheckApi } from '@/api/inventory'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  checkNo: '',
  status: undefined
})

const formData = reactive({
  checkId: undefined,
  warehouseName: '',
  checkDate: '',
  remark: ''
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const handleQuery = async () => {
  try {
    const res = await inventoryCheckApi.pageList(queryForm)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.checkNo = ''
  queryForm.status = undefined
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增盘点'
  Object.assign(formData, {
    checkId: undefined,
    warehouseName: '',
    checkDate: new Date(),
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row: any) => {
  try {
    const res = await inventoryCheckApi.getById(row.checkId)
    dialogTitle.value = '查看盘点'
    Object.assign(formData, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleSubmit = async () => {
  try {
    if (formData.checkId) {
      await inventoryCheckApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await inventoryCheckApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleComplete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要完成该盘点单吗？', '提示', { type: 'warning' })
    await inventoryCheckApi.complete(row.checkId)
    ElMessage.success('完成成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('完成失败')
    }
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该盘点单吗？', '提示', { type: 'warning' })
    await inventoryCheckApi.delete(row.checkId)
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
