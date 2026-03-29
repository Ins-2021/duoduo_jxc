<template>
  <view class="scan-input">
    <view class="input-wrapper">
      <text class="scan-icon" @click="handleScan">📷</text>
      <input
        class="input-field"
        :value="modelValue"
        :placeholder="placeholder"
        :focus="focus"
        @input="onInput"
        @confirm="handleConfirm"
      />
      <view class="scan-btn" @click="handleScan">扫码</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = withDefaults(defineProps<{
  modelValue: string
  placeholder?: string
  autoScan?: boolean
}>(), { placeholder: '请输入或扫描', autoScan: true })

const emit = defineEmits<{
  'update:modelValue': [val: string]
  scan: [val: string]
  confirm: [val: string]
}>()

const focus = ref(false)

function onInput(e: any) {
  emit('update:modelValue', e.detail.value)
}

function handleScan() {
  uni.scanCode({
    success: (res) => {
      emit('update:modelValue', res.result)
      emit('scan', res.result)
      if (props.autoScan) {
        emit('confirm', res.result)
      }
    },
    fail: () => {
      uni.showToast({ title: '扫码失败', icon: 'none' })
    }
  })
}

function handleConfirm() {
  emit('confirm', props.modelValue)
}

function setFocus() { focus.value = true }

defineExpose({ setFocus })
</script>

<style scoped>
.input-wrapper {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 12rpx;
  padding: 16rpx 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.06);
}
.scan-icon { font-size: 40rpx; margin-right: 16rpx; }
.input-field {
  flex: 1;
  height: 72rpx;
  font-size: 28rpx;
}
.scan-btn {
  background: #1890ff;
  color: #fff;
  padding: 12rpx 24rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
}
</style>
