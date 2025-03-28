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

// 获取轮播图列表
export function getBannerPage(
  page: number,
  size: number = 10,
  status?: number,
  bannerType?: number,
) {
  return request({
    url: '/banner/page',
    method: 'get',
    params: {
      page,
      size,
      status,
      bannerType,
    },
  })
}

// 获取所有轮播图（首页使用）
export function getBanners() {
  return request({
    url: '/banner',
    method: 'get',
  })
}

// 新增轮播图
export function addBanner(data: any) {
  return request({
    url: '/banner',
    method: 'post',
    data,
  })
}

// 更新轮播图
export function updateBanner(data: any) {
  return request({
    url: `/banner/${data.bannerId}`,
    method: 'put',
    data,
  })
}

// 删除轮播图
export function deleteBanner(bannerId: string) {
  return request({
    url: `/banner/${bannerId}`,
    method: 'delete',
  })
}

// 修改轮播图状态
export function updateBannerStatus(bannerId: string | number, status: number) {
  return request({
    url: '/banner/status',
    method: 'put',
    params: { bannerId, status },
  })
}
