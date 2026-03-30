<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>商品分类维护</span>
        </div>
      </template>

      <div class="toolbar">
        <el-button type="primary" icon="Plus" v-perm="'data:category:add'" @click="handleAdd">添加分类</el-button>
        <el-button type="success" icon="Edit" v-perm="'data:category:edit'" :disabled="!singleSelected" @click="handleUpdate">修改分类</el-button>
        <el-button type="danger" icon="Delete" v-perm="'data:category:delete'" :disabled="!multipleSelected" @click="handleDeleteSelected">删除分类</el-button>
        <el-button type="info" icon="Upload" @click="handleImport">导入分类</el-button>
      </div>

      <div class="tree-container">
        <el-tree
          ref="treeRef"
          :data="categoryTree"
          node-key="id"
          show-checkbox
          default-expand-all
          :expand-on-click-node="false"
          class="custom-tree"
          :props="defaultProps"
          @check="handleCheck"
        >
          <template #default="{ node, data }">
            <div class="custom-tree-node">
              <el-icon class="folder-icon" v-if="data.children && data.children.length > 0"><Folder /></el-icon>
              <el-icon class="file-icon" v-else><Document /></el-icon>
              <span>{{ node.label }}</span>
              
              <div class="node-actions">
                <el-button link type="primary" size="small" @click.stop="handleEdit(data)">编辑</el-button>
                <el-button link type="danger" size="small" @click.stop="handleDelete(node, data)">删除</el-button>
              </div>
            </div>
          </template>
        </el-tree>
      </div>
    </el-card>

    <!-- 添加/编辑分类弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级分类">
          <el-tree-select
            v-model="form.parentId"
            :data="parentCategoryOptions"
            :props="defaultProps"
            node-key="id"
            check-strictly
            placeholder="请选择上级分类(不选则为顶级分类)"
            style="width: 100%"
            clearable
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" controls-position="right" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 导入分类弹窗 -->
    <el-dialog title="导入商品分类" v-model="importDialogVisible" width="500px" append-to-body>
      <div class="import-tips">
        <el-alert
          title="导入说明"
          type="info"
          :closable="false"
        >
          <template #default>
            <div>1. 请先下载导入模板，按模板格式填写数据</div>
            <div>2. 支持导入字段：分类名称、上级分类、排序</div>
            <div>3. 若上级分类不存在，将自动创建为顶级分类</div>
          </template>
        </el-alert>
      </div>
      
      <div class="import-actions" style="margin-top: 20px;">
        <el-button type="primary" plain @click="downloadTemplate">
          <el-icon><Download /></el-icon>下载导入模板
        </el-button>
      </div>
      
      <el-upload
        class="upload-area"
        drag
        action="/api/import-export/import/product-category"
        :headers="uploadHeaders"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        :before-upload="beforeUpload"
        accept=".xlsx,.xls"
        style="margin-top: 20px;"
      >
        <el-icon class="el-icon--upload"><Upload /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            仅支持 .xlsx 或 .xls 格式的Excel文件
          </div>
        </template>
      </el-upload>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="importDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, ElTree } from 'element-plus'
import { Folder, Document, Plus, Upload, Edit, Delete, Download } from '@element-plus/icons-vue'
import { getProductCategoryTree, addProductCategory, updateProductCategory, deleteProductCategory } from '@/api/product'

// 树形配置
const treeRef = ref<InstanceType<typeof ElTree>>()
const defaultProps = {
  children: 'children',
  label: 'label',
  value: 'id'
}

// 选中状态
const singleSelected = ref(false)
const multipleSelected = ref(false)
const selectedNode = ref<any>(null)
const selectedNodes = ref<any[]>([])

// 分类数据
const categoryTree = ref<any[]>([])

// 获取用于选择上级的树(包含根节点)
const parentCategoryOptions = computed(() => {
  return [
    { id: 0, label: '无(顶级分类)' },
    ...categoryTree.value
  ]
})

// 表单相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const form = reactive({
  id: undefined as number | undefined,
  parentId: undefined as number | undefined,
  name: '',
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

// 获取分类数据(实际应调用API)
const fetchCategories = async () => {
  try {
    const res = await getProductCategoryTree()
    categoryTree.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

onMounted(() => {
  fetchCategories()
})

const resetForm = () => {
  form.id = undefined
  form.parentId = undefined
  form.name = ''
  form.sort = 0
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '添加分类'
  dialogVisible.value = true
}

// 导入相关
const importDialogVisible = ref(false)
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return {
    Authorization: `Bearer ${token}`
  }
})

const handleImport = () => {
  importDialogVisible.value = true
}

const downloadTemplate = () => {
  // 下载导入模板
  const link = document.createElement('a')
  link.href = '/api/import-export/template/product-category'
  link.download = '商品分类导入模板.xlsx'
  link.click()
  ElMessage.success('模板下载中...')
}

