import request from '../api/request'

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
