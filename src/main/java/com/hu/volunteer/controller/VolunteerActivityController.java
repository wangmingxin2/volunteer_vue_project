package com.hu.volunteer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import com.hu.volunteer.pojo.VolunteerActivity;
import com.hu.volunteer.service.IVolunteerActivityService;
import com.hu.volunteer.utils.Result;
import com.hu.volunteer.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 志愿服务活动表 前端控制器
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */

@CrossOrigin
@RestController
@RequestMapping("/volunteer-activity")
public class VolunteerActivityController {

    @Autowired
    private IVolunteerActivityService volunteerActivityService;

    @PostMapping
    public Result<String> createVolunteerActivity(@RequestBody VolunteerActivity volunteerActivity) {
        if (volunteerActivity == null) {
            return Result.validateFailed("志愿服务活动数据不能为空");
        }
        boolean isSaved = volunteerActivityService.save(volunteerActivity);
        if (isSaved) {
            return Result.success("志愿服务活动创建成功");
        } else {
            return Result.error("志愿服务活动创建失败");
        }
    }

    @GetMapping("/{id}")
    public Result<VolunteerActivity> getVolunteerActivityById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("志愿服务活动ID无效");
        }

        VolunteerActivity volunteerActivity = volunteerActivityService.getById(id);
        if (volunteerActivity != null) {
            return Result.success(volunteerActivity);
        } else {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的志愿服务活动");
        }
    }

    @GetMapping
    public Result<List<VolunteerActivity>> getAllVolunteerActivities() {
        List<VolunteerActivity> volunteerActivities = volunteerActivityService.list();
        return Result.success(volunteerActivities);
    }

    @PutMapping("/{id}")
    public Result<VolunteerActivity> updateVolunteerActivity(@RequestBody VolunteerActivity volunteerActivity) {
        if (volunteerActivity == null) {
            return Result.validateFailed("志愿服务活动数据不能为空");
        }
        boolean isUpdated = volunteerActivityService.updateById(volunteerActivity);
        if (isUpdated) {
            return Result.success(volunteerActivityService.getById(volunteerActivity.getActivityId()));
        } else {
            return Result.error("更新志愿服务活动信息失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteVolunteerActivity(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("志愿服务活动ID无效");
        }

        VolunteerActivity existingVolunteerActivity = volunteerActivityService.getById(id);
        if (existingVolunteerActivity == null) {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的志愿服务活动");
        }

        boolean isRemoved = volunteerActivityService.removeById(id);
        if (isRemoved) {
            return Result.success("志愿服务活动删除成功");
        } else {
            return Result.error("志愿服务活动删除失败");
        }
    }
}