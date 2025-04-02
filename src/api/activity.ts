import request from './request'

export interface Activity {
  activityId: number
  orgId: number
  activityName: string
  activityType: string
  actibityImg: string
  description: string
  location: string
  startTime: string
  endTime: string
  volunteerQuota: number
  signedCount: number
  contactPerson: string
  contactPhone: string
  status: number
  createdBy: string
  createdTime: string
  updatedBy: string
  updatedTime: string
}

// 获取活动列表
export function getActivityPage(
  page: number,
  size: number = 10,
  status?: string,
  orgId?: string | number,
  activityName?: string,
  managerId?: string | number,
) {
  return request({
    url: '/activity/page',
    method: 'get',
    params: {
      page,
      size,
      status,
      orgId,
      activityName,
      managerId,
    },
  })
}

// 新增活动
export function addActivity(data: any) {
  return request({
    url: '/activity',
    method: 'post',
    data,
  })
}

// 更新活动
export function updateActivity(data: any) {
  return request({
    url: `/activity/${data.activityId}`,
    method: 'put',
    data,
  })
}

// 删除活动
export function deleteActivity(activityId: string) {
  return request({
    url: `/activity/${activityId}`,
    method: 'delete',
  })
}

// 修改活动状态
export function updateActivityStatus(activityId: string | number, status: number) {
  return request({
    url: '/activity/status',
    method: 'put',
    params: { activityId, status },
  })
}

// 获取活动报名情况
export function getActivitySignups(activityId: string | number, page: number, size: number = 10) {
  return request({
    url: `/activity/${activityId}/signups`,
    method: 'get',
    params: { page, size },
  })
}

// 通过报名申请
export function approveSignup(signupId: string | number) {
  return request({
    url: `/activity/signup/${signupId}/approve`,
    method: 'put',
  })
}

// 添加获取所有活动的方法
export function getAllActivities() {
  return request({
    url: '/activity',
    method: 'get',
  })
}
