<template>
  <div class="home-page">
    <div class="home-main">
      <div class="top-metrics">
        <div v-for="m in topMetrics" :key="m.key" class="top-metric">
          <div class="top-metric-value">
            <span class="top-metric-number">{{ m.value }}</span>
            <span class="top-metric-unit" v-if="m.unit">{{ m.unit }}</span>
          </div>
          <div class="top-metric-title">{{ m.title }}</div>
          <div class="top-metric-sub">
            <span class="top-metric-sub-label">{{ m.subTitle }}</span>
            <span class="top-metric-sub-value">{{ m.subValue }}</span>
          </div>
        </div>
      </div>

      <div class="entry-area">
        <div class="entry-tabs">
          <div class="entry-tab primary" @click="go('salesOut')">销售出库</div>
          <div class="entry-tab" @click="go('inventorySearch')">库存查询</div>
        </div>
        <div class="entry-subtitle">客户对账单</div>
        <div class="quick-links">
          <div class="quick-link" @click="go('productManage')">商品管理</div>
          <div class="quick-link" @click="go('purchaseIn')">进货入库</div>
          <div class="quick-link" @click="go('customerManage')">客户管理</div>
          <div class="quick-link" @click="go('salesStats')">销售统计</div>
          <div class="quick-link" @click="go('arAp')">应收欠款</div>
        </div>
      </div>

      <div class="mid-stats">
        <div v-for="s in midStats" :key="s.key" class="mid-stat">
          <div class="mid-stat-value">{{ s.value }}</div>
          <div class="mid-stat-title">{{ s.title }}</div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <div class="panel-title">数据概览</div>
          <div class="panel-right">
            <span class="panel-subtitle">销售单</span>
            <el-icon class="panel-info"><InfoFilled /></el-icon>
          </div>
        </div>
        <div class="panel-body">
          <div ref="overviewChartEl" class="chart chart-large" />
        </div>
      </div>

      <div class="bottom-panels">
        <div class="panel panel-half">
          <div class="panel-header">
            <div class="panel-title">资产状况</div>
            <div class="panel-right">
              <el-icon class="panel-info"><InfoFilled /></el-icon>
            </div>
          </div>
          <div class="panel-body assets-body">
            <div class="assets-summary">
              <div class="assets-summary-title">总资产(库存+客户余额+应收欠款+应付欠款)</div>
              <div class="assets-summary-value">{{ overview?.assetsTotal ?? '0.00' }}</div>
            </div>
            <div class="assets-content">
              <div ref="assetChartEl" class="chart chart-donut" />
              <div class="assets-legend">
                <div v-for="a in assetLegend" :key="a.key" class="assets-legend-item">
                  <span class="dot" :style="{ backgroundColor: a.color }" />
                  <span class="assets-legend-name">{{ a.name }}</span>
                  <span class="assets-legend-value">{{ a.value }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="panel panel-half">
          <div class="panel-header">
            <div class="panel-title">现金流趋势</div>
            <div class="panel-right">
              <el-icon class="panel-info"><InfoFilled /></el-icon>
            </div>
          </div>
          <div class="panel-body">
            <div ref="cashflowChartEl" class="chart chart-small" />
          </div>
        </div>
      </div>
    </div>

    <div class="home-side">
      <div class="side-card">
        <div class="side-card-header">
          <div class="side-card-title">公告信息</div>
          <el-icon class="side-card-icon"><MoreFilled /></el-icon>
        </div>
        <div class="side-card-body">
          <div class="side-card-text">可在系统设置里设置公告信息</div>
        </div>
      </div>

      <div class="side-card">
        <div class="side-card-header">
          <div class="side-card-title">打印助手</div>
        </div>
        <div class="side-card-body">
          <div class="side-card-text">如有打印需求，请下载我们的打印助手。</div>
          <el-button class="side-btn" type="primary" size="small" @click="go('printHelper')">下载打印助手</el-button>
          <div class="side-card-hint">
            您也可以下载我们的 PC 版本。<br />
            <a class="side-link" href="javascript:void(0)" @click.prevent="go('pcHelper')">微信在线客服咨询</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getHomeOverview, type HomeOverview } from '@/api/home'
