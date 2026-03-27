/** 商品 SPU */
export interface ProductSpuDTO {
  spuId?: number
  spuName: string
  productCode?: string
  spec?: string
  imageUrls?: string
  categoryId?: number
  categoryName?: string
  unit?: string
  brandId?: number
  brandName?: string
  purchasePrice?: number
  retailPrice?: number
  wholesalePrice?: number
  exchangePoints?: number
  defaultWarehouseId?: number
  productLocation?: string
  warningQty?: number
  remark?: string
  status?: number
  initialStockQty?: number
  currentStock?: number
  initialCostPrice?: number
  initialTotalAmount?: number
  attr1Name?: string
  attr2Name?: string
  skuList?: ProductSkuDTO[]
}

/** 商品 SKU */
export interface ProductSkuDTO {
  skuId?: number
  spuId?: number
  skuCode: string
  attr1?: string
  attr2?: string
  purchasePrice?: number
  retailPrice?: number
  wholesalePrice?: number
  weight?: number
  warningQty?: number
  status?: number
}

/** 商品 SKU 选择项 */
export interface ProductSkuSelectDTO {
  id: number
  spuId: number
  name: string
  attributes?: string
  spec?: string
  brand?: string
  unit?: string
  wholesalePrice?: number
  purchasePrice?: number
  retailPrice?: number
  stock?: number
  category?: string
  barcode?: string
  productCode?: string
  defaultWarehouse?: string
  location?: string
  exchangePoints?: number
  stockUnit?: string
  stockValue?: number
  remark?: string
}

/** 商品分类 */
export interface ProductCategoryDTO {
  id?: number
  parentId?: number
  name: string
  sort: number
}

/** 商品分类树节点 */
export interface ProductCategoryTreeNode {
  id: number
  parentId: number
  label: string
  sort: number
  children: ProductCategoryTreeNode[]
}

/** 商品属性 */
export interface ProductAttributeDTO {
  id?: number
  name?: string
  sort?: number
  options?: ProductAttributeValueDTO[]
}

/** 商品属性值 */
export interface ProductAttributeValueDTO {
  id?: number
  attributeId?: number
  value?: string
  sort?: number
}

/** 首页概览 */
export interface HomeOverviewDTO {
  todayGrossProfit: number
  yesterdayGrossProfit: number
  todaySalesAmount: number
  yesterdaySalesAmount: number
  todaySalesCount: number
  yesterdaySalesCount: number
  todayCashIn: number
  yesterdayCashIn: number
  customerCount: number
  supplierCount: number
  inventoryQtyTotal: number
  inventoryAmountTotal: number
  inventoryWarnCount: number
  shelfLifeWarnCount: number
  receivableAmount: number
  payableAmount: number
  assetsTotal: number
  overviewDates: string[]
  overviewSalesOrderCount: number[]
  cashflowDates: string[]
  cashflowIncome: number[]
  cashflowExpense: number[]
}

/** 打印模板 */
export interface PrintTemplateDTO {
  templateId: number
  templateName: string
  bizType: string
  paperType: string
  paperWidthMm: number
  paperHeightMm: number
  designJson?: string
  isDefault: number
  enabled: number
}

/** 打印数据 */
export interface PrintDataDTO {
  billNo: string
  billDate: string
  storeName: string
  customerName: string
  operator: string
  remark: string
  totalAmount: number
  items: PrintDataItemDTO[]
}

/** 打印数据明细 */
export interface PrintDataItemDTO {
  name: string
  qty: number
  price: number
  amount: number
}

/** 打印模板设置 */
export interface PrintTemplateSettingsDTO {
  printNoDialog: boolean
}
