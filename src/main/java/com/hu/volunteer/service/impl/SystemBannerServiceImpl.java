package com.hu.volunteer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hu.volunteer.pojo.SystemBanner;
import com.hu.volunteer.mapper.SystemBannerMapper;
import com.hu.volunteer.service.ISystemBannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统轮播图表 服务实现类
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@Service
public class SystemBannerServiceImpl extends ServiceImpl<SystemBannerMapper, SystemBanner> implements ISystemBannerService {

    @Autowired
    private SystemBannerMapper mapper;
    @Override
    public IPage<SystemBanner> getPageByStatus(Page<SystemBanner> bannerPage, Integer status) {
        return mapper.selectPage(bannerPage, new QueryWrapper<SystemBanner>().eq("status", status));
    }
}
