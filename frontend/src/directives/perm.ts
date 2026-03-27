import type { Directive } from 'vue'
import pinia from '@/store'
import { useUserStore } from '@/store/user'

export const perm: Directive = {
  mounted(el, binding) {
    const userStore = useUserStore(pinia)
    userStore.initialize()

    const value = binding.value
    if (!value) return
    if (typeof value === 'string') {
      if (!userStore.hasPerm(value)) {
        el.style.display = 'none'
      }
      return
    }
    if (Array.isArray(value)) {
      const ok = value.some(v => typeof v === 'string' && userStore.hasPerm(v))
      if (!ok) {
        el.style.display = 'none'
      }
    }
  }
}
