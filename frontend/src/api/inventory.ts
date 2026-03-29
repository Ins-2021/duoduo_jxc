import request from '@/utils/request'
import type {
  Result,
  PageQuery,
  TransferOrderDTO, InventoryCheckDTO, AssemblyOrderDTO, StockInOutDTO, InventoryAlertDTO,
  TransferOrderView, AssemblyOrderView, StockInOutView, InventoryAlertView,
} from '@/types'

type PageParam = { pageNum?: number; pageSize?: number }

type PagedResult<T = unknown> = { total: number; list: T[] }

const buildPageQuery = (data: PageParam, keyword?: string, extraParams?: Record<string, unknown>) => ({
  pageNum: data?.pageNum ?? 1,
  pageSize: data?.pageSize ?? 10,
  params: {
    ...(keyword ? { keyword } : {}),
    ...(extraParams || {})
  }
})

const mapPagedResult = <S, T>(promise: Promise<Result<PagedResult<S>>>, mapper: (item: S) => T): Promise<Result<PagedResult<T>>> =>
  promise.then((res) => ({
    ...res,
    data: {
      ...res.data,
      list: (res.data?.list || []).map(mapper)
    }
  }))

const mapSingleResult = <S, T>(promise: Promise<Result<S>>, mapper: (item: S) => T): Promise<Result<T>> =>
  promise.then((res) => ({
    ...res,
    data: mapper(res.data)
  }))

const normalizeTransfer = (item: TransferOrderDTO): TransferOrderView => ({
  ...item,
  transferOrderId: item.transferId!,
  outWarehouseName: item.fromWarehouseName!,
  inWarehouseName: item.toWarehouseName!
})

const normalizeAssembly = (item: AssemblyOrderDTO): AssemblyOrderView => ({
  ...item,
  assemblyOrderId: item.assemblyId!,
  assemblyType: item.type === 2 ? '拆卸' : '组装'
})

const normalizeStock = (item: StockInOutDTO): StockInOutView => ({
  ...item,
  stockId: item.inOutId!,
  stockNo: item.billNo!,
  stockType: item.type === 2 ? '其他出库' : '其他入库',
  stockDate: item.billDate!,
  stockDirection: item.type === 2 ? 2 : 1
})

const normalizeAlert = (item: InventoryAlertDTO): InventoryAlertView => ({
  ...item,
  productName: item.skuName,
  currentStock: item.currentQty,
  minStock: item.minQty,
  maxStock: item.maxQty
})

const mapTransferPayload = (data: TransferOrderView) => ({
  ...data,
  transferId: data.transferOrderId,
  fromWarehouseName: data.outWarehouseName,
  toWarehouseName: data.inWarehouseName
})

const mapAssemblyPayload = (data: AssemblyOrderView) => ({
  ...data,
  assemblyId: data.assemblyOrderId,
  type: data.assemblyType === '拆卸' ? 2 : 1
})

const mapStockPayload = (data: StockInOutView) => ({
  ...data,
  inOutId: data.stockId,
  billNo: data.stockNo,
  type: data.stockDirection === 2 ? 2 : 1,
  billDate: data.stockDate
})

export const getInventoryList = (params: PageQuery & { warehouseId?: number; skuId?: number }) => {
  return request({
    url: '/inventory/page',
    method: 'get',
    params
  })
}

// 调拨单
export const transferOrderApi = {
  pageList: (data: PageParam & { transferNo?: string; status?: number }) => mapPagedResult(request({
    url: '/inventory/transfer/page',
    method: 'post',
    data: buildPageQuery(data, data?.transferNo, { status: data?.status })
  }), normalizeTransfer),
  getById: (id: number) => mapSingleResult<TransferOrderDTO, TransferOrderView>(request({ url: `/inventory/transfer/${id}`, method: 'get' }), normalizeTransfer),
  create: (data: TransferOrderView) => request({ url: '/inventory/transfer', method: 'post', data: mapTransferPayload(data) }),
  update: (data: TransferOrderView) => request({ url: `/inventory/transfer/${data.transferOrderId ?? (data as TransferOrderView & { transferId?: number }).transferId}`, method: 'put', data: mapTransferPayload(data) }),
  delete: (id: number) => request({ url: `/inventory/transfer/${id}`, method: 'delete' }),
  approve: (id: number) => request({ url: `/inventory/transfer/${id}/approve`, method: 'post' }),
  complete: (_id: number) => Promise.reject(new Error('调拨单暂不支持完成操作'))
}

