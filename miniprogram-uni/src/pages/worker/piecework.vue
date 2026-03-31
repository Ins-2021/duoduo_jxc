<template>
  <view class="container">
    <view class="offline-tip" v-if="isOffline">
      <text>⚠️ 离线模式，数据将缓存后自动同步</text>
    </view>
    <scan-input v-model="bundleNo" placeholder="请输入或扫描扎包号" @confirm="handleScan" />
    <view class="qr-scan-btn" @click="openQrScanner">
      <text>📷 扫描二维码</text>
    </view>
    <view class="bundle-info" v-if="bundleInfo">
      <view class="info-card">
        <view class="info-row"><text class="label">扎包号</text><text class="value">{{ bundleInfo.bundleNo }}</text></view>
        <view class="info-row"><text class="label">款号</text><text class="value">{{ bundleInfo.styleCode }}</text></view>
        <view class="info-row"><text class="label">颜色/尺码</text><text class="value">{{ bundleInfo.color }} / {{ bundleInfo.size }}</text></view>
        <view class="info-row"><text class="label">数量</text><text class="value highlight">{{ bundleInfo.quantity }}</text></view>
      </view>
      <view class="process-section">
        <view class="section-title">选择工序</view>
        <view class="process-list">
          <view
            class="process-item"
            :class="{ active: selectedProcessId === item.id }"
            v-for="item in processList"
            :key="item.id"
            @click="selectedProcessId = item.id"
          >
            <text class="process-name">{{ item.name }}</text>
            <text class="process-price">¥{{ item.price }}/件</text>
          </view>
        </view>
      </view>
      <view class="quantity-section">
        <view class="section-title">计件数量</view>
        <view class="quantity-control">
          <view class="qty-btn" @click="quantity = Math.max(1, quantity - 1)">-</view>
          <text class="qty-value">{{ quantity }}</text>
          <view class="qty-btn" @click="quantity = Math.min(bundleInfo.quantity, quantity + 1)">+</view>
        </view>
      </view>
      <view class="submit-section">
        <view class="submit-btn" :class="{ disabled: submitting }" @click="handleSubmit">
          {{ submitting ? '提交中...' : '提交计件' }}
        </view>
      </view>
    </view>
    <view class="records-section">
      <view class="section-title">今日计件记录</view>
      <view class="record-list">
        <view class="record-item" v-for="item in todayRecords" :key="item.recordId">
          <view class="record-left">
            <text class="bundle-no">{{ item.bundleNo }}</text>
            <text class="process-name">{{ item.processName }}</text>
          </view>
          <view class="record-right">
            <text class="quantity">×{{ item.quantity }}</text>
            <text class="amount">¥{{ item.amount || 0 }}</text>
          </view>
        </view>
        <view v-if="todayRecords.length === 0" class="empty">暂无计件记录</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import ScanInput from '@/components/scan-input/scan-input.vue'
import { getBundleInfo, submitPiecework, getTodayRecords as getWorkerTodayRecords } from '@/api/worker'
import { scanWithQrCode, getTodayRecords } from '@/api/scan'
import { useOfflineStore } from '@/store/offline'
import { useUserStore } from '@/store/user'

const offlineStore = useOfflineStore()
const userStore = useUserStore()
const isOffline = ref(offlineStore.isOffline)
const bundleNo = ref('')
const bundleInfo = ref<any>(null)
const processList = ref<any[]>([])
const selectedProcessId = ref<number | null>(null)
const quantity = ref(1)
const submitting = ref(false)
const todayRecords = ref<any[]>([])
const lastQrCodeNo = ref('')

onMounted(() => { loadTodayRecords() })

const openQrScanner = () => {
  uni.scanCode({
    scanType: ['qrCode'],
    success: (res) => {
      handleQrCodeScan(res.result)
    },
    fail: () => {
      uni.showToast({ title: '扫码失败', icon: 'none' })
    }
  })
}