import { useRouter } from 'vue-router'

const router = useRouter()

const overview = ref<HomeOverview | null>(null)

const topMetrics = computed(() => {
  const d = overview.value
  return [
    {
      key: 'grossProfit',
      title: '今日销售毛利',
      value: d?.todayGrossProfit ?? '0.00',
      unit: '元',
      subTitle: '昨日',
      subValue: d?.yesterdayGrossProfit ?? '0.00'
    },
    {
      key: 'salesAmount',
      title: '今日销售额',
      value: d?.todaySalesAmount ?? '0.00',
      unit: '元',
      subTitle: '昨日',
      subValue: d?.yesterdaySalesAmount ?? '0.00'
    },
    {
      key: 'salesCount',
      title: '今日销售单数',
      value: d?.todaySalesCount ?? 0,
      unit: '单',
      subTitle: '昨日',
      subValue: d?.yesterdaySalesCount ?? 0
    },
    {
      key: 'cashIn',
      title: '今日现金入账',
      value: d?.todayCashIn ?? '0.00',
      unit: '元',
      subTitle: '昨日',
      subValue: d?.yesterdayCashIn ?? '0.00'
    }
  ]
})

const midStats = computed(() => {
  const d = overview.value
  return [
    { key: 'customerCount', title: '客户总数', value: d?.customerCount ?? 0 },
    { key: 'supplierCount', title: '供应商总数', value: d?.supplierCount ?? 0 },
    { key: 'inventoryTotal', title: '库存总数 / 总金额', value: `${d?.inventoryQtyTotal ?? 0} / ${d?.inventoryAmountTotal ?? '0.00'}` },
    { key: 'inventoryWarn', title: '库存预警', value: d?.inventoryWarnCount ?? 0 },
    { key: 'shelfLifeWarn', title: '保质期预警', value: d?.shelfLifeWarnCount ?? 0 },
    { key: 'ar', title: '应收欠款', value: d?.receivableAmount ?? '0.00' },
    { key: 'ap', title: '应付欠款', value: d?.payableAmount ?? '0.00' }
  ]
})

const assetLegend = computed(() => {
  const d = overview.value
  return [
    { key: 'inventory', name: '库存余额', color: '#409eff', value: d?.inventoryAmountTotal ?? '0.00' },
    { key: 'receivable', name: '应收欠款', color: '#67c23a', value: d?.receivableAmount ?? '0.00' },
    { key: 'payable', name: '应付欠款', color: '#f56c6c', value: d?.payableAmount ?? '0.00' }
  ]
})

const overviewChartEl = ref<HTMLDivElement | null>(null)
const assetChartEl = ref<HTMLDivElement | null>(null)
const cashflowChartEl = ref<HTMLDivElement | null>(null)

let overviewChart: echarts.ECharts | null = null
let assetChart: echarts.ECharts | null = null
let cashflowChart: echarts.ECharts | null = null

const renderCharts = () => {
  const d = overview.value
  if (!d) return

  if (overviewChartEl.value) {
    overviewChart ||= echarts.init(overviewChartEl.value)
    overviewChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 50, right: 30, top: 20, bottom: 30 },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: d.overviewDates,
        axisLine: { lineStyle: { color: '#e6e6e6' } },
        axisTick: { show: false },
        axisLabel: { color: '#909399' }
      },
      yAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: '#f0f0f0' } },
        axisLabel: { color: '#909399' }
      },
      series: [
        {
          name: '销售单',
          type: 'line',
          data: d.overviewSalesOrderCount,
          smooth: false,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { color: '#409eff', width: 2 },
          itemStyle: { color: '#409eff' },
          areaStyle: { color: 'rgba(64,158,255,0.12)' }
        }
      ]
    })
  }

  if (assetChartEl.value) {
    assetChart ||= echarts.init(assetChartEl.value)
    assetChart.setOption({
      tooltip: { trigger: 'item' },
      series: [
        {
          type: 'pie',
          radius: ['62%', '82%'],
          center: ['35%', '55%'],
          avoidLabelOverlap: true,
          label: { show: false },
          labelLine: { show: false },
          data: [
            { name: '库存余额', value: Number(d.inventoryAmountTotal || 0), itemStyle: { color: '#409eff' } },
            { name: '应收欠款', value: Number(d.receivableAmount || 0), itemStyle: { color: '#67c23a' } },
            { name: '应付欠款', value: Number(d.payableAmount || 0), itemStyle: { color: '#f56c6c' } }
          ]
        }
      ]
    })
  }

  if (cashflowChartEl.value) {
    cashflowChart ||= echarts.init(cashflowChartEl.value)
    cashflowChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 50, right: 20, top: 20, bottom: 30 },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: d.cashflowDates,
        axisLine: { lineStyle: { color: '#e6e6e6' } },
        axisTick: { show: false },
        axisLabel: { color: '#909399' }
      },
      yAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: '#f0f0f0' } },
        axisLabel: { color: '#909399' }
      },
      series: [
        {
          name: '收入',
          type: 'line',
          data: d.cashflowIncome,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { color: '#f56c6c', width: 2 },
          itemStyle: { color: '#f56c6c' }
        },
        {
          name: '支出',
          type: 'line',
          data: d.cashflowExpense,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { color: '#409eff', width: 2 },
          itemStyle: { color: '#409eff' }
        }
      ]
    })
  }
}

