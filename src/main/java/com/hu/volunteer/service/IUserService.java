package com.hu.volunteer.service;

import com.hu.volunteer.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
public interface IUserService extends IService<User> {

    User getByUsername(String username);
}
