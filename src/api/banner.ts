import request from './request'

export interface Banner {
  bannerId: number
  title: string
  imageUrl: string
  description: string | null
  bannerType: number
  status: number
  createdBy: string | null
  createdTime: string
  updatedBy: string | null
  updatedTime: string
  deleted: number
}

export function getBanners() {
  return request({
    url: '/banner',
    method: 'get',
  })
}
