import request from './request'

// 获取仪表盘数据
export function getDashboardData() {
  return request({
    url: '/dashboard',
    method: 'get',
  })
}
