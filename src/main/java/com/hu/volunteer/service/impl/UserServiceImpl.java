package com.hu.volunteer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hu.volunteer.pojo.User;
import com.hu.volunteer.mapper.UserMapper;
import com.hu.volunteer.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public User getByUsername(String username) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
    }
}
