import Request from 'luch-request'

// 生产环境必须设置为 false，确保使用真实 API
const MOCK = import.meta.env.VITE_USE_MOCK === 'true' || false

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

// 只在开发环境且明确启用 MOCK 时才使用模拟数据
const mockData: Record<string, any> = {
  'GET /auth/user/info': {
    code: 0, data: { id: 1, name: '张三', code: 'W001', departmentName: '缝制一车间', roles: ['worker'], avatar: '' }
  },
  'GET /worker/piecework/today': {
    code: 0, data: [
      { id: 1, bundleNo: 'BP20260329001', processName: '上袖', quantity: 25, amount: 12.50, recordDate: '2026-03-29' },
      { id: 2, bundleNo: 'BP20260329002', processName: '上领', quantity: 30, amount: 18.00, recordDate: '2026-03-29' },
      { id: 3, bundleNo: 'BP20260329003', processName: '合侧缝', quantity: 20, amount: 10.00, recordDate: '2026-03-29' }
    ]
  },
  'POST /worker/piecework': { code: 0, msg: '提交成功', data: null },
  'GET /worker/bundle/info': {
    code: 0, data: {
      bundleNo: 'BP20260329001', styleCode: 'DD-A001', styleName: '短袖T恤', color: '黑色', size: 'M', quantity: 25,
      processes: [
        { id: 1, name: '上袖', price: 0.50, categoryName: '缝制' },
        { id: 2, name: '上领', price: 0.60, categoryName: '缝制' },
        { id: 3, name: '合侧缝', price: 0.50, categoryName: '缝制' },
        { id: 4, name: '底摆', price: 0.30, categoryName: '缝制' }
      ]
    }
  },
  'GET /worker/task/list': {
    code: 0, data: { list: [
      { id: 1, bundleNo: 'BP20260329004', styleCode: 'DD-A002', styleName: 'POLO衫', processName: '上袖', quantity: 50, status: 'pending', deadline: '2026-03-30' },
      { id: 2, bundleNo: 'BP20260329005', styleCode: 'DD-A001', styleName: '短袖T恤', processName: '合侧缝', quantity: 30, status: 'doing', deadline: '2026-03-29' },
      { id: 3, bundleNo: 'BP20260328001', styleCode: 'DD-B001', styleName: '卫衣', processName: '上袖', quantity: 40, status: 'done', deadline: '2026-03-28' }
    ], total: 3 }
  },
  'GET /worker/wage/summary': {
    code: 0, data: { month: '2026-03', totalCount: 156, totalQuantity: 3850, totalAmount: 1925.00, deductionAmount: 120.00, finalAmount: 1805.00 }
  },
  'GET /worker/wage/detail': {
    code: 0, data: { list: [
      { id: 1, recordDate: '2026-03-29', processName: '上袖', quantity: 25, amount: 12.50, styleCode: 'DD-A001' },
      { id: 2, recordDate: '2026-03-29', processName: '上领', quantity: 30, amount: 18.00, styleCode: 'DD-A001' },
      { id: 3, recordDate: '2026-03-28', processName: '合侧缝', quantity: 40, amount: 20.00, styleCode: 'DD-A002' },
      { id: 4, recordDate: '2026-03-28', processName: '底摆', quantity: 50, amount: 15.00, styleCode: 'DD-A002' },
      { id: 5, recordDate: '2026-03-27', processName: '上袖', quantity: 35, amount: 17.50, styleCode: 'DD-B001' }
    ], total: 5 }
  },
  'GET /cutter/task/list': {
    code: 0, data: { list: [
      { id: 1, orderNo: 'PO2026032901', styleCode: 'DD-A001', styleName: '短袖T恤', quantity: 500, status: 'pending', priority: 'high' },
      { id: 2, orderNo: 'PO2026032902', styleCode: 'DD-A002', styleName: 'POLO衫', quantity: 300, status: 'doing', priority: 'medium' }
    ], total: 2 }
  },
  'GET /cutter/order/pending': {
    code: 0, data: [
      { id: 1, orderNo: 'PO2026032901', styleCode: 'DD-A001', styleName: '短袖T恤', quantity: 500, colors: ['黑色', '白色', '蓝色'], sizes: ['S', 'M', 'L', 'XL'] },
      { id: 2, orderNo: 'PO2026032902', styleCode: 'DD-A002', styleName: 'POLO衫', quantity: 300, colors: ['白色', '红色'], sizes: ['M', 'L', 'XL'] }
    ]
  },
  'POST /cutter/bundle/generate': {
    code: 0, data: [
      { id: 1, bundleNo: 'BP20260329010', color: '黑色', size: 'M', quantity: 25 },
      { id: 2, bundleNo: 'BP20260329011', color: '黑色', size: 'M', quantity: 25 }
    ]
  },
  'GET /cutter/stats/today': { code: 0, data: { cutQuantity: 150, bundleCount: 6, pendingOrders: 3 } },
  'GET /cutter/records/today': { code: 0, data: [] },
  'GET /cutter/bundle/1': { code: 0, data: { id: 1, bundleNo: 'BP20260329010', styleCode: 'DD-A001', color: '黑色', size: 'M', quantity: 25, status: 'cutting' } },
  'POST /cutter/bundle/1/print': { code: 0, msg: '打印成功' },
  'POST /cutter/input': { code: 0, msg: '录入成功' },
  'GET /inspector/stats/today': { code: 0, data: { total: 45, passed: 40, failed: 5, passRate: '88.9' } },
  'GET /inspector/first-article/pending': { code: 0, data: [
    { id: 1, confirmationNo: 'FA20260329001', styleCode: 'DD-A001', color: '黑色', size: 'M', processName: '缝制' },
    { id: 2, confirmationNo: 'FA20260329002', styleCode: 'DD-A002', color: '白色', size: 'L', processName: '上袖' }
  ]},
  'POST /inspector/first-article/1/confirm': { code: 0, msg: '确认成功' },
  'POST /inspector/check': { code: 0, msg: '质检提交成功' },
  'GET /inspector/rework/list': { code: 0, data: { list: [
    { id: 1, defectNo: 'RW001', defectType: '线头', bundleNo: 'BP20260329001', styleCode: 'DD-A001', status: 'pending', createdAt: '2026-03-29' }
  ], total: 1 }},
  'POST /inspector/rework': { code: 0, msg: '提交成功' },
  'GET /inspector/history': { code: 0, data: { list: [
    { id: 1, bundleNo: 'BP20260328001', styleCode: 'DD-A001', result: 'passed', checkQuantity: 10, qualifiedQuantity: 10, createdAt: '2026-03-28' },
    { id: 2, bundleNo: 'BP20260328002', styleCode: 'DD-A002', result: 'rework', checkQuantity: 8, qualifiedQuantity: 6, createdAt: '2026-03-28' }
  ], total: 2 }},
  'GET /inspector/bundle/info': {
    code: 0, data: { bundleNo: 'BP20260329001', styleCode: 'DD-A001', styleName: '短袖T恤', color: '黑色', size: 'M', quantity: 25, currentProcess: '质检' }
  },
  'GET /warehouse/overview': { code: 0, data: { totalSku: 256, totalQuantity: 15800, warningCount: 12 } },
  'GET /warehouse/pending': { code: 0, data: [
    { id: 1, type: '入库', no: 'RK20260329001', createdAt: '10:30' },
    { id: 2, type: '出库', no: 'CK20260329001', createdAt: '11:00' }
  ]},
  'POST /warehouse/in': { code: 0, msg: '入库成功' },
  'POST /warehouse/out': { code: 0, msg: '出库成功' },
  'GET /warehouse/stock/list': { code: 0, data: { list: [
    { id: 1, styleCode: 'DD-A001', styleName: '短袖T恤', color: '黑色', size: 'M', quantity: 150, warning: false },
    { id: 2, styleCode: 'DD-A002', styleName: 'POLO衫', color: '白色', size: 'L', quantity: 8, warning: true }
  ], total: 2 }},
  'GET /warehouse/inventory/list': { code: 0, data: { list: [
    { id: 1, warehouseName: '成品仓A', status: 'pending', createdAt: '2026-03-29' }
  ], total: 1 }},
  'POST /warehouse/inventory': { code: 0, msg: '盘点提交成功' },
  'GET /warehouse/records/today': { code: 0, data: [] },
  'GET /leader/overview': { code: 0, data: { totalTasks: 15, pendingTasks: 3, completedTasks: 10 } },
  'GET /leader/bundle/pending': { code: 0, data: [
    { id: 1, bundleNo: 'BP20260329010', styleCode: 'DD-A001', styleName: '短袖T恤', processName: '上袖', quantity: 25 },
    { id: 2, bundleNo: 'BP20260329011', styleCode: 'DD-A002', styleName: 'POLO衫', processName: '合侧缝', quantity: 30 }
  ]},
  'GET /leader/worker/list': { code: 0, data: [
    { id: 1, name: '李四', code: 'W002' },
    { id: 2, name: '王五', code: 'W003' },
    { id: 3, name: '赵六', code: 'W004' }
  ]},
  'POST /leader/assign': { code: 0, msg: '分配成功' },
  'GET /leader/progress/list': { code: 0, data: { list: [
    { id: 1, orderNo: 'PO2026032901', styleNo: 'DD-A001', completed: 120, total: 200, percent: 60, status: 'in_progress' },
    { id: 2, orderNo: 'PO2026032902', styleNo: 'DD-A002', completed: 300, total: 300, percent: 100, status: 'completed' }
  ], total: 2 }},
  'GET /leader/exception/list': { code: 0, data: { list: [
    { id: 1, type: '质量异常', description: 'DD-A001 色差问题', status: 'pending', createdAt: '2026-03-29' }
  ], total: 1 }},
  'POST /leader/exception/handle': { code: 0, msg: '处理成功' },
  'GET /leader/approval/list': { code: 0, data: { list: [
    { id: 1, type: '加班审批', no: 'OT20260329001', createdAt: '2026-03-29' }
  ], total: 1 }},
  'POST /leader/approval': { code: 0, msg: '审批成功' },
  'GET /manager/dashboard': {
    code: 0, data: { producingOrders: 8, todayOutput: 520, weekOutput: 3200, passRate: '92.3',
      pendingOrders: [{ orderNo: 'PO2026032901', styleCode: 'DD-A001', quantity: 500, priority: 'high' }],
      producingOrdersList: [{ orderNo: 'PO2026032902', styleCode: 'DD-A002', quantity: 300, progress: 75, currentProcess: '缝制' }],
      completedOrdersList: [{ orderNo: 'PO2026032701', styleCode: 'DD-B001', quantity: 200 }]
    }
  },
  'GET /manager/order/list': { code: 0, data: { list: [
    { id: 1, orderNo: 'PO2026032901', customerName: '客户A', styleCode: 'DD-A001', quantity: 500, deliveryDate: '2026-04-05', status: '生产中' },
    { id: 2, orderNo: 'PO2026032902', customerName: '客户B', styleCode: 'DD-A002', quantity: 300, deliveryDate: '2026-04-10', status: '待开工' }
  ], total: 2 }},
  'GET /manager/order/1': { code: 0, data: { id: 1, orderNo: 'PO2026032901', customerName: '客户A', styleCode: 'DD-A001', quantity: 500, deliveryDate: '2026-04-05', status: '生产中' }},
  'GET /manager/staff/list': { code: 0, data: { list: [
    { id: 1, name: '张三', code: 'W001', department: '缝制一车间', process: '上袖', todayOutput: 85 },
    { id: 2, name: '李四', code: 'W002', department: '缝制一车间', process: '上领', todayOutput: 72 },
    { id: 3, name: '王五', code: 'W003', department: '缝制二车间', process: '合侧缝', todayOutput: 63 }
  ], total: 3 }},
  'GET /manager/report/list': { code: 0, data: [
    { id: 1, name: '生产日报', type: 'daily', date: '2026-03-29' },
    { id: 2, name: '生产周报', type: 'weekly', date: '2026-03-24' },
    { id: 3, name: '质量报表', type: 'quality', date: '2026-03-29' }
  ]},
  'GET /manager/report/1': { code: 0, data: { id: 1, name: '生产日报', content: '今日产量520件，合格率92.3%' }},
  'GET /boss/dashboard': { code: 0, data: {
    producingOrders: 8, todayOutput: 520, weekOutput: 3200, passRate: '92.3',
    productionProgress: [
      { orderNo: 'PO2026032901', customerName: '客户A', progress: 75, deliveryDate: '2026-04-05', isDelayed: false },
      { orderNo: 'PO2026032902', customerName: '客户B', progress: 30, deliveryDate: '2026-04-03', isDelayed: true }
    ]
  }},
  'GET /boss/kpi': {
    code: 0, data: { monthOutput: 12500, monthOutputGrowth: 8.5, monthRevenue: 625000, monthRevenueGrowth: 12.3, passRate: 92.3, onTimeRate: 88.5 }
  },
  'GET /boss/production/progress': {
    code: 0, data: [
      { orderNo: 'PO2026032901', customerName: '客户A', progress: 75, deliveryDate: '2026-04-05', isDelayed: false },
      { orderNo: 'PO2026032902', customerName: '客户B', progress: 30, deliveryDate: '2026-04-03', isDelayed: true },
      { orderNo: 'PO2026032703', customerName: '客户C', progress: 100, deliveryDate: '2026-03-28', isDelayed: false }
    ]
  },
  'GET /boss/alert/list': { code: 0, data: [
    { id: 1, level: 'high', title: '订单延期预警', description: 'PO2026032902 交期临近', createdAt: '2026-03-29' },
    { id: 2, level: 'medium', title: '库存预警', description: 'DD-A002 白色L尺码库存不足', createdAt: '2026-03-29' },
    { id: 3, level: 'low', title: '设备维护提醒', description: '3号缝纫机需保养', createdAt: '2026-03-28' }
  ]},
  'GET /boss/report/list': { code: 0, data: [
    { id: 1, name: '月度经营报表', type: 'monthly', date: '2026-03' },
    { id: 2, name: '产量分析', type: 'output', date: '2026-03' }
  ]},
  'GET /boss/report/1': { code: 0, data: { id: 1, name: '月度经营报表', content: '本月产量12500件，产值62.5万' }}
}

