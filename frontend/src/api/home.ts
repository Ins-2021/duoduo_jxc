import request from '@/utils/request'
import type { Result, HomeOverviewDTO, PrintTemplateDTO, PageResult } from '@/types'

/** @deprecated 使用 @/types 中的类型 */
export interface HomeOverview extends HomeOverviewDTO {}

/** @deprecated 使用 @/types 中的类型 */
export interface ApiResult<T> extends Result<T> {}

export function getHomeOverview(): Promise<Result<HomeOverviewDTO>> {
  return request({
    url: '/home/overview',
    method: 'get'
  })
}
