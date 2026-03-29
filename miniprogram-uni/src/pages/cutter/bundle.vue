<template>
  <view class="container">
    <view class="order-section">
      <view class="section-title">选择订单</view>
      <view class="order-selector">
        <picker :range="orderOptions" range-key="label" @change="onOrderPick">
          <view class="picker-display">
            <text :class="{ placeholder: !selectedOrder }">
              {{ selectedOrder ? selectedOrder.styleCode + ' - ' + selectedOrder.styleName : '请选择订单' }}
            </text>
            <text class="arrow">›</text>
          </view>
        </picker>
      </view>
    </view>

    <view class="bundle-form" v-if="selectedOrder">
      <view class="form-card">
        <view class="form-title">生成扎包</view>
        <view class="form-row"><text class="label">款号</text><text class="value">{{ selectedOrder.styleCode }}</text></view>
        <view class="form-row"><text class="label">颜色</text>
          <picker :range="colorOptions" @change="onColorPick">
            <text class="value picker-value">{{ form.color || '请选择' }} ›</text>
          </picker>
        </view>
        <view class="form-row"><text class="label">尺码</text>
          <picker :range="sizeOptions" @change="onSizePick">
            <text class="value picker-value">{{ form.size || '请选择' }} ›</text>
          </picker>
        </view>
        <view class="form-row">
          <text class="label">每扎数量</text>
          <input class="form-input" type="number" v-model="form.perBundle" placeholder="默认25" />
        </view>
        <view class="form-row">
          <text class="label">扎数</text>
          <input class="form-input" type="number" v-model="form.bundleCount" placeholder="自动计算" />
        </view>
      </view>
      <view class="btn-group">
        <view class="btn generate-btn" :class="{ disabled: generating }" @click="handleGenerate">
          {{ generating ? '生成中...' : '生成扎包' }}
        </view>
      </view>
    </view>

    <view class="bundle-list-section" v-if="generatedBundles.length > 0">
      <view class="section-header">
        <text class="section-title">已生成扎包 ({{ generatedBundles.length }})</text>
        <view class="print-btn" @click="handlePrintAll">
          <text>🖨️ 批量打印</text>
        </view>
      </view>
      <view class="bundle-list">
        <view class="bundle-item" v-for="item in generatedBundles" :key="item.id" @click="goToDetail(item)">
          <view class="bundle-left">
            <text class="bundle-no">{{ item.bundleNo }}</text>
            <text class="bundle-desc">{{ item.color }} / {{ item.size }} · {{ item.quantity }}件</text>
          </view>
          <view class="bundle-right">
            <view class="print-single" @click.stop="handlePrint(item)">
              <text class="print-text">打印</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view v-if="!selectedOrder && !loading" class="empty">请先选择订单</view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getPendingOrders, getCuttingTaskDetail, generateBundles, printBundleLabel } from '@/api/cutter'

const loading = ref(false)
const generating = ref(false)
const orderList = ref<any[]>([])
const orderOptions = computed(() => orderList.value.map(o => ({ label: `${o.styleCode} ${o.styleName || ''}`, value: o })))
const selectedOrder = ref<any>(null)
const colorOptions = ref<string[]>([])
const sizeOptions = ref<string[]>([])

const form = ref({ color: '', size: '', perBundle: '25', bundleCount: '' })
const generatedBundles = ref<any[]>([])

onMounted(() => { loadOrders() })

const loadOrders = async () => {
  loading.value = true
  try {
    const res: any = await getPendingOrders()
    if (res.success) orderList.value = res.data?.list || res.data || []
  } catch {
    uni.showToast({ title: '加载订单失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const onOrderPick = async (e: any) => {
  const idx = e.detail.value
  selectedOrder.value = orderList.value[idx]
  generatedBundles.value = []
  form.value = { color: '', size: '', perBundle: '25', bundleCount: '' }
  try {
    const res: any = await getCuttingTaskDetail(selectedOrder.value.id)
    if (res.success) {
      colorOptions.value = res.data?.colors || []
      sizeOptions.value = res.data?.sizes || []
    }
  } catch {}
}

const onColorPick = (e: any) => { form.value.color = colorOptions.value[e.detail.value] }
const onSizePick = (e: any) => { form.value.size = sizeOptions.value[e.detail.value] }

const handleGenerate = async () => {
  if (!form.value.color || !form.value.size) {
    uni.showToast({ title: '请选择颜色和尺码', icon: 'none' }); return
  }
  generating.value = true
  try {
    const data = {
      orderId: selectedOrder.value.id,
      color: form.value.color,
      size: form.value.size,
      perBundle: Number(form.value.perBundle) || 25,
      bundleCount: form.value.bundleCount ? Number(form.value.bundleCount) : undefined
    }
    const res: any = await generateBundles(data)
    if (res.success) {
      generatedBundles.value = [...generatedBundles.value, ...(res.data || [])]
      uni.showToast({ title: '生成成功', icon: 'success' })
    }
  } catch {
    uni.showToast({ title: '生成失败', icon: 'none' })
  } finally {
    generating.value = false
  }
}

const handlePrint = async (item: any) => {
  try {
    await printBundleLabel(item.id)
    uni.showToast({ title: '打印指令已发送', icon: 'success' })
  } catch {
    uni.showToast({ title: '打印失败', icon: 'none' })
  }
}

const handlePrintAll = async () => {
  const ids = generatedBundles.value.map(b => b.id)
  try {
    await printBundleLabel(item.id)
    uni.showToast({ title: '批量打印已发送', icon: 'success' })
  } catch {
    uni.showToast({ title: '打印失败', icon: 'none' })
  }
}

const goToDetail = (item: any) => {
  uni.navigateTo({ url: `/pages/cutter/bundle-detail?id=${item.id}` })
}
</script>

<style scoped>
.order-section { margin-bottom: 20rpx; }
.section-title { font-size: 30rpx; font-weight: 600; color: #333; margin-bottom: 12rpx; }
.order-selector {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.picker-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 28rpx;
  color: #333;
}
.picker-display .placeholder { color: #ccc; }
.arrow { font-size: 32rpx; color: #999; }
.form-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin: 20rpx 0;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.form-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 20rpx;
}
.form-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.form-row:last-child { border-bottom: none; }
.form-row .label { font-size: 28rpx; color: #999; flex-shrink: 0; margin-right: 20rpx; }
.form-row .value { font-size: 28rpx; color: #333; }
.picker-value { color: #722ed1; }
.form-input {
  flex: 1;
  text-align: right;
  font-size: 28rpx;
  color: #333;
}
.btn-group { margin: 24rpx 0; }
.generate-btn {
  background: linear-gradient(135deg, #722ed1, #531dab);
  color: #fff;
  text-align: center;
  padding: 28rpx;
  border-radius: 60rpx;
  font-size: 32rpx;
  box-shadow: 0 8rpx 24rpx rgba(114,46,209,0.3);
}
.generate-btn.disabled { opacity: 0.6; }
.bundle-list-section { margin-top: 20rpx; }
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}
.print-btn {
  background: #f0f5ff;
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  color: #1890ff;
}
.bundle-list { display: flex; flex-direction: column; gap: 12rpx; }
.bundle-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.bundle-left { display: flex; flex-direction: column; gap: 4rpx; }
.bundle-no { font-size: 28rpx; color: #333; font-weight: 500; }
.bundle-desc { font-size: 24rpx; color: #999; }
.print-single {
  background: #1890ff;
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
}
.print-text { font-size: 24rpx; color: #fff; }
.empty { text-align: center; color: #999; padding: 80rpx; font-size: 28rpx; }
</style>
