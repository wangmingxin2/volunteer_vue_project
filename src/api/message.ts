import request from './request'

export interface MessageItem {
  notificationId: number
  userId: number
  title: string
  content: string
  isRead: number
  createdBy: string
  createdTime: string
  updatedBy: string
  updatedTime: string
}

export interface MessagePageData {
  records: MessageItem[]
  total: number
  size: number
  current: number
  pages: number
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 获取消息分页列表
export function getMessagePage(params: {
  userId: string | number
  isRead?: number
  page?: number
  size?: number
}): Promise<ApiResponse<MessagePageData>> {
  return request({
    url: '/message/page',
    method: 'get',
    params,
  })
}

// 标记消息为已读
export function markMessageAsRead(notificationId: number | string): Promise<ApiResponse> {
  return request({
    url: `/message/read/${notificationId}`,
    method: 'put',
  })
}

// 批量标记消息为已读
export function batchMarkAsRead(notificationIds: (number | string)[]): Promise<ApiResponse> {
  return request({
    url: '/message/batch-read',
    method: 'put',
    data: notificationIds,
  })
}

// 删除消息
export function deleteMessage(notificationId: number | string): Promise<ApiResponse> {
  return request({
    url: `/message/${notificationId}`,
    method: 'delete',
  })
}

// 全部标记为已读
export function markAllAsRead(userId: number | string): Promise<ApiResponse> {
  return request({
    url: `/message/allRead/${userId}`,
    method: 'put',
  })
}
