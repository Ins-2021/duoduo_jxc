import type { PageQuery } from './common'

/** 客户 */
export interface CustomerDTO {
  customerId: number
  customerName: string
  contactName: string
  contactPhone: string
  address: string
  level: string
  status: number
}

/** 客户查询 */
export interface CustomerQuery extends PageQuery {
  keyword?: string
  status?: number
}

/** 供应商 */
export interface SupplierDTO {
  supplierId: number
  supplierName: string
  status: number
}

/** 供应商查询 */
export interface SupplierQuery extends PageQuery {
  keyword?: string
  status?: number
}

/** 仓库 */
export interface WarehouseDTO {
  warehouseId: number
  warehouseName: string
  status: number
}

/** 仓库查询 */
export interface WarehouseQuery extends PageQuery {
  keyword?: string
  status?: number
}