const beforeUpload = (file: File) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                  file.type === 'application/vnd.ms-excel'
  if (!isExcel) {
    ElMessage.error('请上传Excel文件(.xlsx或.xls)')
    return false
  }
  return true
}

const handleImportSuccess = (response: any) => {
  if (response.code === 200) {
    ElMessage.success(`导入成功！共导入${response.data.successCount || 0}条数据`)
    importDialogVisible.value = false
    fetchCategories() // 刷新列表
  } else {
    ElMessage.error(response.message || '导入失败')
  }
}

const handleImportError = (error: any) => {
  console.error('导入失败:', error)
  ElMessage.error('导入失败，请检查文件格式是否正确')
}

const handleEdit = (data: any) => {
  resetForm()
  form.id = data.id
  form.name = data.label
  form.sort = data.sort || 0
  
  // 使用后端返回的parentId，如果没有则默认顶级(0)
  form.parentId = data.parentId || 0
  
  dialogTitle.value = '编辑分类'
  dialogVisible.value = true
}

const handleUpdate = () => {
  if (selectedNode.value) {
    handleEdit(selectedNode.value)
  }
}

const handleCheck = () => {
  const nodes = treeRef.value?.getCheckedNodes(false, false) || []
  selectedNodes.value = nodes
  singleSelected.value = nodes.length === 1
  multipleSelected.value = nodes.length > 0
  if (nodes.length === 1) {
    selectedNode.value = nodes[0]
  } else {
    selectedNode.value = null
  }
}

const handleDeleteSelected = () => {
  if (!selectedNodes.value.length) return
  
  const hasChildren = selectedNodes.value.some(node => node.children && node.children.length > 0)
  if (hasChildren) {
    ElMessage.warning('选中的分类中包含子分类，无法直接删除')
    return
  }

  ElMessageBox.confirm(`确认删除选中的 ${selectedNodes.value.length} 个分类吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      // 如果后端支持批量删除可以调用批量接口，目前循环调用单个删除
      for (const node of selectedNodes.value) {
        await deleteProductCategory(node.id)
      }
      ElMessage.success('删除成功')
      fetchCategories()
      treeRef.value?.setCheckedKeys([])
      handleCheck()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

const handleDelete = (node: any, data: any) => {
  if (data.children && data.children.length > 0) {
    ElMessage.warning('该分类下包含子分类，无法直接删除')
    return
  }
  
  ElMessageBox.confirm(`确认删除分类 "${data.label}" 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteProductCategory(data.id)
      ElMessage.success('删除成功')
      fetchCategories()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

const submitForm = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        const data = {
          id: form.id,
          parentId: form.parentId === 0 ? undefined : form.parentId,
          name: form.name,
          sort: form.sort
        }
        
        if (form.id) {
          await updateProductCategory(data)
          ElMessage.success('修改成功')
        } else {
          await addProductCategory(data)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        fetchCategories()
      } catch (error) {
        console.error('保存失败:', error)
      }
    }
  })
}
</script>

<style scoped>
.box-card {
  border-radius: 4px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.toolbar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.toolbar .el-button {
  background-color: #1890ff;
  border-color: #1890ff;
}

.toolbar .el-button:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
}

.tree-container {
  padding: 10px 0;
}

/* 自定义树样式以匹配设计图 */
.custom-tree {
  font-size: 14px;
}

:deep(.el-tree-node__content) {
  height: 40px;
}

:deep(.el-tree-node__expand-icon) {
  color: #606266;
  font-size: 16px;
}

/* 虚线连接线效果 */
:deep(.el-tree-node) {
  position: relative;
  padding-left: 16px;
}

:deep(.el-tree-node::before) {
  content: '';
  position: absolute;
  top: -20px;
  bottom: 0;
  left: 0;
  width: 1px;
  border-left: 1px dashed #dcdfe6;
}

:deep(.el-tree-node:last-child::before) {
  bottom: 20px;
}

:deep(.el-tree-node::after) {
  content: '';
  position: absolute;
  top: 20px;
  left: 0;
  width: 14px;
  height: 1px;
  border-top: 1px dashed #dcdfe6;
}

/* 隐藏顶级节点的连接线 */
:deep(.el-tree > .el-tree-node::before),
:deep(.el-tree > .el-tree-node::after) {
  display: none;
}
:deep(.el-tree > .el-tree-node) {
  padding-left: 0;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #303133;
  padding-right: 8px;
}

.folder-icon, .file-icon {
  margin-right: 8px;
  font-size: 16px;
  color: #606266;
}

.folder-icon {
  color: #303133;
}

.node-actions {
  margin-left: auto;
}

/* 复选框样式调整 */
:deep(.el-checkbox__inner) {
  border-color: #dcdfe6;
}
</style>