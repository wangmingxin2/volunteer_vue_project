import request from './request'

export interface Organization {
  orgId: number
  managerId: number
  orgName: string
  count: number
  description: string
  logo: string
  contactPerson: string | null
  contactPhone: string | null
  contactEmail: string | null
  address: string | null
  establishmentDate: string | null
  status: number
  createdBy: string | null
  createdTime: string
  updatedBy: string | null
  updatedTime: string
}

export function getTopOrganizations() {
  return request({
    url: '/organize/top5',
    method: 'get',
  })
}

// 获取组织列表
export function getOrganizationList(params: any) {
  return request({
    url: '/organize/page',
    method: 'get',
    params,
  })
}

// 新增组织
export function addOrganization(data: any) {
  return request({
    url: '/organize',
    method: 'post',
    data,
  })
}

// 更新组织
export function updateOrganization(data: any) {
  return request({
    url: `/organize/${data.orgId}`,
    method: 'put',
    data,
  })
}

// 删除组织
export function deleteOrganization(orgId: string) {
  return request({
    url: `/organize/${orgId}`,
    method: 'delete',
  })
}

// 修改组织状态
export function updateOrganizationStatus(orgId: string | number, status: number) {
  return request({
    url: '/organize/status',
    method: 'put',
    params: { orgId, status },
  })
}

// 根据管理者ID获取组织信息
export function getOrgByManagerId(managerId: string) {
  return request({
    url: `/organize/manager/${managerId}`,
    method: 'get',
  })
}

// 根据组织ID更新组织信息
export function updateOrgById(id: string | number, data: any) {
  return request({
    url: `/organize/${id}`,
    method: 'put',
    data,
  })
}
