import request from '@/utils/request'
import type {
  DocNoRuleDTO, UserCreateRequest, UserUpdateRequest, ResetPasswordRequest,
  RoleCreateRequest, RoleUpdateRequest, MenuSaveRequest, MenuTreeNode,
  PageQuery,
} from '@/types'

export const docNoRuleApi = {
  pageList: (params: PageQuery & { docType?: string; status?: number }) => {
    return request({
      url: '/settings/doc-no-rule/page',
      method: 'post',
      data: params
    })
  },
  getById: (id: number) => {
    return request({
      url: `/settings/doc-no-rule/${id}`,
      method: 'get'
    })
  },
  create: (data: Omit<DocNoRuleDTO, 'ruleId' | 'createTime' | 'updateTime'>) => {
    return request({
      url: '/settings/doc-no-rule',
      method: 'post',
      data
    })
  },
  update: (data: DocNoRuleDTO) => {
    return request({
      url: '/settings/doc-no-rule',
      method: 'put',
      data
    })
  },
  delete: (id: number) => {
    return request({
      url: `/settings/doc-no-rule/${id}`,
      method: 'delete'
    })
  },
  toggleStatus: (id: number) => {
    return request({
      url: `/settings/doc-no-rule/${id}/toggle`,
      method: 'put'
    })
  },
  batchToggleStatus: (ids: number[], status: number) => {
    return request({
      url: '/settings/doc-no-rule/batch-toggle',
      method: 'put',
      data: { ids, status }
    })
  }
}

export function listUsers(params?: PageQuery & { username?: string; status?: number }) {
  return request({
    url: '/system/users',
    method: 'get',
    params
  })
}

export function createUser(data: UserCreateRequest) {
  return request({
    url: '/system/users',
    method: 'post',
    data
  })
}

export function updateUser(userId: number, data: Omit<UserUpdateRequest, 'userId'>) {
  return request({
    url: `/system/users/${userId}`,
    method: 'put',
    data
  })
}

export function deleteUser(userId: number) {
  return request({
    url: `/system/users/${userId}`,
    method: 'delete'
  })
}

export function resetPassword(userId: number, data: ResetPasswordRequest) {
  return request({
    url: `/system/users/${userId}/reset-password`,
    method: 'post',
    data
  })
}

export function assignUserRoles(userId: number, payload: number[] | { roleIds: number[] }) {
  return request({
    url: `/system/users/${userId}/roles`,
    method: 'post',
    data: Array.isArray(payload) ? { roleIds: payload } : payload
  })
}

export function listRoles() {
  return request({
    url: '/system/roles',
    method: 'get'
  })
}

export function createRole(data: RoleCreateRequest) {
  return request({
    url: '/system/roles',
    method: 'post',
    data
  })
}

export function updateRole(roleId: number, data: RoleUpdateRequest) {
  return request({
    url: `/system/roles/${roleId}`,
    method: 'put',
    data
  })
}

export function deleteRole(roleId: number) {
  return request({
    url: `/system/roles/${roleId}`,
    method: 'delete'
  })
}

export function getRoleMenuIds(roleId: number) {
  return request({
    url: `/system/roles/${roleId}/menus`,
    method: 'get'
  })
}

export function assignRoleMenus(roleId: number, payload: number[] | { menuIds: number[] }) {
  return request({
    url: `/system/roles/${roleId}/menus`,
    method: 'post',
    data: Array.isArray(payload) ? { menuIds: payload } : payload
  })
}

export function getMenuTree() {
  return request({
    url: '/system/menus/tree',
    method: 'get'
  })
}

export function createMenu(data: MenuSaveRequest) {
  return request({
    url: '/system/menus',
    method: 'post',
    data
  })
}

export function updateMenu(menuId: number, data: Partial<MenuSaveRequest>) {
  return request({
    url: `/system/menus/${menuId}`,
    method: 'put',
    data
  })
}

export function deleteMenu(menuId: number) {
  return request({
    url: `/system/menus/${menuId}`,
    method: 'delete'
  })
}

export function listDepts(params?: Record<string, any>) {
  return request({
    url: '/system/dept/list',
    method: 'get',
    params
  })
}

export function listDeptsExclude(deptId: number) {
  return request({
    url: `/system/dept/list/exclude/${deptId}`,
    method: 'get'
  })
}

export function getDept(deptId: number) {
  return request({
    url: `/system/dept/${deptId}`,
    method: 'get'
  })
}

export function createDept(data: Record<string, any>) {
  return request({
    url: '/system/dept',
    method: 'post',
    data
  })
}

export function updateDept(data: Record<string, any>) {
  return request({
    url: '/system/dept',
    method: 'put',
    data
  })
}

export function deleteDept(deptId: number) {
  return request({
    url: `/system/dept/${deptId}`,
    method: 'delete'
  })
}

export function pagePosts(params: Record<string, unknown>) {
  return request({
    url: '/system/post/page',
    method: 'get',
    params: {
      current: params?.current ?? params?.pageNum ?? 1,
      size: params?.size ?? params?.pageSize ?? 10,
      postCode: params?.postCode,
      postName: params?.postName,
      status: params?.status
    }
  })
}

export function getPost(postId: number) {
  return request({
    url: `/system/post/${postId}`,
    method: 'get'
  })
}

export function createPost(data: Record<string, any>) {
  return request({
    url: '/system/post',
    method: 'post',
    data
  })
}

export function updatePost(data: Record<string, unknown>) {
  return request({
    url: '/system/post',
    method: 'put',
    data
  })
}

export function deletePost(postId: number | string) {
  return request({
    url: `/system/post/${postId}`,
    method: 'delete'
  })
}