// 盘点单
export const inventoryCheckApi = {
  pageList: (data: PageParam & { checkNo?: string; status?: number }) => mapPagedResult(request({
    url: '/inventory/check/page',
    method: 'post',
    data: buildPageQuery(data, data?.checkNo, { status: data?.status })
  }), (item: InventoryCheckDTO) => item),
  getById: (id: number) => request({ url: `/inventory/check/${id}`, method: 'get' }),
  create: (data: InventoryCheckDTO) => request({ url: '/inventory/check', method: 'post', data }),
  update: (data: InventoryCheckDTO) => request({ url: `/inventory/check/${data.checkId}`, method: 'put', data }),
  delete: (id: number) => request({ url: `/inventory/check/${id}`, method: 'delete' }),
  complete: (id: number) => request({ url: `/inventory/check/${id}/complete`, method: 'post' })
}

// 组装拆卸单
export const assemblyOrderApi = {
  pageList: (data: PageParam & { assemblyNo?: string; assemblyType?: string; status?: number }) => mapPagedResult(request({
    url: '/inventory/assembly/page',
    method: 'post',
    data: buildPageQuery(data, data?.assemblyNo, {
      type: data?.assemblyType === '拆卸' ? 2 : data?.assemblyType === '组装' ? 1 : undefined,
      status: data?.status
    })
  }), normalizeAssembly),
  getById: (id: number) => mapSingleResult<AssemblyOrderDTO, AssemblyOrderView>(request({ url: `/inventory/assembly/${id}`, method: 'get' }), normalizeAssembly),
  create: (data: AssemblyOrderView) => request({ url: '/inventory/assembly', method: 'post', data: mapAssemblyPayload(data) }),
  update: (data: AssemblyOrderView) => request({ url: `/inventory/assembly/${data.assemblyOrderId ?? (data as AssemblyOrderView & { assemblyId?: number }).assemblyId}`, method: 'put', data: mapAssemblyPayload(data) }),
  delete: (id: number) => request({ url: `/inventory/assembly/${id}`, method: 'delete' }),
  approve: (id: number) => request({ url: `/inventory/assembly/${id}/approve`, method: 'post' })
}

// 出入库单
export const stockInOutApi = {
  pageList: (data: PageParam & { stockNo?: string; stockDirection?: number; status?: number }) => mapPagedResult(request({
    url: '/inventory/stock-in-out/page',
    method: 'post',
    data: buildPageQuery(data, data?.stockNo, {
      type: data?.stockDirection === 2 ? 2 : data?.stockDirection === 1 ? 1 : undefined,
      status: data?.status
    })
  }), normalizeStock),
  getById: (id: number) => mapSingleResult<StockInOutDTO, StockInOutView>(request({ url: `/inventory/stock-in-out/${id}`, method: 'get' }), normalizeStock),
  create: (data: StockInOutView) => request({ url: '/inventory/stock-in-out', method: 'post', data: mapStockPayload(data) }),
  update: (data: StockInOutView) => request({ url: `/inventory/stock-in-out/${data.stockId || (data as StockInOutView & { inOutId?: number }).inOutId}`, method: 'put', data: mapStockPayload(data) }),
  delete: (id: number) => request({ url: `/inventory/stock-in-out/${id}`, method: 'delete' }),
  approve: (id: number) => request({ url: `/inventory/stock-in-out/${id}/approve`, method: 'post' })
}

// 库存预警
export const inventoryAlertApi = {
  pageList: (data: PageParam & { productName?: string; warehouseName?: string; alertType?: number; status?: number }) => mapPagedResult(request({
    url: '/inventory/alert/page',
    method: 'post',
    data: buildPageQuery(data, data?.productName || data?.warehouseName, {
      alertType: data?.alertType,
      status: data?.status
    })
  }), normalizeAlert),
  getById: (_id: number) => Promise.reject(new Error('库存预警不支持详情查询')),
  create: (_data: never) => Promise.reject(new Error('库存预警不支持新增')),
  update: (_data: never) => Promise.reject(new Error('库存预警不支持修改')),
  delete: (_id: number) => Promise.reject(new Error('库存预警不支持删除')),
  process: (id: number) => request({ url: `/inventory/alert/${id}/handle`, method: 'post' })
}
