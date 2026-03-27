/** 统一响应包装 */
export interface Result<T> {
  code: number
  msg: string
  data: T
}

/** 分页数据 */
export interface PageResult<T> {
  total: number
  list: T[]
}

/** 分页查询基类 */
export interface PageQuery {
  pageNum: number
  pageSize: number
  params?: Record<string, any>
}

/** 通用下拉选项 */
export interface OptionDTO {
  value: number
  label: string
}

/** 字典项 */
export interface DictItemDTO {
  value: string
  label: string
}

/** 文件上传响应 */
export interface FileUploadResponse {
  fileId: number
  fileName: string
  contentType: string
  size: number
  downloadUrl: string
}
