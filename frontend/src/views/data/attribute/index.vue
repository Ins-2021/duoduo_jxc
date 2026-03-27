<template>
  <div class="app-container">
    <div class="header-bar">
      <el-button type="primary" icon="Plus" v-perm="'data:attribute:add'" @click="handleAddAttribute">添加属性</el-button>
      <span class="tips">温馨提示：点击属性块右上角删除可删除大类属性；点击修改可删除大类属性下的选项。</span>
    </div>

    <div class="attr-list">
      <el-card v-for="(attr, index) in attributes" :key="attr.id" class="attr-card" shadow="never">
        <div class="attr-header">
          <span class="attr-title">{{ attr.name }}</span>
          <div class="attr-actions">
            <span class="action-btn" @click="handleBatchEdit(attr)">批量</span>
            <span v-perm="'data:attribute:edit'" class="action-btn text-primary" @click="handleEdit(attr)">修改</span>
            <span v-perm="'data:attribute:delete'" class="action-btn text-danger" @click="handleDeleteAttr(index, attr)">删除</span>
          </div>
        </div>
        <div class="attr-body">
          <div 
            v-for="(option, optIndex) in attr.options" 
            :key="optIndex" 
            class="attr-tag"
            @click="handleEditOption(attr, Number(optIndex))"
          >
            <span class="tag-text">{{ option.value }}</span>
            <el-icon class="tag-close" @click.stop="handleRemoveOption(attr, Number(optIndex))"><Close /></el-icon>
          </div>
          <div class="attr-tag add-tag" @click="handleAddOption(attr)">
            <el-icon><Plus /></el-icon>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 添加大类属性弹窗 -->
    <el-dialog v-model="addAttrVisible" title="添加属性" width="500px" append-to-body>
      <el-form :model="addAttrForm" label-width="100px" @submit.prevent>
        <el-form-item label="属性名称">
          <el-input v-model="addAttrForm.name" placeholder="请输入属性名称" @keyup.enter="confirmAddAttr" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmAddAttr">保存</el-button>
          <el-button @click="addAttrVisible = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改大类属性弹窗 -->
    <el-dialog v-model="editAttrVisible" title="修改属性名称" width="500px" append-to-body>
      <el-form :model="editAttrForm" label-width="100px" @submit.prevent>
        <el-form-item label="属性名称">
          <el-input v-model="editAttrForm.name" placeholder="请输入属性名称" @keyup.enter="confirmEditAttr" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmEditAttr">保存</el-button>
          <el-button @click="editAttrVisible = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加/修改属性选项弹窗 -->
    <el-dialog v-model="optionVisible" :title="optionDialogTitle" width="500px" append-to-body>
      <el-form :model="optionForm" label-width="100px" @submit.prevent>
        <el-form-item label="选项名称">
          <el-input v-model="optionForm.name" placeholder="请输入选项名称" @keyup.enter="confirmOption" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmOption">保存</el-button>
          <el-button @click="optionVisible = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量维护属性选项弹窗 -->
    <el-dialog v-model="batchVisible" title="批量维护属性选项" width="600px" append-to-body>
      <div class="batch-content">
        <el-input
          v-model="batchForm.text"
          type="textarea"
          :rows="15"
          placeholder="请输入属性值一行一个"
        />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmBatch">保存</el-button>
          <el-button @click="batchVisible = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Close, Plus } from '@element-plus/icons-vue'
import {
  getAttributeList,
  addAttribute,
  updateAttribute,
  deleteAttribute,
  addAttributeOption,
  updateAttributeOption,
  deleteAttributeOption,
  batchSaveAttributeOptions
} from '@/api/attribute'

// 真实数据
const attributes = ref<any[]>([])

const fetchAttributes = async () => {
  try {
    const res = await getAttributeList()
    // 转换后端数据格式适应前端模板
    attributes.value = (res.data || []).map((attr: any) => ({
      id: attr.id,
      name: attr.name,
      sort: attr.sort,
      options: (attr.options || []).map((opt: any) => ({
        id: opt.id,
        value: opt.value
      }))
    }))
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchAttributes()
})

// 添加大类属性
const addAttrVisible = ref(false)
const addAttrForm = reactive({ name: '' })

const handleAddAttribute = () => {
  addAttrForm.name = ''
  addAttrVisible.value = true
}

const confirmAddAttr = async () => {
  if (!addAttrForm.name.trim()) {
    ElMessage.warning('请输入属性名称')
    return
  }
  try {
    await addAttribute({ name: addAttrForm.name.trim(), sort: attributes.value.length })
    ElMessage.success('添加成功')
    addAttrVisible.value = false
    fetchAttributes()
  } catch (error) {
    console.error(error)
  }
}

// 修改大类属性
const editAttrVisible = ref(false)
const editAttrForm = reactive({ id: 0, name: '' })
let currentEditAttrRef: any = null

