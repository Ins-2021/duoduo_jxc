<template>
  <view class="container">
    <view class="filter-tabs">
      <view
        v-for="tab in levelTabs"
        :key="tab.value"
        class="tab-item"
        :class="{ active: currentLevel === tab.value }"
        @click="currentLevel = tab.value"
      >
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <view v-if="filteredAlerts.length" class="alert-list">
      <view v-for="item in filteredAlerts" :key="item.id" class="alert-card" :class="'alert-' + item.level" @click="showDetail(item)">
        <view class="alert-icon-wrap" :class="'icon-' + item.level">
          <text class="alert-icon">{{ item.icon }}</text>
        </view>
        <view class="alert-content">
          <view class="alert-top">
            <text class="alert-title">{{ item.title }}</text>
            <view class="level-badge" :class="'badge-' + item.level">
              <text>{{ item.levelText }}</text>
            </view>
          </view>
          <text class="alert-desc">{{ item.description }}</text>
          <view class="alert-bottom">
            <text class="alert-source">{{ item.source }}</text>
            <text class="alert-time">{{ item.time }}</text>
          </view>
        </view>
      </view>
    </view>
    <view v-else class="empty-state">
      <text class="empty-text">暂无预警信息</text>
    </view>

    <view v-if="showModal" class="modal-mask" @click="showModal = false">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <view class="level-badge-lg" :class="'badge-' + currentAlert?.level">
            <text>{{ currentAlert?.levelText }}</text>
          </view>
          <text class="modal-title">{{ currentAlert?.title }}</text>
        </view>
        <view class="modal-body">
          <view class="detail-row">
            <text class="detail-label">预警来源</text>
            <text class="detail-value">{{ currentAlert?.source }}</text>
          </view>
          <view class="detail-row">
            <text class="detail-label">预警时间</text>
            <text class="detail-value">{{ currentAlert?.time }}</text>
          </view>
          <view class="detail-row">
            <text class="detail-label">关联订单</text>
            <text class="detail-value">{{ currentAlert?.relatedOrder || '-' }}</text>
          </view>
          <view class="detail-desc">
            <text class="detail-label">详细描述</text>
            <text class="desc-text">{{ currentAlert?.fullDescription }}</text>
          </view>
          <view class="detail-desc">
            <text class="detail-label">建议措施</text>
            <text class="desc-text">{{ currentAlert?.suggestion }}</text>
          </view>
        </view>
        <button class="close-btn" @click="showModal = false">关闭</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getAlerts } from '@/api/boss'

const levelTabs = [
  { label: '全部', value: 'all' },
  { label: '紧急', value: 'high' },
  { label: '重要', value: 'medium' },
  { label: '一般', value: 'low' }
]

const currentLevel = ref('all')
const showModal = ref(false)
const currentAlert = ref<any>(null)

const alertData = ref([
  {
    id: 1, icon: '🔴', title: '订单延期', description: 'Nike订单PO2026032501已延期2天',
    fullDescription: 'Nike订单E005款，订单数量300件，当前仅完成100件(33%)，交期2026-03-28已过，目前延期2天。主要原因是缝制工序产能不足。',
    suggestion: '建议增加缝制人员排班或调配其他组支援，同时与客户沟通延期交付方案。',
    level: 'high', levelText: '紧急', source: '生产系统', relatedOrder: 'PO2026032501', time: '2026-03-29 10:30'
  },
  {
    id: 2, icon: '🔴', title: '交期预警', description: 'ZARA订单PO2026032902交期临近，完成率仅53%',
    fullDescription: 'ZARA订单B002款，订单数量150件，当前完成80件(53%)，交期2026-04-10，剩余12天需完成70件，日均需完成5.8件。',
    suggestion: '建议加强进度跟踪，确保每日产量达标。如有风险及时预警并调整排期。',
    level: 'high', levelText: '紧急', source: '生产系统', relatedOrder: 'PO2026032902', time: '2026-03-29 09:00'
  },
  {
    id: 3, icon: '🟠', title: '物料库存预警', description: '面料B002库存仅剩50米，预计3天内耗尽',
    fullDescription: '面料B002(蓝色涤纶)当前库存50米，根据当前生产消耗速度(日均15米)，预计3天内耗尽。关联订单PO2026032902正在生产中。',
    suggestion: '建议立即联系供应商下单补货，同时评估是否需要调整生产计划。',
    level: 'medium', levelText: '重要', source: '仓储系统', relatedOrder: 'PO2026032902', time: '2026-03-28 16:00'
  },
  {
    id: 4, icon: '🟠', title: '合格率下降', description: '本周合格率96.8%，低于目标值97%',
    fullDescription: '本周质检合格率96.8%，较上周下降0.5个百分点，低于目标值97%。主要不良项：跳线(35%)、毛边(28%)、尺寸偏差(20%)。',
    suggestion: '建议对跳线和毛边问题进行专项整改，加强首件检验和过程巡检。',
    level: 'medium', levelText: '重要', source: '质量系统', relatedOrder: '', time: '2026-03-28 18:00'
  },
  {
    id: 5, icon: '🔵', title: '人员出勤率偏低', description: '今日出勤率83.3%，缝制二组2人请假',
    fullDescription: '今日车间出勤率83.3%(25/30)，缝制二组赵六(病假)、郑十(事假)未到岗，可能影响当日产量目标达成。',
    suggestion: '建议关注当日产量变化，必要时可临时调配人员支援缝制二组。',
    level: 'low', levelText: '一般', source: '人事系统', relatedOrder: '', time: '2026-03-29 08:30'
  },
  {
    id: 6, icon: '🔵', title: '设备保养提醒', description: '3号工位平缝机已运行500小时，建议保养',
    fullDescription: '3号工位平缝机(PG-003)累计运行500小时，已达到保养周期。上次保养时间2026-02-27。',
    suggestion: '建议安排设备保养，可在非生产时段进行，避免影响正常生产。',
    level: 'low', levelText: '一般', source: '设备系统', relatedOrder: '', time: '2026-03-29 07:00'
  }
])

