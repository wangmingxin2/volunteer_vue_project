package com.hu.volunteer.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hu.volunteer.dto.UserDto;
import com.hu.volunteer.pojo.User;
import com.hu.volunteer.service.IUserService;
import com.hu.volunteer.utils.Result;
import com.hu.volunteer.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 用户登录
     *
     * @return 登录结果，包含token信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody UserDto userDto) {
        String username = userDto.getUsername();
        String password = userDto.getPassword();
        // 参数校验
        if (username == null || username.trim().isEmpty()) {
            return Result.validateFailed("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.validateFailed("密码不能为空");
        }

        // 查询用户是否存在
        User user = userService.getByUsername(username);
        if (user == null) {
            return Result.error(ResultCode.VALIDATE_FAILED, "用户名或密码错误");
        }

        // 验证密码
        if (!user.getPassword().equals(password)) {
            return Result.error(ResultCode.VALIDATE_FAILED, "用户名或密码错误");
        }
        if (!user.getTypeId().equals(userDto.getTypeId())){
            return Result.error(ResultCode.VALIDATE_FAILED, "用户类型错误");
        }
        if (user.getStatus()!=1){
            return Result.error(ResultCode.VALIDATE_FAILED, "用户已被禁用");
        }
        // 登录成功，使用Sa-Token生成token
        StpUtil.login(user.getUserId());
        String tokenValue = StpUtil.getTokenValue();

        // 获取用户权限并存入Session
        List<String> roles = StpUtil.getRoleList();
        StpUtil.getSession().set("roles", roles);
        System.out.println("roles = " + roles);

        // 组装返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("token", tokenValue);
        data.put("userId", user.getUserId());
        data.put("username", user.getUsername());

        return Result.success(data, "登录成功");
    }

    /**
     * 用户登出
     *
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
            return Result.success("登出成功");
        } else {
            return Result.error(ResultCode.UNAUTHORIZED, "用户未登录");
        }
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getCurrentUserInfo() {
        // 检查用户是否登录
        if (!StpUtil.isLogin()) {
            return Result.error(ResultCode.UNAUTHORIZED, "用户未登录");
        }

        // 获取当前登录用户ID
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString());
        User user = userService.getById(userId);

        if (user != null) {
            // 移除敏感信息
            user.setPassword(null);
            return Result.success(user);
        } else {
            return Result.error(ResultCode.NOT_FOUND, "用户信息不存在");
        }
    }

    /**
     * 检查用户是否登录
     *
     * @return 登录状态
     */
    @GetMapping("/isLogin")
    public Result<Boolean> isLogin() {
        boolean isLogin = StpUtil.isLogin();
        return Result.success(isLogin);
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        if (user == null) {
            return Result.validateFailed("用户数据不能为空");
        }

        // 检查用户名是否已存在
        if (userService.getByUsername(user.getUsername()) != null) {
            return Result.error(ResultCode.CONFLICT, "用户名已存在");
        }

        boolean isSaved = userService.save(user);
        if (isSaved) {
            return Result.success("注册成功");
        } else {
            return Result.error("注册失败");
        }
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @PostMapping("/updatePassword")
    public Result<String> updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        // 检查用户是否登录
        if (!StpUtil.isLogin()) {
            return Result.error(ResultCode.UNAUTHORIZED, "用户未登录");
        }

        // 参数校验
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return Result.validateFailed("旧密码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return Result.validateFailed("新密码不能为空");
        }

        // 获取当前用户
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString());
        User user = userService.getById(userId);

        // 验证旧密码
        if (user.getPassword().equals(oldPassword)) {
            return Result.error(ResultCode.VALIDATE_FAILED, "旧密码不正确");
        }

        // 更新密码
        user.setPassword(newPassword);
        boolean isUpdated = userService.updateById(user);
        if (isUpdated) {
            // 更新成功后强制重新登录
            StpUtil.logout();
            return Result.success("密码修改成功，请重新登录");
        } else {
            return Result.error("密码修改失败");
        }
    }

    /**
     * 创建新用户
     *
     * @param user 用户信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createUser(@RequestBody User user) {
        if (user == null) {
            return Result.validateFailed("用户数据不能为空");
        }
        boolean isSaved = userService.save(user);
        if (isSaved) {
            return Result.success("用户创建成功");
        } else {
            return Result.error("用户创建失败");
        }
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("用户ID无效");
        }

        User user = userService.getById(id);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的用户");
        }
    }


    /**
     * 获取所有用户
     *
     * @return 用户列表
     */
    @GetMapping
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.list();
        return Result.success(users);
    }

    /**
     * 更新用户信息
     *
     * @param user 更新的用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<User> updateUser(@RequestBody User user) {
        if (user == null) {
            return Result.validateFailed("用户数据不能为空");
        }
        boolean isUpdated = userService.updateById(user);
        if (isUpdated) {
            // 返回更新后的用户信息
            return Result.success(userService.getById(user.getUserId()));
        } else {
            return Result.error("更新用户信息失败");
        }

    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {

        if (id == null || id <= 0) {
            return Result.validateFailed("用户ID无效");
        }

        // 检查用户是否存在
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的用户");
        }

        boolean isRemoved = userService.removeById(id);
        if (isRemoved) {
            return Result.success("用户删除成功");
        } else {
            return Result.error("用户删除失败");
        }

    }
}