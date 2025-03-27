package com.hu.volunteer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hu.volunteer.pojo.SystemAnnouncement;
import com.hu.volunteer.mapper.SystemAnnouncementMapper;
import com.hu.volunteer.service.ISystemAnnouncementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统公告表 服务实现类
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@Service
public class SystemAnnouncementServiceImpl extends ServiceImpl<SystemAnnouncementMapper, SystemAnnouncement> implements ISystemAnnouncementService {

    @Autowired
    private SystemAnnouncementMapper mapper;
    @Override
    public IPage<SystemAnnouncement> getPageByStatus(Page<SystemAnnouncement> announcementPage, Integer status) {
        return mapper.selectPage(announcementPage, new QueryWrapper<SystemAnnouncement>().eq("status", status));
    }
}
