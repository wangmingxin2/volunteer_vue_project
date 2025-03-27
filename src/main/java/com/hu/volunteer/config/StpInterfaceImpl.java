package com.hu.volunteer.config;

/**
 * @Author hu
 * @Date 2025/3/25 22:08
 */

import cn.dev33.satoken.stp.StpInterface;
import com.hu.volunteer.pojo.User;
import com.hu.volunteer.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private IUserService userService;
    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> list = new ArrayList<String>();
        list.add("101");
        list.add("user.add");
        list.add("user.update");
        list.add("user.get");
        // list.add("user.delete");
        list.add("art.*");
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
        User user = userService.getById(loginId.toString());
        String userType = user.getTypeId();
        if ( userType.toCharArray()[0] == '1') {
            list.add("admin");
        }else if (userType.toCharArray()[0] == '2') {
            list.add("organize");
        }else {
            list.add("user");
        }
        return list;
    }

}
