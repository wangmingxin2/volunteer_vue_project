package com.hu.volunteer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hu.volunteer.pojo.OrganizeInfo;
import com.hu.volunteer.mapper.OrganizeInfoMapper;
import com.hu.volunteer.service.IOrganizeInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 组织信息表 服务实现类
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@Service
public class OrganizeInfoServiceImpl extends ServiceImpl<OrganizeInfoMapper, OrganizeInfo> implements IOrganizeInfoService {
    @Autowired
    private OrganizeInfoMapper mapper;
    @Override
    public List<OrganizeInfo> listTop5() {
        QueryWrapper<OrganizeInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("count").last("LIMIT 5");
        return baseMapper.selectList(queryWrapper);
    }
}
