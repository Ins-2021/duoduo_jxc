<template>
  <div class="settings-container">
    <el-card>
      <h2>设置管理</h2>
      <el-divider />
      <el-form :model="form" label-width="200px">
        <el-form-item label="启用尺码和颜色(二维录入)">
          <el-checkbox v-model="form.enableSizeColor">启动</el-checkbox>
        </el-form-item>
      </el-form>
      <div style="text-align: right;">
        <el-button type="primary" @click="onSave" :loading="saving" v-perm="'settings:system-params:edit'">保存</el-button>
      </div>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getSystemSettings, saveSystemSettings } from '@/api/settings'

const saving = ref(false)
const form = reactive({
  enableSizeColor: false
})

const load = async () => {
  try {
    const res = await getSystemSettings()
    form.enableSizeColor = !!res?.data?.enableSizeColor
  } catch {
    form.enableSizeColor = false
  }
}

const onSave = async () => {
  saving.value = true
  try {
    await saveSystemSettings({ enableSizeColor: form.enableSizeColor })
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  load()
})
</script>
