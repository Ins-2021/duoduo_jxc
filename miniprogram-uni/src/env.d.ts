/// <reference types="@dcloudio/types" />
declare module '*.vue' {
  import { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

interface PageParams {
  [key: string]: string | number
}

declare global {
  namespace UniApp {
    // 扩展全局类型
  }
}

export {}
