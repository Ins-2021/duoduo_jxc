<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>
      <div class="card-header">
        <span>{{ isEdit ? '编辑裁床单' : '新建裁床单' }}</span>
      </div>
    </template>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="生产订单" prop="orderId">
            <el-select v-model="form.orderId" placeholder="请选择生产订单" filterable style="width: 100%">
              <el-option
                v-for="item in orderOptions"
                :key="item.orderId"
                :label="item.orderNo"
                :value="item.orderId"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="面料" prop="fabricId">
            <el-select v-model="form.fabricId" placeholder="请选择面料" filterable style="width: 100%">
              <el-option
                v-for="item in fabricOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="唛架" prop="markerId">
            <el-input v-model="form.markerId" placeholder="请输入唛架ID" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="裁剪日期" prop="cuttingDate">
            <el-date-picker
              v-model="form.cuttingDate"
              type="date"
              placeholder="选择裁剪日期"
              style="width: 100%"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="计划数量" prop="plannedQuantity">
            <el-input-number v-model="form.plannedQuantity" :min="1" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="裁剪数量" prop="cutQuantity">
            <el-input-number v-model="form.cutQuantity" :min="0" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="裁剪组" prop="cuttingGroupId">
            <el-select v-model="form.cuttingGroupId" placeholder="请选择裁剪组" filterable style="width: 100%">
              <el-option
                v-for="item in workGroupOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="裁床机器" prop="cuttingMachine">
            <el-input v-model="form.cuttingMachine" placeholder="请输入裁床机器" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="铺布方式" prop="spreadMethod">
            <el-select v-model="form.spreadMethod" placeholder="请选择铺布方式" style="width: 100%">
              <el-option label="单向" value="one_way" />
              <el-option label="往返" value="round_trip" />
              <el-option label="对折" value="folded" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
              <el-option label="待处理" value="pending" />
              <el-option label="进行中" value="in_progress" />
              <el-option label="已完成" value="completed" />
              <el-option label="已取消" value="cancelled" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="面料用量" prop="fabricConsumption">
            <el-input-number v-model="form.fabricConsumption" :precision="2" :step="0.01" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="疵品率(%)" prop="fabricDefectRate">
            <el-input-number v-model="form.fabricDefectRate" :precision="2" :step="0.01" :min="0" :max="100" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="利用率(%)" prop="utilizationRate">
            <el-input-number v-model="form.utilizationRate" :precision="2" :step="0.01" :min="0" :max="100" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="实际效率(%)" prop="actualEfficiency">
            <el-input-number v-model="form.actualEfficiency" :precision="2" :step="0.01" :min="0" :max="100" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="裁片数" prop="cutPieces">
        <el-input-number v-model="form.cutPieces" :min="0" style="width: 100%" />
      </el-form-item>

      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注"
        />
      </el-form-item>
    </el-form>

    <div class="form-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCutOrder, createCutOrder, updateCutOrder } from '@/api/production'
import { getProductionOrderList } from '@/api/production'
import { getFabricList } from '@/api/fabric'
import { getWorkGroupList } from '@/api/workgroup'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const orderOptions = ref<any[]>([])
const fabricOptions = ref<any[]>([])
const workGroupOptions = ref<any[]>([])

const form = reactive({
  planId: undefined,
  planNo: '',
  orderId: undefined,
  fabricId: undefined,
  markerId: undefined,
  plannedQuantity: 0,
  cutQuantity: 0,
  cuttingDate: '',
  cuttingGroupId: undefined,
  cuttingMachine: '',
  spreadMethod: 'one_way',
  fabricConsumption: undefined,
  fabricDefectRate: undefined,
  utilizationRate: undefined,
  actualEfficiency: undefined,
  cutPieces: 0,
  status: 'pending',
  remark: ''
})

const rules = {
  orderId: [{ required: true, message: '请选择生产订单', trigger: 'change' }],
  fabricId: [{ required: true, message: '请选择面料', trigger: 'change' }],
  plannedQuantity: [{ required: true, message: '请输入计划数量', trigger: 'blur' }],
  cuttingDate: [{ required: true, message: '请选择裁剪日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 加载生产订单选项
const loadOrderOptions = async () => {
  try {
    const res = await getProductionOrderList({ page: 1, size: 100 })
    orderOptions.value = res.data.list || []
  } catch (error) {
    console.error('加载生产订单失败', error)
  }
}

// 加载面料选项
const loadFabricOptions = async () => {
  try {
    const res = await getFabricList({ page: 1, size: 100 })
    fabricOptions.value = res.data.list || []
  } catch (error) {
    console.error('加载面料失败', error)
  }
}

// 加载裁剪组选项
const loadWorkGroupOptions = async () => {
  try {
    const res = await getWorkGroupList({ page: 1, size: 100 })
    workGroupOptions.value = res.data.list || []
  } catch (error) {
    console.error('加载裁剪组失败', error)
  }
}

// 加载裁床单详情
const loadCutOrderDetail = async (id: number) => {
  loading.value = true
  try {
    const res = await getCutOrder(id)
    const data = res.data
    Object.assign(form, data)
  } catch (error) {
    ElMessage.error('加载裁床单详情失败')
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value && form.planId) {
      await updateCutOrder(form.planId, form)
      ElMessage.success('更新成功')
    } else {
      await createCutOrder(form)
      ElMessage.success('创建成功')
    }
    router.push('/production/cut-order')
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  } finally {
    submitLoading.value = false
  }
}

// 取消
const handleCancel = () => {
  router.push('/production/cut-order')
}

onMounted(() => {
  const id = route.params.id
  if (id) {
    isEdit.value = true
    loadCutOrderDetail(Number(id))
  }
  loadOrderOptions()
  loadFabricOptions()
  loadWorkGroupOptions()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}
</style>