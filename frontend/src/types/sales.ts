/** 销售订单 */
export interface SalesOrderDTO {
  orderId?: number
  docNo?: string
  orderType?: number // 1:批发单, 2:零售单, 3:销售预订单
  docDate?: string
  storeId?: number
  customerId?: number
  operatorId?: number
  status?: number
  settleStatus?: number
  totalAmount?: number
  discountAmount?: number
  otherFee?: number
  actualAmount?: number
  paidAmount?: number
  remark?: string
  details?: SalesOrderDetailDTO[]
}

/** 销售订单明细 */
export interface SalesOrderDetailDTO {
  detailId?: number
  orderId?: number
  skuId?: number
  spuId?: number
  qty?: number
  bookedQty?: number
  unfulfilledQty?: number
  unitPrice?: number
  discountAmount?: number
  lineAmount?: number
  warehouseId?: number
  remark?: string
}

/** 销售退货单 */
export interface SalesReturnOrderDTO {
  orderId?: number
  docNo?: string
  docDate?: string
  customerId?: number
  customerName?: string
  originSalesId?: number
  status?: number
  refundAmount?: number
  refundMethod?: string
  remark?: string
  details?: SalesReturnDetailDTO[]
}

/** 销售退货明细 */
export interface SalesReturnDetailDTO {
  detailId?: number
  orderId?: number
  originSalesDetailId?: number
  skuId?: number
  spuId?: number
  originQty?: number
  returnedQty?: number
  availableQty?: number
  productName?: string
  productInfo?: string
  qty?: number
  unitPrice?: number
  lineAmount?: number
  warehouseId?: number
  remark?: string
}

/** 销售退货源单 */
export interface SalesReturnSourceOrderDTO {
  orderId: number
  docNo: string
  docDate: string
  customerId: number
  customerName: string
  actualAmount: number
  details: SalesReturnSourceDetailDTO[]
}

/** 销售退货源单明细 */
export interface SalesReturnSourceDetailDTO {
  detailId: number
  skuId: number
  spuId: number
  productName: string
  productInfo: string
  qty: number
  returnedQty: number
  availableQty: number
  unitPrice: number
  lineAmount: number
  warehouseId: number
  remark: string
}

/** 采购订单 */
export interface PurchaseOrderDTO {
  orderId?: number
  docNo?: string
  orderType?: number
  docDate?: string
  supplierId?: number
  status?: number
  totalAmount?: number
  paidAmount?: number
  discountAmount?: number
  otherFee?: number
  actualAmount?: number
  remark?: string
  details?: PurchaseOrderDetailDTO[]
}

/** 采购订单明细 */
export interface PurchaseOrderDetailDTO {
  detailId?: number
  orderId?: number
  skuId?: number
  spuId?: number
  qty?: number
  unitPrice?: number
  lineAmount?: number
  warehouseId?: number
}

/** 销售日报 */
export interface SalesDailyReportDTO {
  reportDate: string
  orderCount: number
  totalAmount: number
  discountAmount: number
  actualAmount: number
  refundAmount: number
  netAmount: number
}
