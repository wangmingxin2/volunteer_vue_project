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
