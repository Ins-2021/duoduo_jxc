<template>
  <div class="cost-center-page">
    <el-card>
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="中心编码">
          <el-input v-model="queryForm.centerCode" placeholder="中心编码" clearable />
        </el-form-item>
        <el-form-item label="中心名称">
          <el-input v-model="queryForm.centerName" placeholder="中心名称" clearable />
        </el-form-item>
        <el-form-item label="中心类型">
          <el-select v-model="queryForm.centerType" placeholder="中心类型" clearable>
            <el-option label="生产中心" :value="0" />
            <el-option label="车间" :value="1" />
            <el-option label="管理部门" :value="2" />
            <el-option label="销售部门" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-operations">
        <el-button type="primary" v-perm="'cost:center:add'" @click="handleAdd">新增成本中心</el-button>
      </div>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="centerCode" label="中心编码" width="140" />
        <el-table-column prop="centerName" label="中心名称" width="180" />
        <el-table-column prop="centerType" label="中心类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.centerType === 0" type="primary">生产中心</el-tag>
            <el-tag v-else-if="row.centerType === 1" type="warning">车间</el-tag>
            <el-tag v-else-if="row.centerType === 2" type="info">管理部门</el-tag>
            <el-tag v-else-if="row.centerType === 3" type="success">销售部门</el-tag>
            <span v-else>{{ row.centerType }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="parentId" label="上级中心ID" width="120" />
        <el-table-column prop="managerId" label="负责人ID" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-perm="'cost:center:edit'" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-perm="'cost:center:delete'" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="120px" :disabled="dialogTitle === '查看成本中心'">
        <el-form-item label="中心编码" required>
          <el-input v-model="formData.centerCode" placeholder="请输入中心编码" />
        </el-form-item>
        <el-form-item label="中心名称" required>
          <el-input v-model="formData.centerName" placeholder="请输入中心名称" />
        </el-form-item>
        <el-form-item label="中心类型" required>
          <el-select v-model="formData.centerType" placeholder="请选择">
            <el-option label="生产中心" :value="0" />
            <el-option label="车间" :value="1" />
            <el-option label="管理部门" :value="2" />
            <el-option label="销售部门" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="上级中心ID">
          <el-input-number v-model="formData.parentId" :min="0" />
        </el-form-item>
        <el-form-item label="负责人ID">
          <el-input-number v-model="formData.managerId" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="dialogTitle !== '查看成本中心'" type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCostCenterPage, getCostCenterDetail, createCostCenter, updateCostCenter, deleteCostCenter } from '@/api/cost'

const queryForm = reactive({ pageNum: 1, pageSize: 10, centerCode: '', centerName: '', centerType: undefined as number | undefined, status: undefined as number | undefined })
const formData = reactive({
  costCenterId: undefined as number | undefined,
  centerCode: '', centerName: '', centerType: 0,
  parentId: undefined as number | undefined,
  managerId: undefined as number | undefined,
  status: 1, remark: ''
})
const tableData = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getCostCenterPage(queryForm)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch { ElMessage.error('查询失败') }
  finally { loading.value = false }
}

const handleReset = () => {
  queryForm.centerCode = ''; queryForm.centerName = ''; queryForm.centerType = undefined; queryForm.status = undefined
  queryForm.pageNum = 1
  handleQuery()
}

const resetForm = () => Object.assign(formData, {
  costCenterId: undefined, centerCode: '', centerName: '', centerType: 0,
  parentId: undefined, managerId: undefined, status: 1, remark: ''
})

const handleAdd = () => { dialogTitle.value = '新增成本中心'; resetForm(); dialogVisible.value = true }

const handleView = async (row: any) => {
  dialogTitle.value = '查看成本中心'
  try {
    const res = await getCostCenterDetail(row.costCenterId)
    Object.assign(formData, res.data)
  } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}

const handleEdit = async (row: any) => {
  dialogTitle.value = '编辑成本中心'
  try {
    const res = await getCostCenterDetail(row.costCenterId)
    Object.assign(formData, res.data)
  } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (formData.costCenterId) {
      await updateCostCenter(formData.costCenterId, formData)
      ElMessage.success('更新成功')
    } else {
      await createCostCenter(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch { ElMessage.error('操作失败') }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该成本中心吗？', '提示', { type: 'warning' })
    await deleteCostCenter(row.costCenterId)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => handleQuery())
</script>

<style scoped>
.table-operations { margin-bottom: 16px; }
</style>
