package com.hu.volunteer.controller;


import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.hu.volunteer.pojo.ActivitySignup;
import com.hu.volunteer.service.IActivitySignupService;
import com.hu.volunteer.utils.Result;
import com.hu.volunteer.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * <p>
 * 活动报名表 前端控制器
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */

@CrossOrigin
@RestController
@RequestMapping("/signup")
public class ActivitySignupController {

    @Autowired
    private IActivitySignupService activitySignupService;

    /**
     * 创建活动报名
     *
     * @param activitySignup 活动报名信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createActivitySignup(@RequestBody ActivitySignup activitySignup) {
        if (activitySignup == null) {
            return Result.validateFailed("活动报名数据不能为空");
        }
        boolean isSaved = activitySignupService.save(activitySignup);
        if (isSaved) {
            return Result.success("活动报名创建成功");
        } else {
            return Result.error("活动报名创建失败");
        }
    }

    /**
     * 根据ID获取活动报名
     *
     * @param id 活动报名ID
     * @return 活动报名信息
     */
    @GetMapping("/{id}")
    public Result<ActivitySignup> getActivitySignupById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("活动报名ID无效");
        }

        ActivitySignup activitySignup = activitySignupService.getById(id);
        if (activitySignup != null) {
            return Result.success(activitySignup);
        } else {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的活动报名");
        }
    }

    /**
     * 获取所有活动报名
     *
     * @return 活动报名列表
     */
    @GetMapping
    public Result<List<ActivitySignup>> getAllActivitySignups() {
        List<ActivitySignup> activitySignups = activitySignupService.list();
        return Result.success(activitySignups);
    }

    /**
     * 更新活动报名信息
     *
     * @param activitySignup 更新的活动报名信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<ActivitySignup> updateActivitySignup(@RequestBody ActivitySignup activitySignup) {
        if (activitySignup == null) {
            return Result.validateFailed("活动报名数据不能为空");
        }
        boolean isUpdated = activitySignupService.updateById(activitySignup);
        if (isUpdated) {
            // 返回更新后的活动报名信息
            return Result.success(activitySignupService.getById(activitySignup.getSignupId()));
        } else {
            return Result.error("更新活动报名信息失败");
        }
    }

    /**
     * 删除活动报名
     *
     * @param id 活动报名ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteActivitySignup(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("活动报名ID无效");
        }

        // 检查活动报名是否存在
        ActivitySignup existingActivitySignup = activitySignupService.getById(id);
        if (existingActivitySignup == null) {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的活动报名");
        }

        boolean isRemoved = activitySignupService.removeById(id);
        if (isRemoved) {
            return Result.success("活动报名删除成功");
        } else {
            return Result.error("活动报名删除失败");
        }
    }
}