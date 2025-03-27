package com.hu.volunteer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hu.volunteer.pojo.SystemBanner;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统轮播图表 服务类
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
public interface ISystemBannerService extends IService<SystemBanner> {

    IPage<SystemBanner> getPageByStatus(Page<SystemBanner> bannerPage, Integer status);
}
