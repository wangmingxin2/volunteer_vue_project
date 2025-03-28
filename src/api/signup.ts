import request from './request'

// 获取用户报名列表
export function getUserSignups(userId: string, params?: any) {
  return request({
    url: `/signup/user/${userId}`,
    method: 'get',
    params,
  })
}

// 取消报名
export function cancelSignup(signupId: string) {
  return request({
    url: `/signup/cancel/${signupId}`,
    method: 'put',
  })
}

// 分页查询报名信息
export function getSignupPage(params: any) {
  return request({
    url: '/signup/page',
    method: 'get',
    params,
  })
}

// 审核报名申请
export function auditSignup(signupId: string, auditStatus: number, auditRemark?: string) {
  return request({
    url: `/signup/audit/${signupId}`,
    method: 'put',
    data: {
      auditStatus,
      auditRemark,
    },
  })
}

// 记录服务时长
export function recordServiceHours(signupId: string, serviceHours: number) {
  return request({
    url: `/signup/service-hours/${signupId}`,
    method: 'put',
    data: {
      serviceHours,
    },
  })
}

// 新增报名
export function addSignup(data: any) {
  return request({
    url: '/signup',
    method: 'post',
    data,
  })
}

// 修改报名
export function updateSignup(signupId: string, data: any) {
  return request({
    url: `/signup/${signupId}`,
    method: 'put',
    data,
  })
}

// 删除报名
export function deleteSignup(signupId: string) {
  return request({
    url: `/signup/${signupId}`,
    method: 'delete',
  })
}
