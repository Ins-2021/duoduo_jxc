import request from '@/utils/request'
import type { PageQuery, SalesDailyReportDTO } from '@/types'

export function getSalesDailyReport(params: PageQuery & { startDate?: string; endDate?: string; customerId?: number; spuId?: number }) {
  return request({
    url: '/report/sales-daily',
    method: 'get',
    params
  })
}
