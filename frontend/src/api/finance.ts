import request from '@/utils/request'
import type {
  Result,
  ReceivableDTO, PayableDTO, ReceiptDTO, PaymentDTO,
  FinanceTransactionDTO, WriteOffDTO, IncomeExpenseDTO, FinanceAccountDTO,
  ReceivableView, PayableView, ReceiptView, PaymentView,
  TransactionView, WriteOffView, IncomeExpenseView,
  PageQuery,
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

const normalizeReceivable = (item: ReceivableDTO): ReceivableView => ({
  ...item,
  documentNo: item.billNo || '',
  documentType: '',
  receivableAmount: Number(item.amount || 0)
})

const normalizePayable = (item: PayableDTO): PayableView => ({
  ...item,
  documentNo: item.billNo || '',
  documentType: '',
  payableAmount: Number(item.amount || 0)
})

const normalizeReceipt = (item: ReceiptDTO): ReceiptView => ({
  ...item,
  receiptAmount: Number(item.amount || 0),
  paymentMethod: item.payMethod || '',
  bankAccount: item.accountName || ''
})

const normalizePayment = (item: PaymentDTO): PaymentView => ({
  ...item,
  paymentAmount: Number(item.amount || 0),
  paymentMethod: item.payMethod || '',
  bankAccount: item.accountName || ''
})

const normalizeTransaction = (item: FinanceTransactionDTO): TransactionView => ({
  ...item,
  transactionType: item.type === 1 ? '收入' : item.type === 2 ? '支出' : '转账',
  businessType: item.billType || '',
  businessNo: item.billNo || '',
  incomeAmount: item.type === 1 ? Number(item.amount || 0) : 0,
  expenseAmount: item.type === 2 ? Number(item.amount || 0) : 0,
  paymentMethod: item.category || ''
})

const normalizeWriteOff = (item: WriteOffDTO): WriteOffView => ({
  ...item,
  writeOffType: item.type === 'receivable' ? '应收核销' : '应付核销',
  partnerName: item.billNo || '',
  writeOffDate: item.createTime || '',
  writeOffAmount: Number(item.amount || 0),
  discountAmount: 0,
  paymentMethod: ''
})

const normalizeIncomeExpense = (item: IncomeExpenseDTO): IncomeExpenseView => ({
  ...item,
  expenseId: item.ieId!,
  expenseType: item.type === 1 ? '收入' : '支出',
  expenseDate: item.billDate || '',
  expenseCategory: item.categoryName || '',
  paymentMethod: item.accountName || ''
})

const mapReceivablePayload = (data: ReceivableView) => ({
  ...data,
  billNo: data.documentNo,
  billType: data.documentType,
  amount: data.receivableAmount,
  dueDate: data.dueDate
})

const mapPayablePayload = (data: PayableView) => ({
  ...data,
  billNo: data.documentNo,
  billType: data.documentType,
  amount: data.payableAmount,
  dueDate: data.dueDate
})

const mapReceiptPayload = (data: ReceiptView) => ({
  ...data,
  amount: data.receiptAmount,
  payMethod: data.paymentMethod,
  accountName: data.bankAccount
})

const mapPaymentPayload = (data: PaymentView) => ({
  ...data,
  amount: data.paymentAmount,
  payMethod: data.paymentMethod,
  accountName: data.bankAccount
})

const mapIncomeExpensePayload = (data: IncomeExpenseView) => ({
  ...data,
  ieId: data.expenseId,
  type: data.expenseType === '收入' ? 1 : 2,
  categoryName: data.expenseCategory,
  accountName: data.paymentMethod,
  billDate: data.expenseDate
})

export const receivableApi = {
  pageList: (data: PageParam & { documentNo?: string; customerName?: string; status?: number }) => {
    return mapPagedResult(request({
      url: '/finance/receivable/page',
      method: 'post',
      data: buildPageQuery(data, data?.documentNo || data?.customerName, { status: data?.status })
    }), normalizeReceivable)
  },
  getById: (id: number) => {
    return mapSingleResult<ReceivableDTO, ReceivableView>(request({
      url: `/finance/receivable/${id}`,
      method: 'get'
    }), normalizeReceivable)
  },
  create: (data: ReceivableView) => {
    return request({
      url: '/finance/receivable',
      method: 'post',
      data: mapReceivablePayload(data)
    })
  },
  update: (data: ReceivableView) => {
    return request({
      url: `/finance/receivable/${data.receivableId}`,
      method: 'put',
      data: mapReceivablePayload(data)
    })
  },
  delete: (id: number) => {
    return request({
      url: `/finance/receivable/${id}`,
      method: 'delete'
    })
  },
  writeOff: (id: number, amount: number) => {
    return request({
      url: `/finance/receivable/${id}/write-off`,
      method: 'post',
      params: { amount }
    })
  }
}

export const payableApi = {
  pageList: (data: PageParam & { documentNo?: string; supplierName?: string; status?: number }) => {
    return mapPagedResult(request({
      url: '/finance/payable/page',
      method: 'post',
      data: buildPageQuery(data, data?.documentNo || data?.supplierName, { status: data?.status })
    }), normalizePayable)
  },
  getById: (id: number) => {
    return mapSingleResult<PayableDTO, PayableView>(request({
      url: `/finance/payable/${id}`,
      method: 'get'
    }), normalizePayable)
  },
  create: (data: PayableView) => {
    return request({
      url: '/finance/payable',
      method: 'post',
      data: mapPayablePayload(data)
    })
  },
  update: (data: PayableView) => {
    return request({
      url: `/finance/payable/${data.payableId}`,
      method: 'put',
      data: mapPayablePayload(data)
    })
  },
  delete: (id: number) => {
    return request({
      url: `/finance/payable/${id}`,
      method: 'delete'
    })
  },
  writeOff: (id: number, amount: number) => {
    return request({
      url: `/finance/payable/${id}/write-off`,
      method: 'post',
      params: { amount }
    })
  }
}

export const receiptApi = {
  pageList: (data: PageParam & { receiptNo?: string; customerName?: string; status?: number }) => {
    return mapPagedResult(request({
      url: '/finance/receipt/page',
      method: 'post',
      data: buildPageQuery(data, data?.receiptNo || data?.customerName, { status: data?.status })
    }), normalizeReceipt)
  },
  getById: (id: number) => {
    return mapSingleResult<ReceiptDTO, ReceiptView>(request({
      url: `/finance/receipt/${id}`,
      method: 'get'
    }), normalizeReceipt)
  },
  create: (data: ReceiptView) => {
    return request({
      url: '/finance/receipt',
      method: 'post',
      data: mapReceiptPayload(data)
    })
  },
  update: (data: ReceiptView) => {
    return request({
      url: `/finance/receipt/${data.receiptId}`,
      method: 'put',
      data: mapReceiptPayload(data)
    })
  },
  delete: (id: number) => {
    return request({
      url: `/finance/receipt/${id}`,
      method: 'delete'
    })
  },
  approve: (id: number) => {
    return request({
      url: `/finance/receipt/${id}/complete`,
      method: 'post'
    })
  }
}

export const paymentApi = {
  pageList: (data: PageParam & { paymentNo?: string; supplierName?: string; status?: number }) => {
    return mapPagedResult(request({
      url: '/finance/payment/page',
      method: 'post',
      data: buildPageQuery(data, data?.paymentNo || data?.supplierName, { status: data?.status })
    }), normalizePayment)
  },
  getById: (id: number) => {
    return mapSingleResult<PaymentDTO, PaymentView>(request({
      url: `/finance/payment/${id}`,
      method: 'get'
    }), normalizePayment)
  },
  create: (data: PaymentView) => {
    return request({
      url: '/finance/payment',
      method: 'post',
      data: mapPaymentPayload(data)
    })
  },
  update: (data: PaymentView) => {
    return request({
      url: `/finance/payment/${data.paymentId}`,
      method: 'put',
      data: mapPaymentPayload(data)
    })
  },
  delete: (id: number) => {
    return request({
      url: `/finance/payment/${id}`,
      method: 'delete'
    })
  },
  approve: (id: number) => {
    return request({
      url: `/finance/payment/${id}/complete`,
      method: 'post'
    })
  }
}

export const transactionApi = {
  pageList: (data: PageParam & { businessNo?: string; transactionType?: string; accountId?: number }) => {
    return mapPagedResult(request({
      url: '/finance/transaction/page',
      method: 'post',
      data: buildPageQuery(data, data?.businessNo, { type: data?.transactionType, accountId: data?.accountId })
    }), normalizeTransaction)
  },
  getById: (id: number) => {
    return mapSingleResult<FinanceTransactionDTO, TransactionView>(request({
      url: `/finance/transaction/${id}`,
      method: 'get'
    }), normalizeTransaction)
  },
  create: (_data: never) => Promise.reject(new Error('资金流水暂不支持前端新增')),
  update: (_data: never) => Promise.reject(new Error('资金流水暂不支持前端修改')),
  delete: (_id: number) => Promise.reject(new Error('资金流水暂不支持前端删除'))
}

export const writeOffApi = {
  pageList: (data: PageParam & { writeOffNo?: string; partnerName?: string; writeOffType?: string }) => {
    return mapPagedResult(request({
      url: '/finance/write-off/page',
      method: 'post',
      data: buildPageQuery(data, data?.writeOffNo || data?.partnerName, {
        type: data?.writeOffType === '应收核销' ? 'receivable' : data?.writeOffType === '应付核销' ? 'payable' : undefined
      })
    }), normalizeWriteOff)
  },
  getById: (id: number) => {
    return mapSingleResult<WriteOffDTO, WriteOffView>(request({
      url: `/finance/write-off/${id}`,
      method: 'get'
    }), normalizeWriteOff)
  },
  create: (data: WriteOffView) => {
    return request({
      url: '/finance/write-off',
      method: 'post',
      data: {
        ...data,
        type: data.writeOffType === '应收核销' ? 'receivable' : 'payable',
        amount: data.writeOffAmount,
        billNo: data.partnerName
      }
    })
  },
  update: (_data: never) => Promise.reject(new Error('核销单暂不支持前端修改')),
  delete: (id: number) => {
    return request({
      url: `/finance/write-off/${id}`,
      method: 'delete'
    })
  }
}

export const incomeExpenseApi = {
  pageList: (data: PageParam & { remark?: string; expenseType?: string; status?: number }) => {
    return mapPagedResult(request({
      url: '/finance/income-expense/page',
      method: 'post',
      data: buildPageQuery(data, data?.remark, {
        type: data?.expenseType === '收入' ? 1 : data?.expenseType === '支出' ? 2 : undefined,
        status: data?.status
      })
    }), normalizeIncomeExpense)
  },
  getById: (id: number) => {
    return mapSingleResult<IncomeExpenseDTO, IncomeExpenseView>(request({
      url: `/finance/income-expense/${id}`,
      method: 'get'
    }), normalizeIncomeExpense)
  },
  create: (data: IncomeExpenseView) => {
    return request({
      url: '/finance/income-expense',
      method: 'post',
      data: mapIncomeExpensePayload(data)
    })
  },
  update: (data: IncomeExpenseView) => {
    return request({
      url: `/finance/income-expense/${data.expenseId || (data as IncomeExpenseView & { ieId?: number }).ieId}`,
      method: 'put',
      data: mapIncomeExpensePayload(data)
    })
  },
  delete: (id: number) => {
    return request({
      url: `/finance/income-expense/${id}`,
      method: 'delete'
    })
  }
}

export function getAccountList(params: PageQuery & { accountName?: string }) {
  return request({
    url: '/finance/account/page',
    method: 'get',
    params
  })
}

export function addAccount(data: Omit<FinanceAccountDTO, 'accountId' | 'balance'>) {
  return request({
    url: '/finance/account',
    method: 'post',
    data
  })
}
