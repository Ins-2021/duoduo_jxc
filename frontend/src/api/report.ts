import request from '@/utils/request'
import type { PageQuery, SalesDailyReportDTO } from '@/types'

export function getSalesDailyReport(params: PageQuery & { startDate?: string; endDate?: string; customerId?: number; spuId?: number }) {
  return request({
    url: '/report/sales/daily',
    method: 'get',
    params
  })
}

// 利润分析相关接口
export function getProfitSummary(params: { startDate?: string; endDate?: string }) {
  return request({
    url: '/report/profit/summary',
    method: 'get',
    params
  })
}

export function getStyleProfitRanking(params: { startDate?: string; endDate?: string; orderBy?: string; limit?: number }) {
  return request({
    url: '/report/profit/style-ranking',
    method: 'get',
    params
  })
}

export function getCustomerProfitRanking(params: { startDate?: string; endDate?: string }) {
  return request({
    url: '/report/profit/customer-ranking',
    method: 'get',
    params
  })
}

export function getProfitTrend(params: { startDate?: string; endDate?: string }) {
  return request({
    url: '/report/profit/trend',
    method: 'get',
    params
  })
}

export function getStyleProfitDetail(styleId: number, params: { startDate?: string; endDate?: string }) {
  return request({
    url: `/report/profit/style-detail/${styleId}`,
    method: 'get',
    params
  })
}
