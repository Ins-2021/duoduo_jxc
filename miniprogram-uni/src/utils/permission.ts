const rolePermissions: Record<string, string[]> = {
  worker: ['worker:piecework', 'worker:task', 'worker:wage', 'common:profile'],
  cutter: ['cutter:task', 'cutter:bundle', 'common:profile'],
  inspector: ['inspector:check', 'inspector:first-article', 'inspector:rework', 'common:profile'],
  warehouse: ['warehouse:in', 'warehouse:out', 'warehouse:stock', 'common:profile'],
  leader: ['leader:assign', 'leader:progress', 'leader:approval', 'common:profile'],
  manager: ['manager:dashboard', 'manager:order', 'manager:staff', 'manager:report', 'common:profile'],
  boss: ['boss:dashboard', 'boss:report', 'boss:alert', 'common:profile']
}

export function checkPermission(role: string, permission: string): boolean {
  const permissions = rolePermissions[role]
  return permissions ? permissions.includes(permission) : false
}

export function navigateToPermission(url: string, role: string, permission: string): void {
  if (checkPermission(role, permission)) {
    uni.navigateTo({ url })
  } else {
    uni.showToast({ title: '暂无权限', icon: 'none' })
  }
}
