import request from '@/utils/request'

export function getCustomerOptions() {
  return request({
    url: '/options/customers',
    method: 'get'
  })
}

export function getSupplierOptions() {
  return request({
    url: '/options/suppliers',
    method: 'get'
  })
}

export function getWarehouseOptions() {
  return request({
    url: '/options/warehouses',
    method: 'get'
  })
}

export function getStoreOptions() {
  return request({
    url: '/options/stores',
    method: 'get'
  })
}

export function getStaffOptions() {
  return request({
    url: '/options/staff',
    method: 'get'
  })
}

export function getFinanceAccountOptions() {
  return request({
    url: '/options/finance-accounts',
    method: 'get'
  })
}

export async function getCategoryOptions() {
  const res = await request({
    url: '/product/categories/tree',
    method: 'get'
  })
  const options: Array<{ label: string; value: number | string }> = []
  const walk = (nodes: Array<{ label?: string; name?: string; id: number | string; children?: any[] }>) => {
    nodes.forEach(node => {
      options.push({ label: node.label || node.name || '', value: node.id })
      if (Array.isArray(node.children) && node.children.length > 0) {
        walk(node.children)
      }
    })
  }
  walk(res.data || [])
  return options
}
