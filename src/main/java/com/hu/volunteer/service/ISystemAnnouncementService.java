package com.hu.volunteer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hu.volunteer.pojo.SystemAnnouncement;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统公告表 服务类
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
public interface ISystemAnnouncementService extends IService<SystemAnnouncement> {

    IPage<SystemAnnouncement> getPageByStatus(Page<SystemAnnouncement> announcementPage, Integer status);
}
