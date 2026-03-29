import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface TabBarItem {
  pagePath: string
  text: string
  icon: string
}

export const useRoleStore = defineStore('role', () => {
  const currentRole = ref(uni.getStorageSync('currentRole') || '')

  const tabbarConfig: Record<string, TabBarItem[]> = {
    worker: [
      { pagePath: 'pages/worker/index', text: '首页', icon: 'home' },
      { pagePath: 'pages/worker/piecework', text: '计件', icon: 'scan' },
      { pagePath: 'pages/worker/task', text: '任务', icon: 'list' },
      { pagePath: 'pages/worker/wage', text: '工资', icon: 'money-circle' },
      { pagePath: 'pages/common/profile/index', text: '我的', icon: 'account' }
    ],
    cutter: [
      { pagePath: 'pages/cutter/index', text: '首页', icon: 'home' },
      { pagePath: 'pages/cutter/task', text: '裁床', icon: 'cut' },
      { pagePath: 'pages/cutter/bundle', text: '分包', icon: 'box' },
      { pagePath: 'pages/common/profile/index', text: '我的', icon: 'account' }
    ],
    inspector: [
      { pagePath: 'pages/inspector/index', text: '首页', icon: 'home' },
      { pagePath: 'pages/inspector/check', text: '质检', icon: 'checkmark-circle' },
      { pagePath: 'pages/inspector/rework', text: '返工', icon: 'reload' },
      { pagePath: 'pages/common/profile/index', text: '我的', icon: 'account' }
    ],
    warehouse: [
      { pagePath: 'pages/warehouse/index', text: '首页', icon: 'home' },
      { pagePath: 'pages/warehouse/in', text: '入库', icon: 'arrow-down' },
      { pagePath: 'pages/warehouse/out', text: '出库', icon: 'arrow-up' },
      { pagePath: 'pages/common/profile/index', text: '我的', icon: 'account' }
    ],
    leader: [
      { pagePath: 'pages/leader/index', text: '首页', icon: 'home' },
      { pagePath: 'pages/leader/assign', text: '分配', icon: 'share' },
      { pagePath: 'pages/leader/progress', text: '进度', icon: 'chart' },
      { pagePath: 'pages/leader/approval', text: '审批', icon: 'checkmark' },
      { pagePath: 'pages/common/profile/index', text: '我的', icon: 'account' }
    ],
    manager: [
      { pagePath: 'pages/manager/index', text: '首页', icon: 'home' },
      { pagePath: 'pages/manager/dashboard', text: '看板', icon: 'grid' },
      { pagePath: 'pages/manager/order', text: '订单', icon: 'order' },
      { pagePath: 'pages/manager/report', text: '报表', icon: 'chart' },
      { pagePath: 'pages/common/profile/index', text: '我的', icon: 'account' }
    ],
    boss: [
      { pagePath: 'pages/boss/index', text: '首页', icon: 'home' },
      { pagePath: 'pages/boss/dashboard', text: '大屏', icon: 'grid' },
      { pagePath: 'pages/boss/report', text: '报表', icon: 'chart' },
      { pagePath: 'pages/common/profile/index', text: '我的', icon: 'account' }
    ]
  }

  const currentTabbar = computed(() => tabbarConfig[currentRole.value] || [])

  const setRole = (role: string) => {
    currentRole.value = role
    uni.setStorageSync('currentRole', role)
  }

  return { currentRole, tabbarConfig, currentTabbar, setRole }
})