// Mock 请求处理
function matchMockKey(method: string, url: string): any {
  const cleanUrl = url.split('?')[0]
  const key = `${method.toUpperCase()} ${cleanUrl}`
  if (mockData[key]) return mockData[key]
  // 尝试前缀匹配
  for (const [k, v] of Object.entries(mockData)) {
    if (cleanUrl.startsWith(k.split(' ')[1])) return v
  }
  return null
}

const http = new Request({
  baseURL: BASE_URL,
  timeout: MOCK ? 100 : 30000,
  header: { 'Content-Type': 'application/json' }
})

// 请求拦截器
http.interceptors.request.use(
  (config: any) => {
    const token = uni.getStorageSync('token')
    if (token) {
      config.header['Authorization'] = `Bearer ${token}`
    }
    // Mock 模式：拦截请求，返回 mock 数据
    if (MOCK) {
      const mockResult = matchMockKey(config.method || 'GET', (config.baseURL || '') + (config.url || ''))
      if (mockResult) {
        return Promise.reject({ __MOCK__: true, data: mockResult })
      }
      // 未匹配的接口返回空数据
      return Promise.reject({ __MOCK__: true, data: { code: 0, data: [], msg: 'mock' } })
    }
    return config
  },
  (error: any) => {
    if (error?.__MOCK__) return Promise.reject(error)
    return Promise.reject(error)
  }
)

// 响应拦截器
http.interceptors.response.use(
  (response: any) => {
    const { data } = response
    if (data.code === 0 || data.code === 200) {
      return data
    }
    if (data.code === 401) {
      uni.removeStorageSync('token')
      uni.reLaunch({ url: '/pages/common/login/index' })
      return Promise.reject(new Error('未登录'))
    }
    uni.showToast({ title: data.msg || '请求失败', icon: 'none' })
    return Promise.reject(new Error(data.msg))
  },
  (error: any) => {
    // Mock 模式返回 mock 数据
    if (error?.__MOCK__) {
      return error.data
    }
    // 静默处理网络错误（避免每个页面都弹 toast）
    console.warn('[HTTP]', error?.message || '网络异常')
    return Promise.reject(error)
  }
)

export default http
