import request from './request'

export interface UserDto {
  username: string
  password: string
  typeId: number
}

export interface RegisterDto {
  username: string
  password: string
  typeId: number
}

export interface LoginResult {
  token: string
  userId: number
  username: string
  typeId: number
  // 可能还有其他返回字段
}

export interface UserInfo {
  userId: string
  username: string
  typeId: string
  realName?: string
  idCard?: string
  gender?: string
  email?: string
  phone?: string
  avatar?: string
  // 可能还有其他字段
}

export function login(data: UserDto) {
  return request({
    url: '/user/login',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

export function register(data: RegisterDto) {
  return request({
    url: '/user/register',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get',
    headers: {
      'Content-Type': 'application/json',
      satoken: localStorage.getItem('satoken') || '',
    },
  })
}

export function updatePassword(oldPassword: string, newPassword: string) {
  return request({
    url: '/user/updatePwd',
    method: 'post',
    params: {
      oldPassword,
      newPassword,
    },
  })
}

// 更新用户信息
export function updateUserInfo(data: UserInfo) {
  return request({
    url: `/user/${data.userId}`,
    method: 'put',
    data,
    headers: {
      'Content-Type': 'application/json',
      satoken: localStorage.getItem('satoken') || '',
    },
  })
}

// 获取用户列表
export function getUserList(params: any) {
  return request({
    url: '/user/page',
    method: 'get',
    params,
  })
}

// 新增用户
export function addUser(data: any) {
  return request({
    url: '/user',
    method: 'post',
    data,
  })
}

// 更新用户
export function updateUser(data: any) {
  return request({
    url: `/user/${data.userId}`,
    method: 'put',
    data,
  })
}

// 删除用户
export function deleteUser(userId: string) {
  return request({
    url: `/user/${userId}`,
    method: 'delete',
  })
}

// 重置用户密码
export function resetUserPassword(userId: string) {
  return request({
    url: `/user/resetPassword/${userId}`,
    method: 'post',
  })
}

// 修改用户状态
export function updateUserStatus(userId: string | number, status: number) {
  return request({
    url: '/user/status',
    method: 'put',
    params: { userId, status }, // 使用 params 而不是 data
  })
}
