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

export function getAnnouncementPage(page: number, size: number = 10, status?: number) {
  return request({
    url: '/announcement/page',
    method: 'get',
    params: {
      page,
      size,
      status,
    },
  })
}

// 新增公告
export function addAnnouncement(data: any) {
  return request({
    url: '/announcement',
    method: 'post',
    data,
  })
}

// 更新公告
export function updateAnnouncement(data: any) {
  return request({
    url: `/announcement/${data.announcementId}`,
    method: 'put',
    data,
  })
}

// 删除公告
export function deleteAnnouncement(announcementId: string) {
  return request({
    url: `/announcement/${announcementId}`,
    method: 'delete',
  })
}
