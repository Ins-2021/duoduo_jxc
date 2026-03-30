<template>
  <div class="app-container">
    <!-- 筛选条件 -->
    <el-card shadow="never" style="margin-bottom: 16px;">
      <el-form :model="queryParams" inline>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="排序方式">
          <el-select v-model="queryParams.orderBy" style="width: 120px;">
            <el-option label="毛利" value="profit" />
            <el-option label="销售额" value="amount" />
            <el-option label="销售量" value="quantity" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 汇总卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-title">总销售额</div>
          <div class="summary-value">{{ formatMoney(summary.totalSalesAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-title">总成本</div>
          <div class="summary-value">{{ formatMoney(summary.totalCostAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="summary-card profit">
          <div class="summary-title">总毛利</div>
          <div class="summary-value">{{ formatMoney(summary.totalGrossProfit) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-title">平均毛利率</div>
          <div class="summary-value">{{ summary.avgGrossProfitRate }}%</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>利润趋势</template>
          <div ref="trendChartEl" class="chart-container" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>毛利分布</template>
          <div ref="pieChartEl" class="chart-container" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 款式利润排行 -->
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>款式利润排行</span>
          <el-button type="primary" size="small" @click="exportData">导出</el-button>
        </div>
      </template>
      <el-table :data="styleProfitList" v-loading="loading" border stripe>
        <el-table-column type="index" label="排名" width="60" align="center" />
        <el-table-column prop="styleNo" label="款式编号" width="120" />
        <el-table-column prop="styleName" label="款式名称" />
        <el-table-column prop="salesQuantity" label="销售数量" width="100" align="right" />
        <el-table-column prop="salesAmount" label="销售额" width="120" align="right">
          <template #default="{ row }">
            {{ formatMoney(row.salesAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="costAmount" label="成本" width="120" align="right">
          <template #default="{ row }">
            {{ formatMoney(row.costAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="grossProfit" label="毛利" width="120" align="right">
          <template #default="{ row }">
            <span :class="{ 'profit-positive': row.grossProfit > 0, 'profit-negative': row.grossProfit < 0 }">
              {{ formatMoney(row.grossProfit) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="grossProfitRate" label="毛利率" width="100" align="right">
          <template #default="{ row }">
            <el-progress
              :percentage="Math.max(0, Math.min(100, row.grossProfitRate))"
              :color="getProfitRateColor(row.grossProfitRate)"
            />
            <span style="font-size: 12px;">{{ row.grossProfitRate?.toFixed(1) }}%</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="showDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="款式利润详情" width="800px">
      <el-descriptions :column="2" border v-if="currentStyle">
        <el-descriptions-item label="款式编号">{{ currentStyle.styleNo }}</el-descriptions-item>
        <el-descriptions-item label="款式名称">{{ currentStyle.styleName }}</el-descriptions-item>
        <el-descriptions-item label="销售数量">{{ currentStyle.salesQuantity }}</el-descriptions-item>
        <el-descriptions-item label="订单数">{{ currentStyle.orderCount }}</el-descriptions-item>
        <el-descriptions-item label="销售额">{{ formatMoney(currentStyle.salesAmount) }}</el-descriptions-item>
        <el-descriptions-item label="成本">{{ formatMoney(currentStyle.costAmount) }}</el-descriptions-item>
        <el-descriptions-item label="毛利">{{ formatMoney(currentStyle.grossProfit) }}</el-descriptions-item>
        <el-descriptions-item label="毛利率">{{ currentStyle.grossProfitRate?.toFixed(2) }}%</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getProfitSummary, getStyleProfitRanking, getProfitTrend } from '@/api/report'
import type { ProfitSummary, StyleProfit, ProfitTrend } from '@/types/report'

defineOptions({ name: 'ProfitAnalysis' })

const loading = ref(false)
const dateRange = ref<string[]>([])
const trendChartEl = ref<HTMLDivElement>()
const pieChartEl = ref<HTMLDivElement>()
let trendChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null

const queryParams = reactive({
  startDate: '',
  endDate: '',
  orderBy: 'profit',
  limit: 20
})

const summary = ref<ProfitSummary>({
  totalSalesAmount: 0,
  totalCostAmount: 0,
  totalGrossProfit: 0,
  avgGrossProfitRate: 0,
  totalOrderCount: 0,
  totalSalesQuantity: 0
})

const styleProfitList = ref<StyleProfit[]>([])
const trendData = ref<ProfitTrend | null>(null)
const detailVisible = ref(false)
const currentStyle = ref<StyleProfit | null>(null)

// 格式化金额
const formatMoney = (amount: number | undefined) => {
  if (amount === undefined || amount === null) return '¥0.00'
  return '¥' + amount.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 获取利润率颜色
const getProfitRateColor = (rate: number) => {
  if (rate >= 30) return '#67C23A'
  if (rate >= 15) return '#E6A23C'
  return '#F56C6C'
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 设置日期参数
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.startDate = dateRange.value[0]
      queryParams.endDate = dateRange.value[1]
    }

    const [summaryRes, rankingRes, trendRes] = await Promise.all([
      getProfitSummary({ startDate: queryParams.startDate, endDate: queryParams.endDate }),
      getStyleProfitRanking({
        startDate: queryParams.startDate,
        endDate: queryParams.endDate,
        orderBy: queryParams.orderBy,
        limit: queryParams.limit
      }),
      getProfitTrend({ startDate: queryParams.startDate, endDate: queryParams.endDate })
    ])

    summary.value = summaryRes.data
    styleProfitList.value = rankingRes.data
    trendData.value = trendRes.data

    // 渲染图表
    nextTick(() => {
      renderTrendChart()
      renderPieChart()
    })
  } finally {
    loading.value = false
  }
}

// 渲染趋势图
const renderTrendChart = () => {
  if (!trendChartEl.value || !trendData.value) return

  if (!trendChart) {
    trendChart = echarts.init(trendChartEl.value)
  }

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['销售额', '成本', '毛利']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: trendData.value.dates
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: (value: number) => '¥' + (value / 10000).toFixed(1) + '万'
      }
    },
    series: [
      {
        name: '销售额',
        type: 'line',
        data: trendData.value.salesAmount,
        smooth: true,
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '成本',
        type: 'line',
        data: trendData.value.costAmount,
        smooth: true,
        itemStyle: { color: '#F56C6C' }
      },
      {
        name: '毛利',
        type: 'line',
        data: trendData.value.grossProfit,
        smooth: true,
        itemStyle: { color: '#67C23A' }
      }
    ]
  }

  trendChart.setOption(option)
}

// 渲染饼图
const renderPieChart = () => {
  if (!pieChartEl.value || !styleProfitList.value.length) return

  if (!pieChart) {
    pieChart = echarts.init(pieChartEl.value)
  }

  const data = styleProfitList.value.slice(0, 10).map(item => ({
    name: item.styleName,
    value: item.grossProfit
  }))

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      type: 'scroll',
      orient: 'vertical',
      right: 10,
      top: 20,
      bottom: 20
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        data
      }
    ]
  }

  pieChart.setOption(option)
}

// 显示详情
const showDetail = (row: StyleProfit) => {
  currentStyle.value = row
  detailVisible.value = true
}

// 导出数据
const exportData = () => {
  // TODO: 实现导出功能
  console.log('导出数据')
}

// 重置查询
const resetQuery = () => {
  dateRange.value = []
  queryParams.startDate = ''
  queryParams.endDate = ''
  queryParams.orderBy = 'profit'
  loadData()
}

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.chart-container {
  height: 300px;
}

.summary-card {
  text-align: center;
  padding: 20px;
}

.summary-title {
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.summary-card.profit .summary-value {
  color: #67C23A;
}

.profit-positive {
  color: #67C23A;
}

.profit-negative {
  color: #F56C6C;
}
</style>
