import request from '@/utils/request'
import type {
  WfModelCreateRequest, WfModelSaveRequest, WfModelPublishRequest,
  WfBindingSaveRequest, WfInstanceStartRequest,
  WfDiagramStateResponse, WfTaskApproveRequest, WfTaskRejectRequest,
} from '@/types'

export function listWorkflowModels() {
  return request({
    url: '/workflow/models',
    method: 'get'
  })
}

export function createWorkflowModel(data: WfModelCreateRequest, operatorId?: number) {
  return request({
    url: '/workflow/models',
    method: 'post',
    params: operatorId ? { operatorId } : undefined,
    data
  })
}

export function getWorkflowModel(id: number) {
  return request({
    url: `/workflow/models/${id}`,
    method: 'get'
  })
}

export function saveWorkflowModel(id: number, data: WfModelSaveRequest, operatorId?: number) {
  return request({
    url: `/workflow/models/${id}`,
    method: 'put',
    params: operatorId ? { operatorId } : undefined,
    data
  })
}

export function validateWorkflowModel(id: number) {
  return request({
    url: `/workflow/models/${id}/validate`,
    method: 'post'
  })
}

export function publishWorkflowModel(id: number, data?: WfModelPublishRequest) {
  return request({
    url: `/workflow/models/${id}/publish`,
    method: 'post',
    data
  })
}

export function getWorkflowBinding(bizType: string) {
  return request({
    url: '/workflow/bindings',
    method: 'get',
    params: { bizType }
  })
}

export function saveWorkflowBinding(bizType: string, data: WfBindingSaveRequest, operatorId?: number) {
  return request({
    url: `/workflow/bindings/${bizType}`,
    method: 'put',
    params: operatorId ? { operatorId } : undefined,
    data
  })
}

export function startWorkflowInstance(data: WfInstanceStartRequest) {
  return request({
    url: '/workflow/instances/start',
    method: 'post',
    data
  })
}

export function listMyWorkflowInstances() {
  return request({
    url: '/workflow/instances/mine',
    method: 'get'
  })
}

export function getWorkflowInstanceDetail(procInstId: string) {
  return request({
    url: `/workflow/instances/${procInstId}`,
    method: 'get'
  })
}

export function getWorkflowDiagram(procInstId: string) {
  return request({
    url: `/workflow/instances/${procInstId}/diagram`,
    method: 'get'
  })
}

export function getWorkflowDiagramState(procInstId: string) {
  return request({
    url: `/workflow/instances/${procInstId}/diagram-state`,
    method: 'get'
  })
}

export function listTodoTasks() {
  return request({
    url: '/workflow/tasks/todo',
    method: 'get'
  })
}

export function listDoneTasks() {
  return request({
    url: '/workflow/tasks/done',
    method: 'get'
  })
}

export function approveTask(taskId: string, data: WfTaskApproveRequest) {
  return request({
    url: `/workflow/tasks/${taskId}/approve`,
    method: 'post',
    data
  })
}

export function rejectTask(taskId: string, data: WfTaskRejectRequest) {
  return request({
    url: `/workflow/tasks/${taskId}/reject`,
    method: 'post',
    data
  })
}
