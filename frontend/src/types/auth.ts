/** 登录请求 */
export interface LoginRequest {
  username: string
  password: string
}

/** 刷新 Token 请求 */
export interface RefreshTokenRequest {
  refreshToken: string
}

/** Token 响应 */
export interface TokenResponse {
  tokenType: string
  accessToken: string
  expiresIn: number
  refreshToken: string
}

/** 修改密码请求 */
export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
}

/** 重置密码请求 */
export interface ResetPasswordRequest {
  newPassword: string
}

/** 当前用户信息 */
export interface CurrentUserResponse {
  userId: number
  username: string
  realName: string
  perms: string[]
  roles: string[]
}

/** 菜单树节点 */
export interface MenuTreeNode {
  menuId: number
  parentId: number
  menuName: string
  orderNum: number
  path?: string
  component?: string
  routeName?: string
  icon?: string
  menuType: string
  visible: number
  status: number
  perms?: string
  children: MenuTreeNode[]
}

/** 菜单保存请求 */
export interface MenuSaveRequest {
  parentId: number
  menuName: string
  orderNum?: number
  path?: string
  component?: string
  routeName?: string
  icon?: string
  menuType: string
  visible?: number
  status?: number
  perms?: string
}

/** 角色创建请求 */
export interface RoleCreateRequest {
  roleKey: string
  roleName: string
  dataScope?: string
  status?: number
}

/** 角色更新请求 */
export interface RoleUpdateRequest {
  roleName?: string
  dataScope?: string
  status?: number
}

/** 用户创建请求 */
export interface UserCreateRequest {
  username: string
  deptId?: number
  realName: string
  password: string
  status?: number
}

/** 用户更新请求 */
export interface UserUpdateRequest {
  userId: number
  deptId?: number
  realName?: string
  status?: number
}

/** 在线用户 */
export interface OnlineUserDTO {
  userId: number
  username: string
  jti: string
  loginAt: number
  lastSeenAt: number
  expiresAt: number
}
