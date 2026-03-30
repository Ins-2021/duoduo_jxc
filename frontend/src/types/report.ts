// 利润分析相关类型

export interface ProfitSummary {
  totalSalesAmount: number
  totalCostAmount: number
  totalGrossProfit: number
  avgGrossProfitRate: number
  totalOrderCount: number
  totalSalesQuantity: number
}

export interface StyleProfit {
  styleId: number
  styleNo: string
  styleName: string
  salesQuantity: number
  salesAmount: number
  costAmount: number
  grossProfit: number
  grossProfitRate: number
  orderCount: number
}

export interface CustomerProfit {
  customerId: number
  customerCode: string
  customerName: string
  salesQuantity: number
  salesAmount: number
  costAmount: number
  grossProfit: number
  grossProfitRate: number
  orderCount: number
}

export interface ColorProfit {
  colorCode: string
  colorName: string
  salesQuantity: number
  salesAmount: number
  costAmount: number
  grossProfit: number
  grossProfitRate: number
}

export interface SizeProfit {
  sizeName: string
  salesQuantity: number
  salesAmount: number
  costAmount: number
  grossProfit: number
  grossProfitRate: number
}

export interface MonthlyProfit {
  month: string
  salesQuantity: number
  salesAmount: number
  costAmount: number
  grossProfit: number
  grossProfitRate: number
}

export interface StyleProfitDetail {
  summary: StyleProfit
  byColor: ColorProfit[]
  bySize: SizeProfit[]
  byMonth: MonthlyProfit[]
}

export interface ProfitTrend {
  dates: string[]
  salesAmount: number[]
  costAmount: number[]
  grossProfit: number[]
  totalSalesAmount: number
  totalCostAmount: number
  totalGrossProfit: number
  avgGrossProfitRate: number
}
