<template>
  <view class="container">
    <view class="search-bar">
      <view class="search-input">
        <text class="search-icon">🔍</text>
        <input v-model="keyword" placeholder="搜索姓名/工号" @confirm="handleSearch" />
      </view>
    </view>

    <view class="stats-row">
      <view class="stat-chip">
        <text class="chip-value">{{ staffData.length }}</text>
        <text class="chip-label">员工总数</text>
      </view>
      <view class="stat-chip">
        <text class="chip-value">{{ onDutyCount }}</text>
        <text class="chip-label">在岗人数</text>
      </view>
      <view class="stat-chip">
        <text class="chip-value">{{ totalTodayOutput }}</text>
        <text class="chip-label">今日总产量</text>
      </view>
    </view>

    <view v-if="filteredStaff.length" class="staff-list">
      <view v-for="item in filteredStaff" :key="item.id" class="staff-card">
        <view class="staff-header">
          <view class="staff-avatar">
            <text>{{ item.name.charAt(0) }}</text>
          </view>
          <view class="staff-info">
            <text class="staff-name">{{ item.name }}</text>
            <text class="staff-no">工号: {{ item.workerNo }}</text>
          </view>
          <view class="duty-tag" :class="{ on: item.onDuty, off: !item.onDuty }">
            <text>{{ item.onDuty ? '在岗' : '离岗' }}</text>
          </view>
        </view>
        <view class="staff-detail">
          <view class="detail-item">
            <text class="detail-label">部门</text>
            <text class="detail-value">{{ item.department }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">工序</text>
            <text class="detail-value">{{ item.process }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">今日产量</text>
            <text class="detail-value highlight">{{ item.todayOutput }}件</text>
          </view>
        </view>
      </view>
    </view>
    <view v-else class="empty-state">
      <text class="empty-text">暂无员工数据</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getStaffList } from '@/api/manager'

const keyword = ref('')

const staffData = ref([
  { id: 1, name: '张三', workerNo: 'W001', department: '缝制一组', process: '平缝', todayOutput: 45, onDuty: true },
  { id: 2, name: '李四', workerNo: 'W002', department: '缝制一组', process: '锁边', todayOutput: 38, onDuty: true },
  { id: 3, name: '王五', workerNo: 'W003', department: '缝制二组', process: '平缝', todayOutput: 52, onDuty: true },
  { id: 4, name: '赵六', workerNo: 'W004', department: '缝制二组', process: '钉扣', todayOutput: 0, onDuty: false },
  { id: 5, name: '孙七', workerNo: 'W005', department: '缝制一组', process: '包缝', todayOutput: 41, onDuty: true },
  { id: 6, name: '周八', workerNo: 'W006', department: '缝制三组', process: '平缝', todayOutput: 48, onDuty: true },
  { id: 7, name: '吴九', workerNo: 'W007', department: '缝制三组', process: '锁边', todayOutput: 35, onDuty: true },
  { id: 8, name: '郑十', workerNo: 'W008', department: '缝制二组', process: '包缝', todayOutput: 0, onDuty: false }
])

const onDutyCount = computed(() => staffData.value.filter(s => s.onDuty).length)
const totalTodayOutput = computed(() => staffData.value.reduce((sum, s) => sum + s.todayOutput, 0))

const filteredStaff = computed(() => {
  if (!keyword.value) return staffData.value
  const kw = keyword.value.toLowerCase()
  return staffData.value.filter(s => s.name.includes(kw) || s.workerNo.toLowerCase().includes(kw))
})

const handleSearch = () => {}
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.search-bar { margin-bottom: 16rpx; }
.search-input {
  display: flex; align-items: center; gap: 12rpx;
  background: #fff; border-radius: 16rpx; padding: 16rpx 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.search-icon { font-size: 28rpx; }
.search-input input { flex: 1; font-size: 28rpx; }
.stats-row { display: flex; gap: 12rpx; margin-bottom: 20rpx; }
.stat-chip {
  flex: 1; background: #fff; border-radius: 12rpx; padding: 16rpx;
  text-align: center; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.chip-value { display: block; font-size: 36rpx; font-weight: 700; color: #333; }
.chip-label { display: block; font-size: 22rpx; color: #999; margin-top: 4rpx; }
.staff-list { display: flex; flex-direction: column; gap: 16rpx; }
.staff-card { background: #fff; border-radius: 16rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.staff-header { display: flex; align-items: center; gap: 16rpx; margin-bottom: 16rpx; }
.staff-avatar {
  width: 72rpx; height: 72rpx; border-radius: 50%; background: linear-gradient(135deg, #722ed1, #b37feb);
  display: flex; align-items: center; justify-content: center; color: #fff; font-size: 30rpx; font-weight: 600;
}
.staff-info { flex: 1; display: flex; flex-direction: column; gap: 4rpx; }
.staff-name { font-size: 30rpx; font-weight: 600; color: #333; }
.staff-no { font-size: 24rpx; color: #999; }
.duty-tag { padding: 4rpx 16rpx; border-radius: 8rpx; font-size: 22rpx; }
.duty-tag.on { background: rgba(82,196,26,0.1); color: #52c41a; }
.duty-tag.off { background: rgba(153,153,153,0.1); color: #999; }
.staff-detail { display: flex; gap: 20rpx; padding-top: 16rpx; border-top: 1rpx solid #f0f0f0; }
.detail-item { flex: 1; display: flex; flex-direction: column; gap: 4rpx; }
.detail-label { font-size: 22rpx; color: #999; }
.detail-value { font-size: 26rpx; color: #333; }
.detail-value.highlight { color: #722ed1; font-weight: 600; }
.empty-state { padding: 80rpx; text-align: center; }
.empty-text { font-size: 26rpx; color: #ccc; }
</style>
