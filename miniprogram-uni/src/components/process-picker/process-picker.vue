<template>
  <view class="picker-mask" v-if="visible" @click="close">
    <view class="picker-container" @click.stop>
      <view class="picker-header">
        <text class="title">选择工序</text>
        <text class="close" @click="close">关闭</text>
      </view>
      <scroll-view scroll-y class="process-list">
        <view
          class="process-item"
          :class="{ selected: selected?.id === item.id }"
          v-for="item in processes"
          :key="item.id"
          @click="selectProcess(item)"
        >
          <view class="process-info">
            <text class="process-name">{{ item.name }}</text>
            <text class="process-category">{{ item.categoryName || '' }}</text>
          </view>
          <text class="process-price">¥{{ item.price }}/件</text>
          <text v-if="selected?.id === item.id" class="check">✓</text>
        </view>
        <view v-if="processes.length === 0" class="empty">暂无数据</view>
      </scroll-view>
      <view class="picker-footer">
        <view class="confirm-btn" @click="confirm">确认</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  show: boolean
  processes: any[]
  modelValue?: any
}>()

const emit = defineEmits<{
  'update:show': [val: boolean]
  'update:modelValue': [val: any]
  confirm: [val: any]
}>()

const visible = ref(props.show)
const selected = ref(props.modelValue)

const selectProcess = (process: any) => {
  selected.value = process
}

const confirm = () => {
  emit('update:modelValue', selected.value)
  emit('confirm', selected.value)
  close()
}

const close = () => {
  visible.value = false
  emit('update:show', false)
}
</script>

<style scoped>
.picker-mask {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  z-index: 999;
  display: flex;
  align-items: flex-end;
}
.picker-container {
  width: 100%;
  background: #fff;
  border-radius: 32rpx 32rpx 0 0;
  max-height: 70vh;
  display: flex;
  flex-direction: column;
}
.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}
.picker-header .title { font-size: 32rpx; font-weight: 600; }
.picker-header .close { color: #999; font-size: 28rpx; }
.process-list { flex: 1; padding: 0 30rpx; }
.process-item {
  display: flex;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.process-item.selected { background: #e6f7ff; }
.process-info { flex: 1; }
.process-name { font-size: 28rpx; color: #333; display: block; }
.process-category { font-size: 24rpx; color: #999; display: block; margin-top: 4rpx; }
.process-price { font-size: 28rpx; color: #1890ff; font-weight: 600; margin-right: 20rpx; }
.check { color: #1890ff; font-size: 36rpx; }
.empty { text-align: center; color: #999; padding: 60rpx; }
.picker-footer { padding: 30rpx; border-top: 1rpx solid #f0f0f0; }
.confirm-btn {
  background: #1890ff;
  color: #fff;
  text-align: center;
  padding: 24rpx;
  border-radius: 12rpx;
  font-size: 30rpx;
}
</style>
