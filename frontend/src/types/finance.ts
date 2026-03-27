/** 应收账款（后端字段） */
export interface ReceivableDTO {
  receivableId?: number
  billNo?: string
  customerId?: number
  customerName?: string
  amount?: number
  receivedAmount?: number
  remainingAmount?: number
  status?: number // 0:待收款, 1:部分收款, 2:已收款
  billDate?: string
  dueDate?: string
  remark?: string
  createdBy?: number
  createTime?: string
  updateTime?: string
}

/** 应付账款（后端字段） */
export interface PayableDTO {
  payableId?: number
  billNo?: string
  supplierId?: number
  supplierName?: string
  amount?: number
  paidAmount?: number
  remainingAmount?: number
  status?: number // 0:待付款, 1:部分付款, 2:已付款
  billDate?: string
  dueDate?: string
  remark?: string
  createdBy?: number
  createTime?: string
  updateTime?: string
}

/** 收款单（后端字段） */
export interface ReceiptDTO {
  receiptId?: number
  receiptNo?: string
  customerId?: number
  customerName?: string
  accountId?: number
  accountName?: string
  amount?: number
  payMethod?: string
  status?: number // 0:待确认, 1:已完成
  receiptDate?: string
  remark?: string
  createdBy?: number
  createTime?: string
}

/** 付款单（后端字段） */
export interface PaymentDTO {
  paymentId?: number
  paymentNo?: string
  supplierId?: number
  supplierName?: string
  accountId?: number
  accountName?: string
  amount?: number
  payMethod?: string
  status?: number // 0:待确认, 1:已完成
  paymentDate?: string
  remark?: string
  createdBy?: number
  createTime?: string
}

/** 资金流水（后端字段） */
export interface FinanceTransactionDTO {
  transactionId?: number
  transactionNo?: string
  accountId?: number
  accountName?: string
  type?: number // 1:收入, 2:支出, 3:转账
  amount?: number
  balance?: number
  category?: string
  billType?: string
  billNo?: string
  remark?: string
  transactionDate?: string
  createTime?: string
}

/** 核销单 */
export interface WriteOffDTO {
  writeOffId?: number
  writeOffNo?: string
  type?: string // 'receivable' | 'payable'
  billNo?: string
  billId?: number
  receiptPaymentId?: number
  amount?: number
  remark?: string
  createdBy?: number
  createTime?: string
}

/** 收支记录（后端字段） */
export interface IncomeExpenseDTO {
  ieId?: number
  ieNo?: string
  type?: number // 1:收入, 2:支出
  categoryId?: number
  categoryName?: string
  accountId?: number
  accountName?: string
  amount?: number
  billType?: string
  billNo?: string
  status?: number // 0:待审核, 1:已审核
  billDate?: string
  remark?: string
  createdBy?: number
  createTime?: string
}

/** 财务账户 */
export interface FinanceAccountDTO {
  accountId: number
  accountName: string
  balance: number
  status: number
}

// ========================
// 前端 View Model 类型（经 normalize 后的形态）
// ========================

/** 应收账款（前端展示） */
export type ReceivableView = Omit<ReceivableDTO, 'billNo' | 'billType' | 'amount'> & {
  documentNo: string
  documentType: string
  receivableAmount: number
}

/** 应付账款（前端展示） */
export type PayableView = Omit<PayableDTO, 'billNo' | 'billType' | 'amount'> & {
  documentNo: string
  documentType: string
  payableAmount: number
}

/** 收款单（前端展示） */
export type ReceiptView = Omit<ReceiptDTO, 'amount' | 'payMethod' | 'accountName'> & {
  receiptAmount: number
  paymentMethod: string
  bankAccount: string
}

/** 付款单（前端展示） */
export type PaymentView = Omit<PaymentDTO, 'amount' | 'payMethod' | 'accountName'> & {
  paymentAmount: number
  paymentMethod: string
  bankAccount: string
}

/** 资金流水（前端展示） */
export type TransactionView = Omit<FinanceTransactionDTO, 'type' | 'billType' | 'billNo' | 'amount' | 'category'> & {
  transactionType: string // '收入' | '支出' | '转账'
  businessType: string
  businessNo: string
  incomeAmount: number
  expenseAmount: number
  paymentMethod: string
}

/** 核销单（前端展示） */
export type WriteOffView = Omit<WriteOffDTO, 'type'> & {
  writeOffType: string // '应收核销' | '应付核销'
  partnerName: string
  writeOffDate: string
  writeOffAmount: number
  discountAmount: number
  paymentMethod: string
}

/** 收支记录（前端展示） */
export type IncomeExpenseView = Omit<IncomeExpenseDTO, 'ieId' | 'type' | 'billDate' | 'categoryName' | 'accountName'> & {
  expenseId: number
  expenseType: string // '收入' | '支出'
  expenseDate: string
  expenseCategory: string
  paymentMethod: string
}
