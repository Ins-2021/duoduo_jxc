/** 库存 */
export interface InventoryDTO {
  inventoryId: number
  warehouseId: number
  skuId: number
  qty: number
}

/** 库存预警 */
export interface InventoryAlertDTO {
  alertId: number
  warehouseId: number
  warehouseName: string
  skuId: number
  skuCode: string
  skuName: string
  attr1?: string
  attr2?: string
  currentQty: number
  minQty: number
  maxQty: number
  alertType: number
  status: number
  alertTime: string
}

/** 调拨单（后端字段） */
export interface TransferOrderDTO {
  transferId?: number
  transferNo?: string
  fromWarehouseId?: number
  toWarehouseId?: number
  fromWarehouseName?: string
  toWarehouseName?: string
  status?: number
  transferDate?: string
  remark?: string
  details?: TransferOrderDetailDTO[]
  createTime?: string
  updateTime?: string
}

/** 调拨单明细 */
export interface TransferOrderDetailDTO {
  detailId?: number
  transferId?: number
  skuId?: number
  skuCode?: string
  skuName?: string
  attr1?: string
  attr2?: string
  qty?: number
  costPrice?: number
}

/** 盘点单 */
export interface InventoryCheckDTO {
  checkId?: number
  checkNo?: string
  warehouseId?: number
  warehouseName?: string
  status?: number
  checkDate?: string
  remark?: string
  details?: InventoryCheckDetailDTO[]
  createTime?: string
  updateTime?: string
}

/** 盘点单明细 */
export interface InventoryCheckDetailDTO {
  detailId?: number
  checkId?: number
  skuId?: number
  skuCode?: string
  skuName?: string
  attr1?: string
  attr2?: string
  systemQty?: number
  actualQty?: number
  diffQty?: number
  costPrice?: number
  diffAmount?: number
}

/** 组装拆卸单（后端字段） */
export interface AssemblyOrderDTO {
  assemblyId?: number
  assemblyNo?: string
  warehouseId?: number
  warehouseName?: string
  type?: number // 1:组装, 2:拆卸
  status?: number
  assemblyDate?: string
  remark?: string
  details?: AssemblyOrderDetailDTO[]
  createTime?: string
  updateTime?: string
}

/** 组装拆卸单明细 */
export interface AssemblyOrderDetailDTO {
  detailId?: number
  assemblyId?: number
  skuId?: number
  skuCode?: string
  skuName?: string
  attr1?: string
  attr2?: string
  qty?: number
  costPrice?: number
  itemType?: number
}

/** 出入库单（后端字段） */
export interface StockInOutDTO {
  inOutId?: number
  billNo?: string
  type?: number // 1:入库, 2:出库
  warehouseId?: number
  warehouseName?: string
  status?: number
  billDate?: string
  remark?: string
  details?: StockInOutDetailDTO[]
  createTime?: string
  updateTime?: string
}

/** 出入库单明细 */
export interface StockInOutDetailDTO {
  detailId?: number
  inOutId?: number
  skuId?: number
  skuCode?: string
  skuName?: string
  attr1?: string
  attr2?: string
  qty?: number
  costPrice?: number
  amount?: number
}

// ========================
// 前端 View Model 类型（用于列表展示，经 normalize 后的形态）
// ========================

/** 调拨单（前端展示） */
export type TransferOrderView = Omit<TransferOrderDTO, 'transferId' | 'fromWarehouseName' | 'toWarehouseName'> & {
  transferOrderId: number
  outWarehouseName: string
  inWarehouseName: string
}

/** 组装拆卸单（前端展示） */
export type AssemblyOrderView = Omit<AssemblyOrderDTO, 'assemblyId' | 'type'> & {
  assemblyOrderId: number
  assemblyType: string // '组装' | '拆卸'
}

/** 出入库单（前端展示） */
export type StockInOutView = Omit<StockInOutDTO, 'inOutId' | 'billNo' | 'billDate'> & {
  stockId: number
  stockNo: string
  stockType: string
  stockDate: string
  stockDirection: number
}

/** 库存预警（前端展示） */
export type InventoryAlertView = Omit<InventoryAlertDTO, 'skuName' | 'currentQty' | 'minQty' | 'maxQty'> & {
  productName: string
  currentStock: number
  minStock: number
  maxStock: number
}
