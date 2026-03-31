<template>
  <div class="app-container">
    <!-- 固定头部 -->
    <div class="sticky-header" style="margin-bottom: 20px;">
      <el-page-header @back="goBack">
        <template #content>
          <span class="text-large font-600 mr-3">{{ isEdit ? '编辑生产单' : '新建生产单' }}</span>
        </template>
        <template #extra>
          <div class="flex items-center" style="display: flex; gap: 10px;">
            <el-button @click="goBack">取消</el-button>
            <el-button @click="handleSaveDraft" :loading="saving">保存草稿</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
          </div>
        </template>
      </el-page-header>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <!-- 基本信息 -->
      <el-card class="box-card" shadow="never" style="margin-bottom: 16px;">
        <template #header>基本信息</template>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="生产单号" prop="orderNo">
              <el-input v-model="form.orderNo" placeholder="自动生成" :disabled="true"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="款号" prop="styleCode">
              <el-select v-model="form.styleCode" placeholder="选择款号" filterable @change="handleStyleChange" style="width: 100%">
                <el-option v-for="item in styleOptions" :key="item.code" :label="item.code" :value="item.code"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="品名" prop="styleName">
              <el-input v-model="form.styleName" :disabled="true"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="关联销售单" prop="salesOrderId">
              <el-select v-model="form.salesOrderId" placeholder="选择销售单" clearable filterable style="width: 100%">
                <el-option v-for="item in salesOrderOptions" :key="item.id" :label="item.orderNo" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="生产工厂" prop="factoryId">
              <el-select v-model="form.factoryId" placeholder="选择工厂" style="width: 100%">
                <el-option v-for="item in factoryOptions" :key="item.id" :label="item.name" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="form.priority" placeholder="选择优先级" style="width: 100%">
                <el-option label="低" value="low"/>
                <el-option label="普通" value="normal"/>
                <el-option label="高" value="high"/>
                <el-option label="紧急" value="urgent"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="计划开始日期" prop="planStartDate">
              <el-date-picker v-model="form.planStartDate" type="date" placeholder="选择日期" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="计划完成日期" prop="planEndDate">
              <el-date-picker v-model="form.planEndDate" type="date" placeholder="选择日期" style="width: 100%"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 规格数量 -->
      <el-card class="box-card" shadow="never" style="margin-bottom: 16px;">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span>规格数量</span>
            <el-button type="primary" plain size="small" @click="handleAddSku">添加规格</el-button>
          </div>
        </template>
        <el-table :data="form.skus" border>
          <el-table-column prop="color" label="颜色" width="150">
            <template #default="{row}">
              <el-select v-model="row.color" placeholder="选择颜色">
                <el-option v-for="item in colorOptions" :key="item" :label="item" :value="item"/>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="size" label="尺码" width="150">
            <template #default="{row}">
              <el-select v-model="row.size" placeholder="选择尺码">
                <el-option v-for="item in sizeOptions" :key="item" :label="item" :value="item"/>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="150">
            <template #default="{row}">
              <el-input-number v-model="row.quantity" :min="1" style="width: 100%"/>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注">
            <template #default="{row}">
              <el-input v-model="row.remark" placeholder="备注"/>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{$index}">
              <el-button link type="danger" @click="handleRemoveSku($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 15px; font-weight: bold; text-align: right;">
          合计数量：{{ totalQuantity }}
        </div>
      </el-card>

      <!-- 工序配置 -->
      <el-card class="box-card" shadow="never" style="margin-bottom: 16px;">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span>工序配置</span>
            <el-button type="primary" plain size="small" @click="handleLoadProcessConfig">加载模板</el-button>
          </div>
        </template>
        <el-table :data="form.processes" border>
          <el-table-column type="index" label="序号" width="60" align="center"/>
          <el-table-column prop="processName" label="工序名称" width="200">
            <template #default="{row}">
              <el-select v-model="row.processId" placeholder="选择工序" filterable @change="handleProcessChange(row)" style="width: 100%">
                <el-option v-for="item in processOptions" :key="item.id" :label="item.name" :value="item.id"/>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="unit" label="单位" width="100"/>
          <el-table-column prop="price" label="单价" width="120">
            <template #default="{row}">
              <el-input-number v-model="row.price" :min="0" :precision="2" :step="0.1" controls-position="right" style="width: 100%"/>
            </template>
          </el-table-column>
          <el-table-column prop="estimatedTime" label="工时(分钟)" width="120">
            <template #default="{row}">
              <el-input-number v-model="row.estimatedTime" :min="0" controls-position="right" style="width: 100%"/>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注">
            <template #default="{row}">
              <el-input v-model="row.remark" placeholder="备注"/>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{$index}">
              <el-button link type="danger" @click="handleRemoveProcess($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 15px;">
          <el-button type="primary" link @click="handleAddProcess">添加工序</el-button>
        </div>
      </el-card>

      <!-- 备注 -->
      <el-card class="box-card" shadow="never">
        <template #header>备注信息</template>
        <el-form-item prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息"/>
        </el-form-item>
      </el-card>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

