import request from './request'

export interface Announcement {
  announcementId: number
  title: string
  content: string
  coverImage: string
  status: number
  createdBy: string | null
  createdTime: string
  updatedBy: string | null
  updatedTime: string
  deleted: number
}

export function getAnnouncementPage(page: number, size: number, status: number = 0) {
  return request({
    url: '/announcement/page',
    method: 'get',
    params: {
      status,
      page,
      size,
    },
  })
}
