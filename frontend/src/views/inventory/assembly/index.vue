<template>
  <div class="assembly-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="组装单号">
          <el-input v-model="queryForm.assemblyNo" placeholder="组装单号" clearable />
        </el-form-item>
        <el-form-item label="组装类型">
          <el-select v-model="queryForm.assemblyType" placeholder="组装类型" clearable>
            <el-option label="组装" value="组装" />
            <el-option label="拆卸" value="拆卸" />
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
        <el-button type="primary" v-perm="'inventory:assembly:add'" @click="handleAdd">新增组装</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="assemblyNo" label="组装单号" width="180" />
        <el-table-column prop="assemblyType" label="组装类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.assemblyType === '组装'" type="success">组装</el-tag>
            <el-tag v-else type="warning">拆卸</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warehouseName" label="仓库" width="120" />
        <el-table-column prop="assemblyDate" label="操作日期" width="120" />
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
            <el-button link type="success" @click="handleApprove(row)" v-if="row.status === 0">审核</el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-if="row.status === 0">删除</el-button>
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
        <el-form-item label="组装类型">
          <el-select v-model="formData.assemblyType">
            <el-option label="组装" value="组装" />
            <el-option label="拆卸" value="拆卸" />
          </el-select>
        </el-form-item>
        <el-form-item label="仓库">
          <el-select v-model="formData.warehouseName">
            <el-option label="仓库A" value="仓库A" />
            <el-option label="仓库B" value="仓库B" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作日期">
          <el-date-picker v-model="formData.assemblyDate" type="date" />
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
import { assemblyOrderApi } from '@/api/inventory'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  assemblyNo: '',
  assemblyType: '',
  status: undefined
})

const formData = reactive({
  assemblyOrderId: undefined,
  assemblyType: '组装',
  warehouseName: '',
  assemblyDate: '',
  remark: ''
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const handleQuery = async () => {
  try {
    const res = await assemblyOrderApi.pageList(queryForm)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.assemblyNo = ''
  queryForm.assemblyType = ''
  queryForm.status = undefined
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增组装'
  Object.assign(formData, {
    assemblyOrderId: undefined,
    assemblyType: '组装',
    warehouseName: '',
    assemblyDate: new Date(),
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row: any) => {
  try {
    const res = await assemblyOrderApi.getById(row.assemblyOrderId)
    dialogTitle.value = '查看组装'
    Object.assign(formData, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleSubmit = async () => {
  try {
    if (formData.assemblyOrderId) {
      await assemblyOrderApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await assemblyOrderApi.create(formData)
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
    await ElMessageBox.confirm('确定要审核该组装单吗？', '提示', { type: 'warning' })
    await assemblyOrderApi.approve(row.assemblyOrderId)
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
    await ElMessageBox.confirm('确定要删除该组装单吗？', '提示', { type: 'warning' })
    await assemblyOrderApi.delete(row.assemblyOrderId)
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
