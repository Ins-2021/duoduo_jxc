import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import Layout from '../layout/index.vue'
import pinia from '@/store'
import { useUserStore } from '@/store/user'

export const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue')
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('../views/error/403.vue')
  },
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/home/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'sales',
        name: 'Sales',
        redirect: '/sales/booking',
        meta: { title: '销售', icon: 'ShoppingCart', perm: 'sales:menu:view' },
        children: [
          {
            path: 'booking',
            name: 'SalesBooking',
            component: () => import('../views/sales/booking/index.vue'),
            meta: { title: '销售预定', perm: 'sales:booking:view' }
          },
          {
            path: 'booking/add',
            name: 'SalesBookingAdd',
            component: () => import('../views/sales/booking/add.vue'),
            meta: { title: '新增销售预定', hidden: true, perm: 'sales:booking:add' }
          },
          {
            path: 'booking/edit/:id',
            name: 'SalesBookingEdit',
            component: () => import('../views/sales/booking/add.vue'),
            meta: { title: '修改销售预定', hidden: true, perm: 'sales:booking:edit' }
          },
          {
            path: 'quotation',
            name: 'SalesQuotation',
            component: () => import('../views/quotation/index.vue'),
            meta: { title: '销售报价', perm: 'sales:quotation:view' }
          },
          {
            path: 'order',
            name: 'SalesOrder',
            component: () => import('../views/sales/order/index.vue'),
            meta: { title: '销售', perm: 'sales:order:view' }
          },
          {
            path: 'order/add',
            name: 'SalesOrderAdd',
            component: () => import('../views/sales/order/add.vue'),
            meta: { title: '新增销售单', hidden: true, perm: 'sales:order:add' }
          },
          {
            path: 'return',
            name: 'SalesReturn',
            component: () => import('../views/sales/return/index.vue'),
            meta: { title: '销售退货', perm: 'sales:return:view' }
          },
          {
            path: 'return/add',
            name: 'SalesReturnAdd',
            component: () => import('../views/sales/return/add.vue'),
            meta: { title: '新增销售退货单', hidden: true, perm: 'sales:return:add' }
          },
          {
            path: 'retail',
            name: 'SalesRetail',
            component: () => import('../views/sales/retail/index.vue'),
            meta: { title: '零售收银', perm: 'sales:retail:view' }
          },
          {
            path: 'retail-return',
            name: 'SalesRetailReturn',
            component: () => import('../views/sales/retail-return/index.vue'),
            meta: { title: '零售退货', perm: 'sales:retail-return:view' }
          },
          {
            path: 'points-exchange',
            name: 'SalesPointsExchange',
            component: () => import('../views/sales/points-exchange/index.vue'),
            meta: { title: '积分兑换商品', perm: 'sales:points-exchange:view' }
          },
          {
            path: 'customer-recharge',
            name: 'SalesCustomerRecharge',
            component: () => import('../views/sales/customer-recharge/index.vue'),
            meta: { title: '客户储值', perm: 'sales:customer-recharge:view' }
          }
        ]
      },
      {
        path: 'purchase',
        name: 'Purchase',
        redirect: '/purchase/booking',
        meta: { title: '进货', icon: 'Van', perm: 'purchase:menu:view' },
        children: [
          {
            path: 'booking',
            name: 'PurchaseBooking',
            component: () => import('../views/purchase/booking/index.vue'),
            meta: { title: '进货预定', perm: 'purchase:booking:view' }
          },
          {
            path: 'booking/add',
            name: 'PurchaseBookingAdd',
            component: () => import('../views/purchase/booking/add.vue'),
            meta: { title: '新增进货预定', hidden: true, perm: 'purchase:booking:add' }
          },
          {
            path: 'index',
            name: 'PurchaseIndex',
            component: () => import('../views/purchase/index.vue'),
            meta: { title: '进货管理', perm: 'purchase:order:view' }
          },
          {
            path: 'add',
            name: 'PurchaseAdd',
            component: () => import('../views/purchase/add.vue'),
            meta: { title: '新增进货单', hidden: true, perm: 'purchase:order:add' }
          },
          {
            path: 'return',
            name: 'PurchaseReturn',
            component: () => import('../views/purchase/return/index.vue'),
            meta: { title: '进货退货', perm: 'purchase:return:view' }
          },
          {
            path: 'return/add',
            name: 'PurchaseReturnAdd',
            component: () => import('../views/purchase/return/add.vue'),
            meta: { title: '新增进货退货单', hidden: true, perm: 'purchase:return:add' }
          }
        ]
      },
      {
        path: 'inventory',
        name: 'Inventory',
        redirect: '/inventory/query',
        meta: { title: '库存', icon: 'Box', perm: 'inventory:menu:view' },
        children: [
          {
            path: 'query',
            name: 'InventoryQuery',
            component: () => import('../views/inventory/index.vue'),
            meta: { title: '库存查询', perm: 'inventory:query:view' }
          },
          {
            path: 'transfer',
            name: 'InventoryTransfer',
            component: () => import('../views/inventory/transfer/index.vue'),
            meta: { title: '调拨单', perm: 'inventory:transfer:view' }
          },
          {
            path: 'check',
            name: 'InventoryCheck',
            component: () => import('../views/inventory/check/index.vue'),
            meta: { title: '库存盘点', perm: 'inventory:check:view' }
          },
          {
            path: 'assembly',
            name: 'InventoryAssembly',
            component: () => import('../views/inventory/assembly/index.vue'),
            meta: { title: '组装拆卸', perm: 'inventory:assembly:view' }
          },
          {
            path: 'stock-in',
            name: 'InventoryStockIn',
            component: () => import('../views/inventory/stock-in/index.vue'),
            meta: { title: '其他入库', perm: 'inventory:stock-in:view' }
          },
          {
            path: 'stock-out',
            name: 'InventoryStockOut',
            component: () => import('../views/inventory/stock-out/index.vue'),
            meta: { title: '其他出库', perm: 'inventory:stock-out:view' }
          },
          {
            path: 'alert',
            name: 'InventoryAlert',
            component: () => import('../views/inventory/alert/index.vue'),
            meta: { title: '库存预警', perm: 'inventory:alert:view' }
          },
          {
            path: 'batch',
            name: 'InventoryBatch',
            component: () => import('../views/batch/index.vue'),
            meta: { title: '批次管理', perm: 'inventory:batch:view' }
          },
          {
            path: 'barcode',
            name: 'InventoryBarcode',
            component: () => import('../views/barcode/index.vue'),
            meta: { title: '条码管理', perm: 'inventory:barcode:view' }
          },
          {
            path: 'barcode/rule',
            name: 'InventoryBarcodeRule',
            component: () => import('../views/barcode/rule.vue'),
            meta: { title: '条码规则', perm: 'inventory:barcode:view' }
          }
        ]
      },
      {
        path: 'finance',
        name: 'Finance',
        redirect: '/finance/receivable',
        meta: { title: '财务', icon: 'Money', perm: 'finance:menu:view' },
        children: [
          {
            path: 'receivable',
            name: 'FinanceReceivable',
            component: () => import('../views/finance/receivable/index.vue'),
            meta: { title: '应收账款', perm: 'finance:receivable:view' }
          },
          {
            path: 'payable',
            name: 'FinancePayable',
            component: () => import('../views/finance/payable/index.vue'),
            meta: { title: '应付账款', perm: 'finance:payable:view' }
          },
          {
            path: 'receipt',
            name: 'FinanceReceipt',
            component: () => import('../views/finance/receipt/index.vue'),
            meta: { title: '收款单', perm: 'finance:receipt:view' }
          },
          {
            path: 'payment',
            name: 'FinancePayment',
            component: () => import('../views/finance/payment/index.vue'),
            meta: { title: '付款单', perm: 'finance:payment:view' }
          },
          {
            path: 'transaction',
            name: 'FinanceTransaction',
            component: () => import('../views/finance/transaction/index.vue'),
            meta: { title: '资金流水', perm: 'finance:transaction:view' }
          },
          {
            path: 'write-off',
            name: 'FinanceWriteOff',
            component: () => import('../views/finance/write-off/index.vue'),
            meta: { title: '核销单', perm: 'finance:write-off:view' }
          },
          {
            path: 'income-expense',
            name: 'FinanceIncomeExpense',
            component: () => import('../views/finance/income-expense/index.vue'),
            meta: { title: '收支记录', perm: 'finance:income-expense:view' }
          },
          {
            path: 'supplier-recon',
            name: 'FinanceSupplierRecon',
            component: () => import('../views/supplier/reconciliation.vue'),
            meta: { title: '供应商对账', perm: 'finance:supplier-recon:view' }
          }
        ]
      },
      {
        path: 'reports',
        name: 'Reports',
        component: () => import('../views/reports/index.vue'),
        meta: { title: '报表', icon: 'DataAnalysis', perm: 'reports:menu:view' }
      },
      {
        path: 'data',
        name: 'Data',
        redirect: '/data/product',
        meta: { title: '资料', icon: 'Document', perm: 'data:menu:view' },
        children: [
          {
            path: 'product',
            name: 'DataProduct',
            component: () => import('../views/data/index.vue'),
            meta: { title: '商品管理', perm: 'data:product:view' }
          },
          {
            path: 'product/add',
            name: 'DataProductAdd',
            component: () => import('../views/data/product/add.vue'),
            meta: { title: '新增商品', hidden: true, perm: 'data:product:add' }
          },
          {
            path: 'product/edit/:id',
            name: 'DataProductEdit',
            component: () => import('../views/data/product/add.vue'),
            meta: { title: '编辑商品', hidden: true, perm: 'data:product:edit' }
          },
          {
            path: 'style',
            name: 'DataStyle',
            component: () => import('../views/style/index.vue'),
            meta: { title: '款式管理', perm: 'data:style:view' }
          },
          {
            path: 'category',
            name: 'DataCategory',
            component: () => import('../views/data/category/index.vue'),
            meta: { title: '商品分类', perm: 'data:category:view' }
          },
          {
            path: 'attribute',
            name: 'DataAttribute',
            component: () => import('../views/data/attribute/index.vue'),
            meta: { title: '属性设置', perm: 'data:attribute:view' }
          },
          {
            path: 'customer',
            name: 'DataCustomer',
            component: () => import('../views/data/customer/index.vue'),
            meta: { title: '客户管理', perm: 'data:customer:view' }
          },
          {
            path: 'supplier',
            name: 'DataSupplier',
            component: () => import('../views/data/supplier/index.vue'),
            meta: { title: '供应商管理', perm: 'data:supplier:view' }
          },
          {
            path: 'warehouse',
            name: 'DataWarehouse',
            component: () => import('../views/data/warehouse/index.vue'),
            meta: { title: '仓库管理', perm: 'data:warehouse:view' }
          },
          {
            path: 'brand',
            name: 'DataBrand',
            component: () => import('../views/data/brand/index.vue'),
            meta: { title: '品牌管理', perm: 'data:brand:view' }
          }
        ]
      },
      {
        path: 'mes',
        name: 'MES',
        redirect: '/mes/process',
        meta: { title: '生产执行', icon: 'Setting', perm: 'mes:menu:view' },
        children: [
          {
            path: 'process',
            name: 'MesProcess',
            component: () => import('../views/process/index.vue'),
            meta: { title: '工序管理', perm: 'mes:process:view' }
          },
          {
            path: 'quality/standard',
            name: 'QualityStandard',
            component: () => import('../views/quality/standard.vue'),
            meta: { title: '质检标准', perm: 'mes:quality:view' }
          },
          {
            path: 'quality/check',
            name: 'QualityCheck',
            component: () => import('../views/quality/check.vue'),
            meta: { title: '质检记录', perm: 'mes:quality:view' }
          }
        ]
      },
      {
        path: 'system',
        name: 'System',
        redirect: '/system/users',
        component: () => import('../views/system/layout.vue'),
        meta: { title: '系统管理', icon: 'Setting', perm: 'system:menu:view' },
        children: [
          {
            path: 'users',
            name: 'SystemUsers',
            component: () => import('../views/system/users.vue'),
            meta: { title: '用户管理', perm: 'system:user:view' }
          },
          {
            path: 'roles',
            name: 'SystemRoles',
            component: () => import('../views/system/roles.vue'),
            meta: { title: '角色管理', perm: 'system:role:view' }
          },
          {
            path: 'menus',
            name: 'SystemMenus',
            component: () => import('../views/system/menus.vue'),
            meta: { title: '菜单管理', perm: 'system:menu:view' }
          },
          {
            path: 'depts',
            name: 'SystemDepts',
            component: () => import('../views/system/depts.vue'),
            meta: { title: '部门管理', perm: 'system:dept:view' }
          },
          {
            path: 'posts',
            name: 'SystemPosts',
            component: () => import('../views/system/posts.vue'),
            meta: { title: '岗位管理', perm: 'system:post:view' }
          }
        ]
      },
      {
        path: 'workflow',
        name: 'Workflow',
        redirect: '/workflow/models',
        component: () => import('../views/workflow/layout.vue'),
        meta: { title: '工作流', icon: 'Share', perm: 'workflow:menu:view' },
        children: [
          {
            path: 'models',
            name: 'WorkflowModels',
            component: () => import('../views/workflow/models.vue'),
            meta: { title: '流程设计', perm: 'workflow:model:view' }
          },
          {
            path: 'designer/:id',
            name: 'WorkflowDesigner',
            component: () => import('../views/workflow/designer.vue'),
            meta: { title: '流程编辑', hidden: true, perm: 'workflow:model:view' }
          },
          {
            path: 'binding',
            name: 'WorkflowBinding',
            component: () => import('../views/workflow/binding.vue'),
            meta: { title: '流程绑定', perm: 'workflow:binding:view' }
          },
          {
            path: 'todo',
            name: 'WorkflowTodo',
            component: () => import('../views/workflow/todo.vue'),
            meta: { title: '我的待办', perm: 'workflow:task:todo' }
          },
          {
            path: 'done',
            name: 'WorkflowDone',
            component: () => import('../views/workflow/done.vue'),
            meta: { title: '我的已办', perm: 'workflow:task:done' }
          },
          {
            path: 'mine',
            name: 'WorkflowMine',
            component: () => import('../views/workflow/mine.vue'),
            meta: { title: '我发起的', perm: 'workflow:instance:mine' }
          },
          {
            path: 'instance/:procInstId',
            name: 'WorkflowInstanceDetail',
            component: () => import('../views/workflow/instance-detail.vue'),
            meta: { title: '流程详情', hidden: true, perm: 'workflow:instance:mine' }
          }
        ]
      },
      {
        path: 'settings',
        name: 'Settings',
        redirect: '/settings/doc-no-rule',
        component: () => import('../views/settings/layout.vue'),
        meta: { title: '设置', icon: 'Setting', perm: 'settings:menu:view' },
        children: [
          {
            path: 'doc-no-rule',
            name: 'SettingsDocNoRule',
            component: () => import('../views/settings/doc-no-rule/index.vue'),
            meta: { title: '单号规则', perm: 'settings:doc-no-rule:view' }
          },
          {
            path: 'system-params',
            name: 'SettingsSystemParams',
            component: () => import('../views/settings/system-params.vue'),
            meta: { title: '系统参数', perm: 'settings:system-params:view' }
          },
          {
            path: 'print-template',
            name: 'SettingsPrintTemplate',
            component: () => import('../views/settings/print-template.vue'),
            meta: { title: '打印模板', perm: 'settings:print-template:view' }
          },
          {
            path: 'print-template/designer/:id',
            name: 'SettingsPrintTemplateDesigner',
            component: () => import('../views/settings/print-template-designer-fabric.vue'),
            meta: { title: '打印模板设计', hidden: true, perm: 'settings:print-template:view' }
          },
          {
            path: 'field-settings',
            name: 'SettingsFieldSettings',
            component: () => import('../views/settings/field-settings.vue'),
            meta: { title: '字段设置', perm: 'settings:field-settings:view' }
          },
          {
            path: 'op-log',
            name: 'SettingsOpLog',
            component: () => import('../views/settings/op-log.vue'),
            meta: { title: '操作日志', perm: 'settings:op-log:view' }
          },
          {
            path: 'staff',
            name: 'SettingsStaff',
            component: () => import('../views/settings/staff/index.vue'),
            meta: { title: '职员管理', perm: 'settings:staff:view' }
          },
          {
            path: 'permissions',
            name: 'SettingsPermissions',
            component: () => import('../views/settings/permissions/index.vue'),
            meta: { title: '权限设置', perm: 'settings:permissions:view' }
          },
          {
            path: 'data-auth',
            name: 'SettingsDataAuth',
            component: () => import('../views/settings/data-auth/index.vue'),
            meta: { title: '数据授权', perm: 'settings:data-auth:view' }
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const rootRoute = routes.find(route => route.path === '/')

const getAccessibleChildren = (route: RouteRecordRaw | undefined) => {
  const userStore = useUserStore(pinia)
  const children = route?.children || []
  return children.filter(child => {
    if ((child.meta as any)?.hidden) {
      return false
    }
    const perm = (child.meta as any)?.perm
    return !perm || userStore.hasPerm(String(perm))
  })
}

const getFirstAccessibleChildPath = (route: RouteRecordRaw | undefined) => {
  const firstChild = getAccessibleChildren(route)[0]
  if (!firstChild) {
    return ''
  }
  return `/${route?.path}/${firstChild.path}`.replace(/\/+/g, '/')
}

router.beforeEach((to) => {
  const userStore = useUserStore(pinia)
  userStore.initialize()

  if (to.meta.title) {
    document.title = `${to.meta.title} - 多多进销存`
  } else {
    document.title = '多多进销存'
  }

  if (to.path === '/login') {
    return true
  }
  const token = userStore.accessToken
  if (!token) {
    return '/login'
  }
  const perm = (to.meta as any)?.perm
  if (perm && !userStore.hasPerm(String(perm))) {
    return '/403'
  }
  const topLevel = rootRoute?.children?.find(route => `/${route.path}` === to.path)
  if (topLevel?.children?.length) {
    const firstAccessibleChildPath = getFirstAccessibleChildPath(topLevel)
    if (!firstAccessibleChildPath) {
      return '/403'
    }
    return firstAccessibleChildPath
  }
  return true
})

export default router