const handleEdit = (attr: any) => {
  currentEditAttrRef = attr
  editAttrForm.id = attr.id
  editAttrForm.name = attr.name
  editAttrVisible.value = true
}

const confirmEditAttr = async () => {
  if (!editAttrForm.name.trim()) {
    ElMessage.warning('请输入属性名称')
    return
  }
  try {
    await updateAttribute({ id: editAttrForm.id, name: editAttrForm.name.trim() })
    ElMessage.success('修改成功')
    editAttrVisible.value = false
    fetchAttributes()
  } catch (error) {
    console.error(error)
  }
}

// 删除大类属性
const handleDeleteAttr = (index: number, attr: any) => {
  ElMessageBox.confirm(`确认删除属性大类 "${attr.name}" 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAttribute(attr.id)
      ElMessage.success('删除成功')
      fetchAttributes()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

// 选项操作(添加/修改)
const optionVisible = ref(false)
const optionDialogTitle = ref('')
const optionForm = reactive({ name: '' })
let currentAttrRefForOption: any = null
let currentOptionRef: any = null
let isEditOption = false

const handleAddOption = (attr: any) => {
  currentAttrRefForOption = attr
  isEditOption = false
  optionForm.name = ''
  optionDialogTitle.value = '添加属性选项'
  optionVisible.value = true
}

const handleEditOption = (attr: any, optIndex: number) => {
  currentAttrRefForOption = attr
  currentOptionRef = attr.options[optIndex]
  isEditOption = true
  optionForm.name = currentOptionRef.value
  optionDialogTitle.value = '修改属性名称'
  optionVisible.value = true
}

const confirmOption = async () => {
  const val = optionForm.name.trim()
  if (!val) {
    ElMessage.warning('请输入选项名称')
    return
  }
  
  try {
    if (!isEditOption) {
      // 新增
      await addAttributeOption(currentAttrRefForOption.id, { value: val })
      ElMessage.success('添加成功')
    } else {
      // 修改
      await updateAttributeOption(currentOptionRef.id, { value: val })
      ElMessage.success('修改成功')
    }
    optionVisible.value = false
    fetchAttributes()
  } catch (error) {
    console.error(error)
  }
}

// 删除选项
const handleRemoveOption = async (attr: any, optIndex: number) => {
  const opt = attr.options[optIndex]
  try {
    await deleteAttributeOption(opt.id)
    ElMessage.success('删除成功')
    fetchAttributes()
  } catch (error) {
    console.error(error)
  }
}

// 批量维护
const batchVisible = ref(false)
const batchForm = reactive({ text: '' })
let currentBatchAttrRef: any = null

const handleBatchEdit = (attr: any) => {
  currentBatchAttrRef = attr
  batchForm.text = attr.options.map((opt: any) => opt.value).join('\n')
  batchVisible.value = true
}

const confirmBatch = async () => {
  if (currentBatchAttrRef) {
    const lines = batchForm.text.split('\n').map(s => s.trim()).filter(Boolean)
    const uniqueValues = Array.from(new Set(lines))
    
    try {
      await batchSaveAttributeOptions(currentBatchAttrRef.id, { values: uniqueValues })
      ElMessage.success('批量保存成功')
      batchVisible.value = false
      fetchAttributes()
    } catch (error) {
      console.error(error)
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);
}

.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.tips {
  color: #909399;
  font-size: 12px;
}

.attr-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.attr-card {
  border-radius: 4px;
}

:deep(.attr-card .el-card__body) {
  padding: 0;
}

.attr-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #fdfdfd;
}

.attr-title {
  font-weight: bold;
  font-size: 15px;
  color: #303133;
}

.attr-actions {
  display: flex;
  gap: 15px;
  font-size: 13px;
}

.action-btn {
  cursor: pointer;
  color: #606266;
}

.action-btn:hover {
  opacity: 0.8;
}

.text-primary {
  color: #409eff;
}

.text-danger {
  color: #f56c6c;
}

.attr-body {
  padding: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.attr-tag {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 80px;
  height: 36px;
  padding: 0 15px;
  background-color: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  transition: all 0.2s;
}

.attr-tag:hover {
  border-color: #c0c4cc;
}

.tag-text {
  user-select: none;
}

.tag-close {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 16px;
  height: 16px;
  background-color: #c0c4cc;
  color: #fff;
  border-radius: 50%;
  font-size: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.tag-close:hover {
  background-color: #f56c6c;
}

.attr-tag:hover .tag-close {
  opacity: 1;
}

.add-tag {
  border-style: dashed;
  width: 80px;
  padding: 0;
}

.add-tag:hover {
  color: #409eff;
  border-color: #409eff;
}

/* 弹窗底部按钮样式统一 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>