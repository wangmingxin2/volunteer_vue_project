import request from './request'

// 定义API响应接口
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 活动报名数据接口
export interface ActivitySignupData {
  activityId: number
  userId: number
  orgId: number
  emergencyContact: string
  emergencyPhone: string
  signupTime?: string // 服务器可以自动生成，可选
}

// 获取用户报名列表
export function getUserSignups(userId: string, params?: any): Promise<ApiResponse<any>> {
  return request({
    url: `/signup/user/${userId}`,
    method: 'get',
    params,
  })
}

// 取消报名
export function cancelSignup(signupId: string): Promise<ApiResponse<any>> {
  return request({
    url: `/signup/cancel/${signupId}`,
    method: 'put',
  })
}

// 分页查询报名信息
export function getSignupPage(params: any): Promise<ApiResponse<any>> {
  return request({
    url: '/signup/page',
    method: 'get',
    params,
  })
}

// 审核报名申请
export function auditSignup(
  signupId: string,
  auditStatus: number,
  auditRemark?: string,
): Promise<ApiResponse<any>> {
  return request({
    url: `/signup/audit/${signupId}`,
    method: 'put',
    params: {
      auditStatus,
      auditRemark,
    },
  })
}

// 记录服务时长
export function recordServiceHours(
  signupId: string,
  serviceHours: number,
): Promise<ApiResponse<any>> {
  return request({
    url: `/signup/service-hours/${signupId}`,
    method: 'put',
    params: {
      serviceHours,
    },
  })
}

// 新增报名
export function addSignup(data: ActivitySignupData): Promise<ApiResponse<any>> {
  return request({
    url: '/signup',
    method: 'post',
    data,
  })
}

// 修改报名
export function updateSignup(signupId: string, data: any): Promise<ApiResponse<any>> {
  return request({
    url: `/signup/${signupId}`,
    method: 'put',
    data,
  })
}

// 删除报名
export function deleteSignup(signupId: string): Promise<ApiResponse<any>> {
  return request({
    url: `/signup/${signupId}`,
    method: 'delete',
  })
}