const handleQrCodeScan = async (qrContent: string) => {
  let qrCodeNo = qrContent
  try {
    const parsed = JSON.parse(qrContent)
    qrCodeNo = parsed.no || qrContent
  } catch {
    // 如果不是 JSON，直接使用原始内容
  }

  lastQrCodeNo.value = qrCodeNo

  if (!selectedProcessId.value) {
    uni.showToast({ title: '请先选择工序', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    const workerId = userStore.userInfo?.id
    if (!workerId) {
      uni.showToast({ title: '请先登录', icon: 'none' })
      return
    }

    await scanWithQrCode(qrCodeNo, selectedProcessId.value, quantity.value)
    uni.showToast({ title: '扫码计件成功', icon: 'success' })
    quantity.value = 1
    loadTodayRecords()
  } catch (e: any) {
    uni.showToast({ title: e.data?.message || '扫码计件失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

const handleScan = async (code: string) => {
  if (!code) return
  bundleNo.value = code
  try {
    const res: any = await getBundleInfo(code)
    if (res.success) {
      bundleInfo.value = res.data
      processList.value = res.data.processes || []
      if (processList.value.length > 0) selectedProcessId.value = processList.value[0].id
    }
  } catch { uni.showToast({ title: '获取扎包信息失败', icon: 'none' }) }
}

const handleSubmit = async () => {
  if (!bundleInfo.value || !selectedProcessId.value) {
    uni.showToast({ title: '请先扫描扎包并选择工序', icon: 'none' }); return
  }
  submitting.value = true
  try {
    const data = { bundleNo: bundleInfo.value.bundleNo, processId: selectedProcessId.value, quantity: quantity.value }
    if (isOffline.value) {
      await offlineStore.addQueue('piecework', data)
      uni.showToast({ title: '已保存到本地', icon: 'none' })
    } else {
      await submitPiecework(data)
      uni.showToast({ title: '提交成功', icon: 'success' })
    }
    bundleNo.value = ''; bundleInfo.value = null; selectedProcessId.value = null; quantity.value = 1
    loadTodayRecords()
  } catch { uni.showToast({ title: '提交失败', icon: 'none' }) }
  finally { submitting.value = false }
}

const loadTodayRecords = async () => {
  try {
    const workerId = userStore.userInfo?.id
    if (workerId) {
      const res: any = await getTodayRecords(workerId)
      todayRecords.value = res.data || []
    } else {
      const res: any = await getWorkerTodayRecords()
      todayRecords.value = res.data || []
    }
  } catch {}
}

defineExpose({
  onScanResult: (result: string) => {
    handleQrCodeScan(result)
  }
})
</script>

<style scoped>
.offline-tip {
  background: #fff7e6;
  color: #fa8c16;
  padding: 16rpx 24rpx;
  border-radius: 8rpx;
  margin-bottom: 20rpx;
  font-size: 26rpx;
  text-align: center;
}
.qr-scan-btn {
  background: linear-gradient(135deg, #52c41a, #389e0d);
  color: #fff;
  text-align: center;
  padding: 24rpx;
  border-radius: 12rpx;
  margin: 20rpx 0;
  font-size: 30rpx;
}
.info-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin: 20rpx 0;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 12rpx 0;
  font-size: 28rpx;
}
.info-row .label { color: #999; }
.info-row .value.highlight { color: #1890ff; font-weight: 600; }
.process-list { display: flex; flex-wrap: wrap; gap: 16rpx; margin-bottom: 24rpx; }
.process-item {
  padding: 16rpx 24rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.process-item.active { background: #e6f7ff; border: 2rpx solid #1890ff; }
.process-name { font-size: 26rpx; }
.process-price { font-size: 24rpx; color: #1890ff; }
.quantity-control { display: flex; align-items: center; gap: 30rpx; justify-content: center; }
.qty-btn {
  width: 64rpx; height: 64rpx;
  border: 2rpx solid #ddd;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 36rpx; color: #333;
}
.qty-value { font-size: 40rpx; font-weight: 600; min-width: 80rpx; text-align: center; }
.submit-section { margin: 30rpx 0; }
.submit-btn {
  background: linear-gradient(135deg, #1890ff, #096dd9);
  color: #fff;
  text-align: center;
  padding: 28rpx;
  border-radius: 60rpx;
  font-size: 32rpx;
  box-shadow: 0 8rpx 24rpx rgba(24,144,255,0.3);
}
.submit-btn.disabled { opacity: 0.6; }
.section-title {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  margin-bottom: 16rpx;
}
.record-list { background: #fff; border-radius: 16rpx; overflow: hidden; }
.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.record-left { display: flex; flex-direction: column; gap: 4rpx; }
.bundle-no { font-size: 28rpx; color: #333; }
.process-name { font-size: 24rpx; color: #999; }
.record-right { display: flex; gap: 20rpx; }
.quantity { font-size: 28rpx; color: #666; }
.amount { font-size: 28rpx; color: #52c41a; font-weight: 600; }
.empty { text-align: center; color: #999; padding: 60rpx; font-size: 28rpx; }
</style>
