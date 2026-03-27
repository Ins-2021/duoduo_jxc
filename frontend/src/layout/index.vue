<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="aside">
      <div class="logo">多多进销存</div>
      <el-menu
        :default-active="activeMenu"
        :default-openeds="defaultOpeneds"
        :unique-opened="true"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <template v-for="menu in sidebarMenus" :key="menu.menuId">
          <!-- 目录/有子菜单 -->
          <el-sub-menu
            v-if="menu.children?.length"
            :index="menu.path || '/' + menu.path"
          >
            <template #title>
              <el-icon v-if="menu.icon">
                <component :is="menu.icon" />
              </el-icon>
              <span>{{ menu.menuName }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children"
              :key="child.menuId"
              :index="resolveMenuPath(child)"
            >
              <span>{{ child.menuName }}</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- 无子菜单的顶级菜单 -->
          <el-menu-item
            v-else
            :index="menu.path || '/' + menu.path"
          >
            <el-icon v-if="menu.icon">
              <component :is="menu.icon" />
            </el-icon>
            <span>{{ menu.menuName }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container class="main-container">
      <el-header class="header" height="auto">
        <!-- 顶部工具栏 -->
        <div class="top-navbar">
          <div class="navbar-left">
            <el-icon class="refresh-icon"><RefreshRight /></el-icon>
            <el-dropdown>
              <span class="company-dropdown">
                衣多多服饰 <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>衣多多服饰</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <span class="divider">|</span>
            <span class="expire-date">到期日期：永久</span>
          </div>
          
          <div class="navbar-right">
            <el-button link class="nav-btn">
              <el-icon><Shop /></el-icon> 设置门店
            </el-button>
            <el-button link class="nav-btn" @click="openChangePwd">
              <el-icon><Lock /></el-icon> 修改密码
            </el-button>
            <el-dropdown>
              <span class="user-dropdown">
                {{ userStore.displayName }} <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="doLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        
        <!-- 多页签区域占位 -->
        <div class="tags-view-container">
          <TagsView />
        </div>
      </el-header>
      
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>

    <!-- 修改密码弹窗 -->
    <el-dialog title="修改密码" v-model="pwdVisible" width="400px" append-to-body>
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="pwdVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPwd">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import TagsView from './TagsView.vue'
import pinia from '@/store'
import { useUserStore, type UserMenuItem } from '@/store/user'
import { logout, changePassword } from '@/api/auth'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'

const route = useRoute()
const userStore = useUserStore(pinia)
userStore.initialize()

// ==================== 密码修改 ====================
const pwdVisible = ref(false)
const pwdFormRef = ref<FormInstance>()
const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPwd = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== pwdForm.value.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [{ required: true, validator: validateConfirmPwd, trigger: 'blur' }]
}

const openChangePwd = () => {
  pwdForm.value.oldPassword = ''
  pwdForm.value.newPassword = ''
  pwdForm.value.confirmPassword = ''
  if (pwdFormRef.value) {
    pwdFormRef.value.resetFields()
  }
  pwdVisible.value = true
}

const submitPwd = () => {
  pwdFormRef.value?.validate((valid) => {
    if (valid) {
      changePassword({ oldPassword: pwdForm.value.oldPassword, newPassword: pwdForm.value.newPassword }).then(() => {
        ElMessage.success('密码修改成功，请重新登录')
        pwdVisible.value = false
        doLogout()
      })
    }
  })
}

// ==================== 侧边栏菜单（后端数据驱动） ====================

/**
 * 构建侧边栏菜单树
 * - visible=0 的菜单不显示
 * - status=0 的菜单不显示
 * - menuType='F'(按钮) 不作为菜单显示
 * - 仅保留 menuType='M'(目录) 和 'C'(菜单)
 * - 按 orderNum 排序
 * - 过滤掉 children 为空的目录
 */
const sidebarMenus = computed<UserMenuItem[]>(() => {
  const menus = userStore.userMenus || []
  return filterSidebarMenus(menus)
})

const filterSidebarMenus = (items: UserMenuItem[]): UserMenuItem[] => {
  // 只保留目录(M)和菜单(C)，排除按钮(F)
  // 过滤 visible=0（隐藏）和 status=0（停用）
  const filtered = items
    .filter(item => item.menuType !== 'F' && item.visible !== 0 && item.status !== 0)
    .sort((a, b) => (a.orderNum || 0) - (b.orderNum || 0))
    .map(item => {
      const children = item.children ? filterSidebarMenus(item.children) : []
      return { ...item, children }
    })
    .filter(item => {
      // 目录(M)必须要有可见子菜单才显示
      if (item.menuType === 'M' && item.children.length === 0) {
        return false
      }
      return true
    })
  return filtered
}

/**
 * 解析菜单路径
 * 子菜单的 path 可能是相对路径（如 'product'）或绝对路径（如 '/sales/order'）
 */
const resolveMenuPath = (menu: UserMenuItem): string => {
  if (!menu.path) return '/'
  if (menu.path.startsWith('/')) return menu.path
  return '/' + menu.path
}

const activeMenu = computed(() => {
  if (route.path.startsWith('/settings/')) {
    return '/settings'
  }
  return route.path
})

/**
 * 当前路由所属的父菜单路径，用于手风琴模式下自动展开
 * 根据当前路由路径找到匹配的顶级菜单，返回其 index
 */
const defaultOpeneds = computed<string[]>(() => {
  const path = route.path
  for (const menu of sidebarMenus.value) {
    if (menu.children?.length && menu.children.some(child => {
      const childPath = resolveMenuPath(child)
      return path === childPath || path.startsWith(childPath + '/')
    })) {
      return [menu.path || '/' + menu.path]
    }
  }
  return []
})

// ==================== 退出登录 ====================
const doLogout = async () => {
  try {
    const rt = userStore.refreshToken
    await logout(rt || undefined)
  } finally {
    userStore.clearAuth()
    location.href = '/login'
  }
}

// ==================== 初始化 ====================
onMounted(() => {
  userStore.loadUserMenus()
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  width: 100%;
  min-width: 1200px;
}

.aside {
  background-color: #304156;
  color: white;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  background-color: #2b3643;
}

.el-menu-vertical {
  border-right: none;
  flex: 1;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0;
  display: flex;
  flex-direction: column;
}

.top-navbar {
  height: 50px;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 14px;
}

.refresh-icon {
  font-size: 18px;
  cursor: pointer;
  color: #606266;
}

.company-dropdown {
  cursor: pointer;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 4px;
}

.divider {
  color: #dcdfe6;
}

.expire-date {
  color: #909399;
  font-size: 13px;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-btn {
  color: #606266;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.nav-btn:hover {
  color: #409EFF;
}

.user-dropdown {
  cursor: pointer;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.tags-view-container {
  height: 40px;
  background-color: #f5f7f9;
}

.main {
  background-color: #f0f2f5;
  padding: 10px;
  height: calc(100vh - 90px);
  overflow-y: auto;
}
</style>