defineOptions({ name: 'ProductionOrderAdd' })

const router = useRouter()
const route = useRoute()

const isEdit = computed(() => !!route.params.id)
const saving = ref(false)
const submitting = ref(false)
const formRef = ref()

const form = reactive({
  orderNo: '',
  styleCode: '',
  styleName: '',
  salesOrderId: '',
  factoryId: '',
  priority: 'normal',
  planStartDate: '',
  planEndDate: '',
  skus: [] as any[],
  processes: [] as any[],
  remark: ''
})

const rules = {
  styleCode: [{ required: true, message: '请选择款号', trigger: 'change' }],
  factoryId: [{ required: true, message: '请选择工厂', trigger: 'change' }],
  planStartDate: [{ required: true, message: '请选择计划开始日期', trigger: 'change' }]
}

// Mock Data
const styleOptions = [
  { code: 'S001', name: '春季新款T恤' },
  { code: 'S002', name: '夏季连衣裙' }
]
const salesOrderOptions = [
  { id: 1, orderNo: 'SO20260329001' }
]
const factoryOptions = [
  { id: 1, name: '一号工厂' },
  { id: 2, name: '二号工厂' }
]
const colorOptions = ['红色', '蓝色', '黑色', '白色']
const sizeOptions = ['S', 'M', 'L', 'XL', 'XXL']
const processOptions = [
  { id: 1, name: '裁剪', unit: '件', price: 0.5, estimatedTime: 2 },
  { id: 2, name: '缝纫', unit: '件', price: 1.5, estimatedTime: 10 },
  { id: 3, name: '质检', unit: '件', price: 0.3, estimatedTime: 1 }
]

const totalQuantity = computed(() => {
  return form.skus.reduce((sum, item) => sum + (item.quantity || 0), 0)
})

const goBack = () => {
  router.back()
}

const handleStyleChange = (val: string) => {
  const style = styleOptions.find(item => item.code === val)
  if (style) {
    form.styleName = style.name
  } else {
    form.styleName = ''
  }
}

const handleAddSku = () => {
  form.skus.push({ color: '', size: '', quantity: 1, remark: '' })
}

const handleRemoveSku = (index: number) => {
  form.skus.splice(index, 1)
}

const handleAddProcess = () => {
  form.processes.push({ processId: '', processName: '', unit: '', price: 0, estimatedTime: 0, remark: '' })
}

const handleRemoveProcess = (index: number) => {
  form.processes.splice(index, 1)
}

const handleProcessChange = (row: any) => {
  const process = processOptions.find(p => p.id === row.processId)
  if (process) {
    row.processName = process.name
    row.unit = process.unit
    row.price = process.price
    row.estimatedTime = process.estimatedTime
  }
}

const handleLoadProcessConfig = () => {
  if (!form.styleCode) {
    ElMessage.warning('请先选择款号')
    return
  }
  // Mock load template
  form.processes = processOptions.map(p => ({
    processId: p.id,
    processName: p.name,
    unit: p.unit,
    price: p.price,
    estimatedTime: p.estimatedTime,
    remark: ''
  }))
  ElMessage.success('模板加载成功')
}

const handleSaveDraft = async () => {
  if (!formRef.value) return
  saving.value = true
  setTimeout(() => {
    ElMessage.success('草稿保存成功')
    saving.value = false
    router.back()
  }, 500)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid: boolean) => {
    if (valid) {
      if (form.skus.length === 0) {
        ElMessage.warning('请添加规格数量')
        return
      }
      submitting.value = true
      setTimeout(() => {
        ElMessage.success('提交成功')
        submitting.value = false
        router.back()
      }, 500)
    }
  })
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>
