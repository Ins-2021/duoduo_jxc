/** 系统设置 */
export interface SystemSettingsDTO {
  enableSizeColor?: boolean
  systemName?: string
  companyName?: string
  companyPhone?: string
  companyAddress?: string
  creatorSelectable?: string
  autoAudit?: string
  enableBatch?: string
  enableShelfLife?: string
  enableBarcode?: string
  enableInventory?: string
  salesCarryAmount?: string
  purchaseCarryAmount?: string
  forbidZeroStockOut?: string
  hideApprover?: string
  allowEditPrice?: string
  billingSalesman?: string
  warnLowPrice?: string
  salesTaxRate?: string
  enableSalesTax?: boolean
  salesIncludeTax?: boolean
  purchaseTaxRate?: string
  enablePurchaseTax?: boolean
  purchaseIncludeTax?: boolean
  priceDecimals?: string
  qtyDecimals?: string
  pieceRound?: string
  cloudDriveUrl?: string
  printerSelectable?: string
  billingShowImage?: string
  billingScanAdd?: string
  inventoryWarningDefault?: number
  enableQuickProductDropdown?: string
  allowDuplicateBarcode?: string
  allowDuplicateNameSpec?: string
  allowNewProductOnBilling?: string
  defaultPaper?: string
  reportBatchPrint?: string
  continuousPrint?: string
  printClothesWholesaleLayout?: string
  printTextileStyleLayout?: string
  remotePrintAccount?: string
  remoteCashAccount?: string
  multiDeliveryCalc?: string
  timezone?: string
  retailPermission?: string
  pointsRatio?: string
  enablePoints?: boolean
  autoPrintReceipt?: string
  defaultCustomer?: string
  defaultAccount?: string
  chartDays?: string
  noticeInfo?: string
}

/** 单号规则 */
export interface DocNoRuleDTO {
  ruleId?: number
  docType?: string
  docName?: string
  prefix?: string
  template?: string
  seqLength?: number
  includeYear?: number
  includeMonth?: number
  includeDay?: number
  useRandom?: number
  randomLength?: number
  status?: number
  remark?: string
  createTime?: string
  updateTime?: string
}

/** UI 视图配置 */
export interface UiViewConfigDTO {
  viewKey: string
  viewName: string
  scene: string
  version: number
  columns: any
}

/** UI 视图摘要 */
export interface UiViewSummaryDTO {
  viewKey: string
  viewName: string
  scene: string
  version: number
  updateTime: string
}

/** 操作日志 */
export interface OperLogDTO {
  logId: number
  storeName: string
  createTime: string
  content: string
  operatorName: string
}
