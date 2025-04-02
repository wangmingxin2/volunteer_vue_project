import request from './request'

// API响应接口定义
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface ReflectionPageData {
  records: ReflectionItem[]
  total: number
  size: number
  current: number
  pages: number
}

export interface ReflectionItem {
  reflectionId: number
  userId: number
  activityId: number
  activityName: string
  title: string
  content: string
  images?: string
  auditStatus: number
  auditRemark?: string
  likeCount: number
  isPublic: number
  createdTime: string
  updatedTime: string
  userRealName?: string
}

// 获取心得分页列表
export function getReflectionPage(params: any): Promise<ApiResponse<ReflectionPageData>> {
  return request({
    url: '/reflection/page',
    method: 'get',
    params,
  })
}

// 审核心得
export function auditReflection(
  reflectionId: number | string,
  auditStatus: number,
  auditRemark?: string,
): Promise<ApiResponse> {
  return request({
    url: `/reflection/audit/${reflectionId}`,
    method: 'put',
    params: { auditStatus, auditRemark: auditRemark || undefined },
  })
}

// 删除心得
export function deleteReflection(reflectionId: number | string): Promise<ApiResponse> {
  return request({
    url: `/reflection/${reflectionId}`,
    method: 'delete',
  })
}

export interface ReflectionData {
  reflectionId?: number
  userId: number | string
  activityId: number | string
  title: string
  content: string
  images?: string
  isPublic?: number
}

// 新增心得
export function addReflection(data: ReflectionData): Promise<ApiResponse> {
  return request({
    url: '/reflection',
    method: 'post',
    data,
  })
}

// 更新心得
export function updateReflection(data: ReflectionData): Promise<ApiResponse> {
  return request({
    url: `/reflection/${data.reflectionId}`,
    method: 'put',
    data,
  })
}

// 获取心得详情
export function getReflectionById(
  reflectionId: number | string,
): Promise<ApiResponse<ReflectionItem>> {
  return request({
    url: `/reflection/${reflectionId}`,
    method: 'get',
  })
}

// 上传图片
export function uploadImage(file: File): Promise<ApiResponse<string>> {
  const formData = new FormData()
  formData.append('file', file)

  return request({
    url: '/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

// 添加获取用户已提交心得的活动列表接口
export function getUserSubmittedActivityIds(
  userId: number | string,
): Promise<ApiResponse<number[]>> {
  return request({
    url: `/reflection/submitted-activities/${userId}`,
    method: 'get',
  })
}