const resizeHandler = () => {
  overviewChart?.resize()
  assetChart?.resize()
  cashflowChart?.resize()
}

const buildFallbackOverview = (): HomeOverview => {
  const today = new Date()
  const dates: string[] = []
  for (let i = 6; i >= 0; i--) {
    const d = new Date(today)
    d.setDate(today.getDate() - i)
    const y = d.getFullYear()
    const m = String(d.getMonth() + 1).padStart(2, '0')
    const dd = String(d.getDate()).padStart(2, '0')
    dates.push(`${y}-${m}-${dd}`)
  }
  return {
    todayGrossProfit: 0,
    yesterdayGrossProfit: 0,
    todaySalesAmount: 0,
    yesterdaySalesAmount: 0,
    todaySalesCount: 0,
    yesterdaySalesCount: 0,
    todayCashIn: 0,
    yesterdayCashIn: 0,
    customerCount: 0,
    supplierCount: 0,
    inventoryQtyTotal: 0,
    inventoryAmountTotal: 0,
    inventoryWarnCount: 0,
    shelfLifeWarnCount: 0,
    receivableAmount: 0,
    payableAmount: 0,
    assetsTotal: 0,
    overviewDates: dates,
    overviewSalesOrderCount: dates.map(() => 0),
    cashflowDates: dates,
    cashflowIncome: dates.map(() => 0),
    cashflowExpense: dates.map(() => 0)
  }
}

const load = async () => {
  try {
    const res = await getHomeOverview()
    overview.value = res.data
  } catch {
    overview.value = buildFallbackOverview()
  }
  await nextTick()
  renderCharts()
}

type GoTarget =
  | 'salesOut'
  | 'inventorySearch'
  | 'productManage'
  | 'purchaseIn'
  | 'customerManage'
  | 'salesStats'
  | 'arAp'
  | 'printHelper'
  | 'pcHelper'

const go = (t: GoTarget) => {
  if (t === 'salesOut') return router.push('/sales/order/add')
  if (t === 'inventorySearch') return router.push('/inventory')
  if (t === 'productManage') return router.push('/data')
  if (t === 'purchaseIn') return router.push('/purchase/add')
  if (t === 'customerManage') return router.push('/data')
  if (t === 'salesStats') return router.push('/reports')
  if (t === 'arAp') return router.push('/finance/receivable')
  if (t === 'printHelper') {
    ElMessage.info('功能即将上线，敬请期待')
    return
  }
  if (t === 'pcHelper') {
    window.open('https://example.com/wechat', '_blank')
    return
  }
  ElMessage.info('功能即将上线，敬请期待')
}

onMounted(() => {
  load()
  window.addEventListener('resize', resizeHandler)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeHandler)
  overviewChart?.dispose()
  assetChart?.dispose()
  cashflowChart?.dispose()
  overviewChart = null
  assetChart = null
  cashflowChart = null
})
</script>

<style scoped>
.home-page {
  display: flex;
  gap: 10px;
  min-height: 800px;
}

