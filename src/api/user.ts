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
