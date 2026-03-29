<template>
  <view class="container">
    <view class="month-selector">
      <view class="arrow" @click="changeMonth(-1)">
        <text>◀</text>
      </view>
      <text class="month-text">{{ displayMonth }}</text>
      <view class="arrow" @click="changeMonth(1)">
        <text>▶</text>
      </view>
    </view>
    <view class="wage-summary">
      <view class="summary-card">
        <text class="summary-label">本月总工资</text>
        <text class="summary-amount">¥{{ wageData.totalAmount || '0.00' }}</text>
      </view>
      <view class="summary-detail">
        <view class="detail-item">
          <text class="detail-value">{{ wageData.totalCount || 0 }}</text>
          <text class="detail-label">计件次数</text>
        </view>
        <view class="detail-item">
          <text class="detail-value">{{ wageData.totalQuantity || 0 }}</text>
          <text class="detail-label">总数量</text>
        </view>
        <view class="detail-item">
          <text class="detail-value">{{ wageData.workDays || 0 }}</text>
          <text class="detail-label">出勤天数</text>
        </view>
      </view>
    </view>
    <view class="section">
      <view class="section-header">
        <text class="section-title">工资明细</text>
        <text class="section-more" @click="goToDetail">查看详情 ›</text>
      </view>
      <view class="wage-list">
        <view class="wage-item" v-for="item in wageList" :key="item.id">
          <view class="wage-left">
            <text class="date">{{ item.date }}</text>
            <text class="detail">{{ item.count }}次计件 · {{ item.quantity }}件</text>
          </view>
          <text class="wage-amount">+¥{{ item.amount }}</text>
        </view>
        <view v-if="wageList.length === 0" class="empty">暂无工资记录</view>
      </view>
    </view>
    <view class="load-more" v-if="hasMore" @click="loadMore">
      <text>{{ loading ? '加载中...' : '加载更多' }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getWageSummary, getWageDetail } from '@/api/worker'

const currentYear = ref(new Date().getFullYear())
const currentMonthNum = ref(new Date().getMonth() + 1)

const displayMonth = computed(() =>
  `${currentYear.value}年${currentMonthNum.value}月`
)

const wageData = ref({ totalAmount: 0, totalCount: 0, totalQuantity: 0, workDays: 0 })
const wageList = ref<any[]>([])
const page = ref(1)
const pageSize = 20
const hasMore = ref(true)
const loading = ref(false)

onShow(() => {
  page.value = 1
  wageList.value = []
  hasMore.value = true
  loadData()
})

const changeMonth = (offset: number) => {
  currentMonthNum.value += offset
  if (currentMonthNum.value > 12) {
    currentMonthNum.value = 1
    currentYear.value++
  } else if (currentMonthNum.value < 1) {
    currentMonthNum.value = 12
    currentYear.value--
  }
  page.value = 1
  wageList.value = []
  hasMore.value = true
  loadData()
}

const loadData = async () => {
  const month = `${currentYear.value}-${String(currentMonthNum.value).padStart(2, '0')}`
  try {
    const summaryRes: any = await getWageSummary({ month })
    if (summaryRes.success) wageData.value = summaryRes.data || wageData.value
  } catch {}
  await loadWageList()
}

const loadWageList = async () => {
  if (loading.value) return
  loading.value = true
  const month = `${currentYear.value}-${String(currentMonthNum.value).padStart(2, '0')}`
  try {
    const res: any = await getWageDetail({ month, page: page.value, pageSize })
    if (res.success) {
      const list = res.data?.list || res.data || []
      wageList.value = page.value === 1 ? list : [...wageList.value, ...list]
      hasMore.value = list.length >= pageSize
    }
  } catch {
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (!hasMore.value || loading.value) return
  page.value++
  loadWageList()
}

const goToDetail = () => {
  const month = `${currentYear.value}-${String(currentMonthNum.value).padStart(2, '0')}`
  uni.navigateTo({ url: `/pages/worker/wage-detail?month=${month}` })
}
</script>

<style scoped>
.month-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40rpx;
  padding: 20rpx 0;
}
.month-text { font-size: 34rpx; font-weight: 600; color: #333; }
.arrow {
  width: 60rpx; height: 60rpx;
  display: flex; align-items: center; justify-content: center;
  font-size: 24rpx; color: #999;
}
.wage-summary { margin-bottom: 30rpx; }
.summary-card {
  background: linear-gradient(135deg, #52c41a, #389e0d);
  border-radius: 16rpx;
  padding: 40rpx 30rpx;
  text-align: center;
  margin-bottom: 20rpx;
}
.summary-label { font-size: 28rpx; color: rgba(255,255,255,0.8); display: block; }
.summary-amount { font-size: 56rpx; font-weight: 700; color: #fff; margin-top: 12rpx; display: block; }
.summary-detail {
  display: flex;
  justify-content: space-around;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx 0;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.detail-item { text-align: center; }
.detail-value { font-size: 36rpx; font-weight: 600; color: #333; display: block; }
.detail-label { font-size: 24rpx; color: #999; margin-top: 4rpx; display: block; }
.section { margin-bottom: 20rpx; }
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.section-title { font-size: 30rpx; font-weight: 600; color: #333; }
.section-more { font-size: 26rpx; color: #1890ff; }
.wage-list { display: flex; flex-direction: column; gap: 16rpx; }
.wage-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.wage-left { display: flex; flex-direction: column; gap: 4rpx; }
.date { font-size: 28rpx; color: #333; font-weight: 500; }
.detail { font-size: 24rpx; color: #999; }
.wage-amount { font-size: 32rpx; font-weight: 600; color: #52c41a; }
.empty { text-align: center; color: #999; padding: 80rpx; font-size: 28rpx; }
.load-more { text-align: center; padding: 30rpx; color: #1890ff; font-size: 28rpx; }
</style>