const filteredAlerts = computed(() => {
  if (currentLevel.value === 'all') return alertData.value
  return alertData.value.filter(a => a.level === currentLevel.value)
})

const showDetail = (item: any) => {
  currentAlert.value = item
  showModal.value = true
}
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.filter-tabs { display: flex; gap: 16rpx; margin-bottom: 20rpx; background: #fff; border-radius: 16rpx; padding: 16rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.tab-item { flex: 1; text-align: center; padding: 16rpx 0; border-radius: 12rpx; font-size: 26rpx; color: #666; }
.tab-item.active { background: #cf1322; color: #fff; font-weight: 600; }
.alert-list { display: flex; flex-direction: column; gap: 16rpx; }
.alert-card {
  display: flex; gap: 16rpx; background: #fff; border-radius: 16rpx; padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); border-left: 6rpx solid;
}
.alert-high { border-left-color: #ff4d4f; }
.alert-medium { border-left-color: #faad14; }
.alert-low { border-left-color: #1890ff; }
.alert-icon-wrap { width: 72rpx; height: 72rpx; border-radius: 16rpx; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.icon-high { background: rgba(255,77,79,0.08); }
.icon-medium { background: rgba(250,173,20,0.08); }
.icon-low { background: rgba(24,144,255,0.08); }
.alert-icon { font-size: 36rpx; }
.alert-content { flex: 1; }
.alert-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 8rpx; }
.alert-title { font-size: 30rpx; font-weight: 600; color: #333; flex: 1; margin-right: 12rpx; }
.level-badge { font-size: 22rpx; padding: 2rpx 12rpx; border-radius: 6rpx; white-space: nowrap; }
.badge-high { color: #ff4d4f; background: rgba(255,77,79,0.1); }
.badge-medium { color: #faad14; background: rgba(250,173,20,0.1); }
.badge-low { color: #1890ff; background: rgba(24,144,255,0.1); }
.alert-desc { display: block; font-size: 26rpx; color: #999; margin-bottom: 12rpx; line-height: 1.5; }
.alert-bottom { display: flex; justify-content: space-between; font-size: 22rpx; color: #ccc; }
.modal-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: center; justify-content: center; }
.modal-content { background: #fff; border-radius: 20rpx; padding: 40rpx; width: 85%; max-height: 85vh; overflow-y: auto; }
.modal-header { text-align: center; margin-bottom: 30rpx; }
.level-badge-lg { display: inline-block; font-size: 26rpx; padding: 4rpx 20rpx; border-radius: 8rpx; margin-bottom: 12rpx; }
.modal-title { display: block; font-size: 34rpx; font-weight: 600; color: #333; }
.modal-body { margin-bottom: 30rpx; }
.detail-row { display: flex; justify-content: space-between; padding: 16rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.detail-label { font-size: 26rpx; color: #999; }
.detail-value { font-size: 26rpx; color: #333; }
.detail-desc { padding: 16rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.detail-desc .detail-label { display: block; margin-bottom: 8rpx; }
.desc-text { font-size: 26rpx; color: #666; line-height: 1.6; }
.close-btn { background: #f5f5f5; color: #666; border-radius: 12rpx; padding: 20rpx 0; font-size: 30rpx; border: none; }
.empty-state { padding: 80rpx; text-align: center; }
.empty-text { font-size: 26rpx; color: #ccc; }
</style>
