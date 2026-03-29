<template>
  <div class="barcode-rule-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="规则名称">
          <el-input v-model="queryForm.keyword" placeholder="规则名称/前缀" clearable />
        </el-form-item>
        <el-form-item label="规则类型">
          <el-select v-model="queryForm.ruleType" placeholder="全部" clearable>
            <el-option label="SKU码" value="SKU" />
            <el-option label="箱码" value="BOX" />
            <el-option label="批次码" value="BATCH" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <el-button type="primary" @click="handleAdd">新增规则</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="ruleName" label="规则名称" width="150" />
        <el-table-column prop="ruleType" label="规则类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.ruleType === 'SKU'" type="primary">SKU码</el-tag>
            <el-tag v-else-if="row.ruleType === 'BOX'" type="warning">箱码</el-tag>
            <el-tag v-else-if="row.ruleType === 'BATCH'" type="success">批次码</el-tag>
            <el-tag v-else type="info">{{ row.ruleType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="prefix" label="前缀" width="120" />
        <el-table-column prop="dateFormat" label="日期格式" width="120" />
        <el-table-column prop="seqLength" label="序列号长度" width="100" />
        <el-table-column prop="ruleExpression" label="规则表达式" min-width="180" />
        <el-table-column prop="example" label="示例" width="200" />
        <el-table-column prop="isDefault" label="默认" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault === 1" type="success">是</el-tag>
            <span v-else>否</span>
          </template>
        </el-table-column>
        <el-table-column prop="isActive" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isActive === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
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
        <el-form-item label="规则名称">
          <el-input v-model="formData.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="规则类型">
          <el-select v-model="formData.ruleType">
            <el-option label="SKU码" value="SKU" />
            <el-option label="箱码" value="BOX" />
            <el-option label="批次码" value="BATCH" />
          </el-select>
        </el-form-item>
        <el-form-item label="前缀">
          <el-input v-model="formData.prefix" placeholder="如 SKU" />
        </el-form-item>
        <el-form-item label="日期格式">
          <el-input v-model="formData.dateFormat" placeholder="如 yyyyMMdd" />
        </el-form-item>
        <el-form-item label="序列号长度">
          <el-input-number v-model="formData.seqLength" :min="2" :max="10" />
        </el-form-item>
        <el-form-item label="规则表达式">
          <el-input v-model="formData.ruleExpression" placeholder="如 {prefix}{date}{seq}" />
        </el-form-item>
        <el-form-item label="示例">
          <el-input v-model="formData.example" placeholder="自动生成示例" />
        </el-form-item>
        <el-form-item label="默认规则">
          <el-switch v-model="formData.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="formData.isActive" :active-value="1" :inactive-value="0" />
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
import { getRulePage, addRule, updateRule, deleteRule } from '@/api/barcode'
import type { BarcodeRuleDTO } from '@/api/barcode'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  ruleType: ''
})

const formData = reactive<BarcodeRuleDTO>({
  ruleId: undefined,
  ruleName: '',
  ruleType: 'SKU',
  prefix: '',
  dateFormat: 'yyyyMMdd',
  seqLength: 4,
  ruleExpression: '',
  example: '',
  isDefault: 0,
  isActive: 1
})

const tableData = ref<BarcodeRuleDTO[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const handleQuery = async () => {
  try {
    const res = await getRulePage(queryForm)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.ruleType = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增规则'
  Object.assign(formData, {
    ruleId: undefined, ruleName: '', ruleType: 'SKU', prefix: '',
    dateFormat: 'yyyyMMdd', seqLength: 4, ruleExpression: '',
    example: '', isDefault: 0, isActive: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row: BarcodeRuleDTO) => {
  dialogTitle.value = '编辑规则'
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (formData.ruleId) {
      await updateRule(formData.ruleId, formData)
      ElMessage.success('更新成功')
    } else {
      await addRule(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row: BarcodeRuleDTO) => {
  try {
    await ElMessageBox.confirm('确定要删除该规则吗？', '提示', { type: 'warning' })
    await deleteRule(row.ruleId!)
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
