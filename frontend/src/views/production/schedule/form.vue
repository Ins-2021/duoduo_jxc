<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>
      <div class="card-header">
        <span>{{ isEdit ? '编辑生产排程' : '新建生产排程' }}</span>
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
          <el-form-item label="生产计划" prop="planId">
            <el-select v-model="form.planId" placeholder="请选择生产计划" filterable style="width: 100%">
              <el-option
                v-for="item in planOptions"
                :key="item.planId"
                :label="item.planNo"
                :value="item.planId"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="开始日期" prop="startDate">
            <el-date-picker
              v-model="form.startDate"
              type="date"
              placeholder="选择开始日期"
              style="width: 100%"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结束日期" prop="endDate">
            <el-date-picker
              v-model="form.endDate"
              type="date"
              placeholder="选择结束日期"
              style="width: 100%"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
              <el-option label="草稿" value="draft" />
              <el-option label="已确认" value="confirmed" />
              <el-option label="进行中" value="in_progress" />
              <el-option label="已完成" value="completed" />
              <el-option label="已取消" value="cancelled" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="排程明细" prop="scheduleItems">
        <el-input
          v-model="form.scheduleItems"
          type="textarea"
          :rows="4"
          placeholder="请输入排程明细（JSON格式）"
        />
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
import { getProductionSchedule, createProductionSchedule, updateProductionSchedule } from '@/api/production'
import { getProductionOrderList } from '@/api/production'
import { getProductionPlanList } from '@/api/production'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const orderOptions = ref<any[]>([])
const planOptions = ref<any[]>([])

const form = reactive({
  scheduleId: undefined,
  scheduleNo: '',
  orderId: undefined,
  planId: undefined,
  startDate: '',
  endDate: '',
  scheduleItems: '',
  status: 'draft',
  remark: ''
})

const rules = {
  orderId: [{ required: true, message: '请选择生产订单', trigger: 'change' }],
  planId: [{ required: true, message: '请选择生产计划', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
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

// 加载生产计划选项
const loadPlanOptions = async () => {
  try {
    const res = await getProductionPlanList({ page: 1, size: 100 })
    planOptions.value = res.data.list || []
  } catch (error) {
    console.error('加载生产计划失败', error)
  }
}

// 加载排程详情
const loadScheduleDetail = async (id: number) => {
  loading.value = true
  try {
    const res = await getProductionSchedule(id)
    const data = res.data
    Object.assign(form, data)
  } catch (error) {
    ElMessage.error('加载排程详情失败')
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
    if (isEdit.value && form.scheduleId) {
      await updateProductionSchedule(form.scheduleId, form)
      ElMessage.success('更新成功')
    } else {
      await createProductionSchedule(form)
      ElMessage.success('创建成功')
    }
    router.push('/production/schedule')
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  } finally {
    submitLoading.value = false
  }
}

// 取消
const handleCancel = () => {
  router.push('/production/schedule')
}

onMounted(() => {
  const id = route.params.id
  if (id) {
    isEdit.value = true
    loadScheduleDetail(Number(id))
  }
  loadOrderOptions()
  loadPlanOptions()
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