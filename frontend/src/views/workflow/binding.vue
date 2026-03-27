<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <div class="left">
          <el-input v-model="bizType" placeholder="bizType，例如 PURCHASE_ORDER" style="width: 280px" />
          <el-button @click="load">查询</el-button>
        </div>
        <div class="right">
          <span class="label">操作人ID</span>
          <el-input-number v-model="operatorId" :min="0" :step="1" />
        </div>
      </div>

      <el-form label-width="120px" style="max-width: 680px">
        <el-form-item label="流程定义Key">
          <el-input v-model="form.processDefKey" placeholder="发布后的流程 key（processDefinitionKey）" />
        </el-form-item>
        <el-form-item label="是否启用">
          <el-switch v-model="enabledBool" />
        </el-form-item>
        <el-form-item label="启动条件(JSON)">
          <el-input
            v-model="form.startCondition"
            type="textarea"
            :rows="4"
            placeholder="可选，复杂建议后续用 DMN"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getWorkflowBinding, saveWorkflowBinding } from '@/api/workflow'

const operatorId = ref<number>(1)
const bizType = ref('DEMO')

const form = reactive<any>({
  processDefKey: '',
  enabled: 1,
  startCondition: ''
})

const enabledBool = computed({
  get() {
    return Number(form.enabled) !== 0
  },
  set(v: boolean) {
    form.enabled = v ? 1 : 0
  }
})

const load = async () => {
  const res: any = await getWorkflowBinding(bizType.value)
  const data = res.data
  if (!data) {
    form.processDefKey = ''
    form.enabled = 1
    form.startCondition = ''
    return
  }
  form.processDefKey = data.processDefKey
  form.enabled = data.enabled
  form.startCondition = data.startCondition || ''
}

const save = async () => {
  await saveWorkflowBinding(
    bizType.value,
    {
      processDefKey: form.processDefKey,
      enabled: form.enabled,
      startCondition: form.startCondition
    },
    operatorId.value
  )
  ElMessage.success('保存成功')
}
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.label {
  margin-right: 8px;
  color: #606266;
}

.right {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>

