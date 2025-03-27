package com.hu.volunteer.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import com.hu.volunteer.pojo.ActivityReflection;
import com.hu.volunteer.service.IActivityReflectionService;
import com.hu.volunteer.utils.Result;
import com.hu.volunteer.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 活动心得表 前端控制器
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */


@CrossOrigin
@RestController
@RequestMapping("/reflection")
public class ActivityReflectionController {

    @Autowired
    private IActivityReflectionService activityReflectionService;

    /**
     * 创建活动心得
     *
     * @param activityReflection 活动心得信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createActivityReflection(@RequestBody ActivityReflection activityReflection) {
        if (activityReflection == null) {
            return Result.validateFailed("活动心得数据不能为空");
        }
        boolean isSaved = activityReflectionService.save(activityReflection);
        if (isSaved) {
            return Result.success("活动心得创建成功");
        } else {
            return Result.error("活动心得创建失败");
        }
    }

    /**
     * 根据ID获取活动心得
     *
     * @param id 活动心得ID
     * @return 活动心得信息
     */
    @GetMapping("/{id}")
    public Result<ActivityReflection> getActivityReflectionById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("活动心得ID无效");
        }

        ActivityReflection activityReflection = activityReflectionService.getById(id);
        if (activityReflection != null) {
            return Result.success(activityReflection);
        } else {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的活动心得");
        }
    }

    /**
     * 获取所有活动心得
     *
     * @return 活动心得列表
     */
    @GetMapping
    public Result<List<ActivityReflection>> getAllActivityReflections() {
        List<ActivityReflection> activityReflections = activityReflectionService.list();
        return Result.success(activityReflections);
    }

    /**
     * 更新活动心得信息
     *
     * @param activityReflection 更新的活动心得信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<ActivityReflection> updateActivityReflection(@RequestBody ActivityReflection activityReflection) {
        if (activityReflection == null) {
            return Result.validateFailed("活动心得数据不能为空");
        }
        boolean isUpdated = activityReflectionService.updateById(activityReflection);
        if (isUpdated) {
            // 返回更新后的活动心得信息
            return Result.success(activityReflectionService.getById(activityReflection.getReflectionId()));
        } else {
            return Result.error("更新活动心得信息失败");
        }
    }

    /**
     * 删除活动心得
     *
     * @param id 活动心得ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteActivityReflection(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("活动心得ID无效");
        }

        // 检查活动心得是否存在
        ActivityReflection existingActivityReflection = activityReflectionService.getById(id);
        if (existingActivityReflection == null) {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的活动心得");
        }

        boolean isRemoved = activityReflectionService.removeById(id);
        if (isRemoved) {
            return Result.success("活动心得删除成功");
        } else {
            return Result.error("活动心得删除失败");
        }
    }
}