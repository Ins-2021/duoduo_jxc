// 通用类型
export type { Result, PageResult, PageQuery, OptionDTO, DictItemDTO, FileUploadResponse } from './common'

// 认证/权限
export type {
  LoginRequest, RefreshTokenRequest, TokenResponse,
  ChangePasswordRequest, ResetPasswordRequest,
  CurrentUserResponse, MenuTreeNode, MenuSaveRequest,
  RoleCreateRequest, RoleUpdateRequest,
  UserCreateRequest, UserUpdateRequest, OnlineUserDTO,
} from './auth'

// 基础数据
export type {
  CustomerDTO, CustomerQuery,
  SupplierDTO, SupplierQuery,
  WarehouseDTO, WarehouseQuery,
} from './data'

// 商品/打印
export type {
  ProductSpuDTO, ProductSkuDTO, ProductSkuSelectDTO, ProductSkuSelectQuery,
  ProductCategoryDTO, ProductCategoryTreeNode,
  ProductAttributeDTO, ProductAttributeValueDTO,
  HomeOverviewDTO, PrintTemplateDTO, PrintDataDTO, PrintDataItemDTO,
  PrintTemplateSettingsDTO,
} from './product'

// 销售/采购
export type {
  SalesOrderDTO, SalesOrderDetailDTO,
  SalesReturnOrderDTO, SalesReturnDetailDTO,
  SalesReturnSourceOrderDTO, SalesReturnSourceDetailDTO,
  PurchaseOrderDTO, PurchaseOrderDetailDTO,
  SalesDailyReportDTO,
} from './sales'

// 报表/分析
export type {
  ProfitSummary, StyleProfit, CustomerProfit,
  ColorProfit, SizeProfit, MonthlyProfit,
  StyleProfitDetail, ProfitTrend,
} from './report'

// 库存
export type {
  InventoryDTO, InventoryAlertDTO,
  TransferOrderDTO, TransferOrderDetailDTO,
  InventoryCheckDTO, InventoryCheckDetailDTO,
  AssemblyOrderDTO, AssemblyOrderDetailDTO,
  StockInOutDTO, StockInOutDetailDTO,
  TransferOrderView, AssemblyOrderView,
  StockInOutView, InventoryAlertView,
} from './inventory'

// 财务
export type {
  ReceivableDTO, PayableDTO,
  ReceiptDTO, PaymentDTO,
  FinanceTransactionDTO, WriteOffDTO, IncomeExpenseDTO,
  FinanceAccountDTO,
  ReceivableView, PayableView,
  ReceiptView, PaymentView,
  TransactionView, WriteOffView, IncomeExpenseView,
} from './finance'

// 系统
export type {
  SystemSettingsDTO, DocNoRuleDTO,
  UiViewConfigDTO, UiViewSummaryDTO, OperLogDTO,
} from './system'

// 工作流
export type {
  WfModelCreateRequest, WfModelSaveRequest, WfModelPublishRequest,
  WfBindingSaveRequest, WfInstanceStartRequest,
  WfDiagramStateResponse, WfTaskApproveRequest, WfTaskRejectRequest,
} from './workflow'
