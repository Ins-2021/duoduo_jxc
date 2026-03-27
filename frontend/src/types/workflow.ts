/** 工作流模型创建请求 */
export interface WfModelCreateRequest {
  modelKey: string
  name: string
  category?: string
}

/** 工作流模型保存请求 */
export interface WfModelSaveRequest {
  bpmnXml: string
  name?: string
  category?: string
}

/** 工作流模型发布请求 */
export interface WfModelPublishRequest {
  publishedBy?: number
  remark?: string
}

/** 工作流绑定保存请求 */
export interface WfBindingSaveRequest {
  processDefKey: string
  enabled?: number
  startCondition?: string
}

/** 工作流实例启动请求 */
export interface WfInstanceStartRequest {
  bizType: string
  bizId: string
  title: string
  initiatorId?: number
  variables?: Record<string, any>
}

/** 工作流图状态 */
export interface WfDiagramStateResponse {
  activeNodeKeys: string[]
  finishedNodeKeys: string[]
}

/** 工作流审批请求 */
export interface WfTaskApproveRequest {
  userId?: number
  comment?: string
  variables?: Record<string, any>
}

/** 工作流驳回请求 */
export interface WfTaskRejectRequest {
  userId?: number
  comment?: string
  rejectType?: string
}
