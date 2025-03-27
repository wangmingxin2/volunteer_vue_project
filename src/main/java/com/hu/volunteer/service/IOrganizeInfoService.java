package com.hu.volunteer.service;

import com.hu.volunteer.pojo.OrganizeInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 组织信息表 服务类
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
public interface IOrganizeInfoService extends IService<OrganizeInfo> {

    List<OrganizeInfo> listTop5();
}
