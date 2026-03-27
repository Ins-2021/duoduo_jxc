<template>
  <div class="doc-no-rule-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm" @submit.native.prevent="handleQuery">
        <el-form-item label="单据名称">
          <el-input v-model="queryForm.docName" placeholder="请输入单据名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" @click="handleQuery">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <el-button type="primary" v-perm="'settings:doc-no-rule:add'" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增规则
        </el-button>
        <el-button v-perm="'settings:doc-no-rule:edit'" type="success" :disabled="selectedRows.length === 0" @click="handleBatchToggle(1)">
          <el-icon><Check /></el-icon>
          批量启用
        </el-button>
        <el-button v-perm="'settings:doc-no-rule:edit'" type="warning" :disabled="selectedRows.length === 0" @click="handleBatchToggle(0)">
          <el-icon><Close /></el-icon>
          批量禁用
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="docType" label="单据类型" min-width="180" align="center" />
        <el-table-column prop="docName" label="单据名称" width="120" />
        <el-table-column label="规则格式" min-width="200">
          <template #default="{ row }">
            <el-tag v-if="row.prefix" type="info" size="small">{{ row.prefix }}</el-tag>
            <el-tag v-if="row.includeYear === 1" type="success" size="small">Y</el-tag>
            <el-tag v-if="row.includeMonth === 1" type="warning" size="small">M</el-tag>
            <el-tag v-if="row.includeDay === 1" type="danger" size="small">D</el-tag>
            <el-tag v-if="row.useRandom === 1" type="primary" size="small">随机{{ row.randomLength }}位</el-tag>
            <el-tag v-else type="primary" size="small">{{ '#'.repeat(row.seqLength || 4) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预览" width="170">
          <template #default="{ row }">
            <code class="preview-code">{{ getPreview(row) }}</code>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              active-color="#13ce66"
              inactive-color="#ff4949"
              @change="handleToggleStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
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
        background
        style="margin-top: 16px; justify-content: flex-end"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="单据类型" prop="docType">
          <el-input
            v-model="formData.docType"
            placeholder="请输入单据类型，如：SALES_ORDER"
            maxlength="50"
          />
        </el-form-item>
        <el-form-item label="单据名称" prop="docName">
          <el-input v-model="formData.docName" placeholder="请输入单据名称，如：销售单" maxlength="50" />
        </el-form-item>
        <el-form-item label="前缀" prop="prefix">
          <el-input
            v-model="formData.prefix"
            placeholder="请输入编号前缀，如：XS"
            maxlength="10"
            style="width: 200px"
          />
          <span class="form-tip">建议使用2-4位字母或数字</span>
        </el-form-item>
        <el-form-item label="包含日期">
          <el-checkbox v-model="formData.includeYear" :true-value="1" :false-value="0">
            年份 (YYYY)
          </el-checkbox>
          <el-checkbox v-model="formData.includeMonth" :true-value="1" :false-value="0">
            月份 (MM)
          </el-checkbox>
          <el-checkbox v-model="formData.includeDay" :true-value="1" :false-value="0">
            日期 (DD)
          </el-checkbox>
        </el-form-item>
        <el-form-item label="生成方式">
          <el-radio-group v-model="formData.useRandom">
            <el-radio :value="0">流水号</el-radio>
            <el-radio :value="1">随机数</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formData.useRandom === 0" label="流水号长度" prop="seqLength">
          <el-input-number v-model="formData.seqLength" :min="2" :max="10" controls-position="right" />
          <span class="form-tip">推荐值为 4-6 位</span>
        </el-form-item>
        <el-form-item v-if="formData.useRandom === 1" label="随机数长度" prop="randomLength">
          <el-input-number v-model="formData.randomLength" :min="4" :max="12" controls-position="right" />
          <span class="form-tip">推荐值为 6-8 位</span>
        </el-form-item>
        <el-form-item label="预览">
          <el-input :value="getPreview(formData)" readonly class="preview-input" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Search, RefreshLeft, Plus, Check, Close, Edit, Delete } from '@element-plus/icons-vue'
import { docNoRuleApi } from '@/api/system'

interface DocNoRule {
  ruleId?: number
  docType: string
  docName: string
  prefix: string
  includeYear: number
  includeMonth: number
  includeDay: number
  seqLength: number
  useRandom: number
  randomLength: number
  status: number
  remark?: string
}

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<DocNoRule[]>([])
const total = ref(0)
const selectedRows = ref<DocNoRule[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增单号规则')
const formRef = ref<FormInstance>()

const queryForm = reactive({
  docName: '',
  status: undefined as number | undefined,
  pageNum: 1,
  pageSize: 10
})

const formData = reactive<DocNoRule>({
  docType: '',
  docName: '',
  prefix: '',
  includeYear: 1,
  includeMonth: 1,
  includeDay: 0,
  seqLength: 4,
  useRandom: 0,
  randomLength: 6,
  status: 1,
  remark: ''
})

const formRules = computed(() => ({
  docType: [{ required: true, message: '请输入单据类型', trigger: 'blur' }],
  docName: [{ required: true, message: '请输入单据名称', trigger: 'blur' }],
  prefix: [{ required: true, message: '请输入编号前缀', trigger: 'blur' }],
  seqLength: formData.useRandom === 0 ? [{ required: true, message: '请输入流水号长度', trigger: 'blur' }] : [],
  randomLength: formData.useRandom === 1 ? [{ required: true, message: '请输入随机数长度', trigger: 'blur' }] : []
}))

const getPreview = (rule: DocNoRule) => {
  const now = new Date()
  let preview = ''
  if (rule.prefix) preview += rule.prefix
  if (rule.includeYear === 1) preview += now.getFullYear()
  if (rule.includeMonth === 1) preview += String(now.getMonth() + 1).padStart(2, '0')
  if (rule.includeDay === 1) preview += String(now.getDate()).padStart(2, '0')
  if (rule.useRandom === 1) {
    preview += 'X'.repeat(rule.randomLength || 6)
  } else {
    preview += '1'.padStart(rule.seqLength || 4, '0')
  }
  return preview || '(未设置规则)'
}

const buildParams = () => {
  const params: Record<string, any> = {}
  if (queryForm.docName) params.docName = queryForm.docName
  if (queryForm.status !== undefined && queryForm.status !== null) params.status = queryForm.status
  return {
    pageNum: queryForm.pageNum,
    pageSize: queryForm.pageSize,
    params
  }
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await docNoRuleApi.pageList(buildParams())
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.docName = ''
  queryForm.status = undefined
  queryForm.pageNum = 1
  handleQuery()
}

const handleSelectionChange = (selection: DocNoRule[]) => {
  selectedRows.value = selection
}

const handleAdd = () => {
  dialogTitle.value = '新增单号规则'
  Object.assign(formData, {
    ruleId: undefined,
    docType: '',
    docName: '',
    prefix: '',
    includeYear: 1,
    includeMonth: 1,
    includeDay: 0,
    seqLength: 4,
    useRandom: 0,
    randomLength: 6,
    status: 1,
    remark: ''
  })
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

const handleEdit = (row: DocNoRule) => {
  dialogTitle.value = '编辑单号规则'
  Object.assign(formData, { ...row })
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    if (formData.ruleId) {
      await docNoRuleApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await docNoRuleApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch {
    // 请求拦截器已提示错误
  } finally {
    submitting.value = false
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleDelete = async (row: DocNoRule) => {
  try {
    await ElMessageBox.confirm('确定要删除该单号规则吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await docNoRuleApi.delete(row.ruleId!)
    ElMessage.success('删除成功')
    handleQuery()
  } catch {
    // 用户取消
  }
}

const handleToggleStatus = async (row: DocNoRule) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await docNoRuleApi.toggleStatus(row.ruleId!)
    ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
  } catch {
    // 回滚状态
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleBatchToggle = async (status: number) => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要操作的规则')
    return
  }
  const label = status === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定要${label}选中的 ${selectedRows.value.length} 条规则吗？`, '提示', {
      type: 'warning'
    })
    const ids = selectedRows.value.map(r => r.ruleId!).filter(Boolean)
    await docNoRuleApi.batchToggleStatus(ids, status)
    ElMessage.success(`批量${label}成功`)
    handleQuery()
  } catch {
    // 用户取消
  }
}

handleQuery()
</script>

<style scoped>
.doc-no-rule-page {
  padding: 16px;
}

.table-operations {
  margin: 16px 0;
  display: flex;
  gap: 8px;
}

.form-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}

.preview-code {
  padding: 2px 6px;
  background-color: #f5f7fa;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  color: #606266;
  font-family: 'Courier New', monospace;
  font-size: 13px;
}

.preview-input :deep(.el-input__inner) {
  background-color: #f5f7fa;
  color: #606266;
  font-family: 'Courier New', monospace;
}
</style>
