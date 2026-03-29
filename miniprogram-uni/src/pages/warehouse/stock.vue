<template>
  <view class="container">
    <view class="search-section">
      <view class="search-wrap">
        <input
          v-model="keyword"
          class="search-input"
          placeholder="搜索款号/颜色/尺码"
          confirm-type="search"
          @confirm="onSearch"
        />
        <view class="search-btn" @click="onSearch">搜索</view>
      </view>
    </view>

    <view v-if="list.length > 0" class="list-section">
      <view v-for="item in list" :key="item.id" class="list-item">
        <view class="item-header">
          <text class="style-no">{{ item.styleNo }}</text>
          <text class="warning-tag" :class="item.warning ? 'tag-warn' : 'tag-normal'">
            {{ item.warning ? '预警' : '正常' }}
          </text>
        </view>
        <view class="item-info">
          <text class="info-text">颜色：{{ item.color }}</text>
          <text class="info-text">尺码：{{ item.size }}</text>
        </view>
        <view class="item-stock">
          <text class="stock-label">库存数量</text>
          <text class="stock-value" :class="{ 'stock-low': item.warning }">{{ item.stockQuantity }}</text>
          <text v-if="item.warning" class="stock-warn-text">低于预警值 {{ item.warningThreshold }}</text>
        </view>
        <view class="item-location">
          <text>库位：{{ item.location || '未分配' }}</text>
        </view>
      </view>
    </view>

    <view v-if="!loading && list.length === 0" class="empty-state">
      <text>{{ searched ? '未找到匹配的库存记录' : '暂无库存数据' }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getStockList } from '@/api/warehouse'

const keyword = ref('')
const list = ref<any[]>([])
const loading = ref(false)
const searched = ref(false)

const onSearch = async () => {
  loading.value = true
  searched.value = true
  try {
    const params: any = {}
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    const res = await getStockList(params)
    list.value = res.data || []
  } catch {} finally {
    loading.value = false
  }
}

onShow(() => { onSearch() })
</script>

<style scoped>
.container { padding: 24rpx; }
.search-section { margin-bottom: 24rpx; }
.search-wrap {
  display: flex; gap: 16rpx;
  background: #fff; border-radius: 12rpx; padding: 16rpx 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.search-input { flex: 1; font-size: 28rpx; height: 72rpx; }
.search-btn {
  background: #1890ff; color: #fff; font-size: 28rpx;
  padding: 0 32rpx; border-radius: 12rpx;
  display: flex; align-items: center;
}
.list-section { display: flex; flex-direction: column; gap: 20rpx; }
.list-item {
  background: #fff; border-radius: 16rpx; padding: 28rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.style-no { font-size: 32rpx; font-weight: 600; color: #333; }
.warning-tag {
  font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 6rpx; color: #fff;
}
.tag-normal { background: #52c41a; }
.tag-warn { background: #ff4d4f; }
.item-info { display: flex; gap: 24rpx; margin-bottom: 16rpx; }
.info-text { font-size: 26rpx; color: #666; }
.item-stock { display: flex; align-items: center; gap: 16rpx; margin-bottom: 12rpx; }
.stock-label { font-size: 26rpx; color: #999; }
.stock-value { font-size: 32rpx; font-weight: 700; color: #333; }
.stock-value.stock-low { color: #ff4d4f; }
.stock-warn-text { font-size: 22rpx; color: #ff4d4f; }
.item-location { font-size: 26rpx; color: #999; }
.empty-state { text-align: center; padding: 100rpx 0; color: #999; font-size: 28rpx; }
</style>