.home-main {
  flex: 1;
  min-width: 820px;
}

.home-side {
  width: 300px;
  flex: 0 0 300px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.top-metrics {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  background: #fff;
  border: 1px solid #ebeef5;
}

.top-metric {
  padding: 14px 18px;
  border-right: 1px solid #ebeef5;
}
.top-metric:last-child {
  border-right: none;
}

.top-metric-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.top-metric-number {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}

.top-metric-unit {
  font-size: 12px;
  color: #909399;
}

.top-metric-title {
  margin-top: 2px;
  font-size: 12px;
  color: #909399;
}

.top-metric-sub {
  margin-top: 2px;
  font-size: 12px;
  color: #c0c4cc;
}
.top-metric-sub-label {
  margin-right: 4px;
}
.top-metric-sub-value {
  color: #909399;
}

.entry-area {
  margin-top: 10px;
  background: #fff;
  border: 1px solid #ebeef5;
  padding: 18px 18px 16px;
}

.entry-tabs {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.entry-tab {
  width: 120px;
  height: 34px;
  line-height: 34px;
  text-align: center;
  border-radius: 18px;
  background: #f5f7fa;
  color: #606266;
  font-weight: 600;
  cursor: pointer;
  user-select: none;
  border: 1px solid #dcdfe6;
}

.entry-tab.primary {
  background: linear-gradient(90deg, #4aa3ff, #409eff);
  color: #fff;
  border-color: transparent;
}

.entry-subtitle {
  margin-top: 10px;
  text-align: center;
  font-size: 12px;
  color: #909399;
}

.quick-links {
  margin-top: 12px;
  display: flex;
  justify-content: center;
  gap: 18px;
  color: #606266;
  font-size: 12px;
}

.quick-link {
  cursor: pointer;
  user-select: none;
}
.quick-link:hover {
  color: #409eff;
}

.mid-stats {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #fff;
  border: 1px solid #ebeef5;
}

.mid-stat {
  padding: 12px 10px;
  border-right: 1px solid #ebeef5;
  text-align: center;
}
.mid-stat:last-child {
  border-right: none;
}

.mid-stat-value {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}

.mid-stat-title {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.panel {
  margin-top: 10px;
  background: #fff;
  border: 1px solid #ebeef5;
}

.panel-header {
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
  border-bottom: 1px solid #ebeef5;
}

.panel-title {
  font-size: 13px;
  color: #606266;
  font-weight: 600;
}

.panel-right {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #909399;
}

.panel-info {
  font-size: 14px;
  color: #c0c4cc;
}

.panel-body {
  padding: 10px 12px 12px;
}

.chart {
  width: 100%;
}
.chart-large {
  height: 260px;
}
.chart-small {
  height: 220px;
}
.chart-donut {
  height: 210px;
}

.bottom-panels {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}

.panel-half {
  flex: 1;
}

.assets-body {
  padding: 12px;
}

.assets-summary {
  color: #909399;
  font-size: 12px;
}
.assets-summary-value {
  margin-top: 6px;
  font-size: 20px;
  font-weight: 700;
  color: #303133;
}

.assets-content {
  margin-top: 8px;
  display: grid;
  grid-template-columns: 260px 1fr;
  align-items: center;
  gap: 6px;
}

.assets-legend {
  padding-right: 10px;
}

.assets-legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #606266;
  margin: 6px 0;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}

.assets-legend-name {
  flex: 1;
  color: #909399;
}

.assets-legend-value {
  font-weight: 600;
  color: #606266;
}

.side-card {
  background: #fff;
  border: 1px solid #ebeef5;
}

.side-card-header {
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
  border-bottom: 1px solid #ebeef5;
}

.side-card-title {
  font-size: 13px;
  color: #606266;
  font-weight: 600;
}

.side-card-icon {
  color: #c0c4cc;
}

.side-card-body {
  padding: 12px;
}

.side-card-text {
  font-size: 12px;
  color: #909399;
  line-height: 18px;
}

.side-btn {
  margin-top: 10px;
}

.side-card-hint {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
  line-height: 18px;
}

.side-link {
  color: #67c23a;
  text-decoration: none;
}
.side-link:hover {
  text-decoration: underline;
}
</style>
