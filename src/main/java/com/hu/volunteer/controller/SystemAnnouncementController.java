package com.hu.volunteer.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hu.volunteer.pojo.SystemAnnouncement;
import com.hu.volunteer.service.ISystemAnnouncementService;
import com.hu.volunteer.utils.Result;
import com.hu.volunteer.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * <p>
 * 系统公告表 前端控制器
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@CrossOrigin
@RestController
@RequestMapping("/announcement")
public class SystemAnnouncementController {

    @Autowired
    private ISystemAnnouncementService systemAnnouncementService;

    @PostMapping
    @SaCheckRole("admin")
    public Result<String> createSystemAnnouncement(@RequestBody SystemAnnouncement systemAnnouncement) {
        if (systemAnnouncement == null) {
            return Result.validateFailed("系统公告数据不能为空");
        }
        boolean isSaved = systemAnnouncementService.save(systemAnnouncement);
        if (isSaved) {
            return Result.success("系统公告创建成功");
        } else {
            return Result.error("系统公告创建失败");
        }
    }

    @GetMapping("/{id}")
    public Result<SystemAnnouncement> getSystemAnnouncementById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("系统公告ID无效");
        }

        SystemAnnouncement systemAnnouncement = systemAnnouncementService.getById(id);
        if (systemAnnouncement != null) {
            return Result.success(systemAnnouncement);
        } else {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的系统公告");
        }
    }

    @GetMapping
    public Result<List<SystemAnnouncement>> getAllSystemAnnouncements() {
        List<SystemAnnouncement> systemAnnouncements = systemAnnouncementService.list();
        return Result.success(systemAnnouncements);
    }
    /**
     * 分页获取系统公告列表，按状态过滤
     *
     * @param status 状态
     * @param page 当前页码，默认第一页
     * @param size 每页大小，默认五条
     * @return 分页系统公告列表
     */
    @GetMapping("/page")
    public Result<IPage<SystemAnnouncement>> getSystemAnnouncementPage(
            @RequestParam Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<SystemAnnouncement> announcementPage = new Page<>(page, size);
        IPage<SystemAnnouncement> announcementIPage = systemAnnouncementService.getPageByStatus(announcementPage, status);
        return Result.success(announcementIPage);
    }
    @PutMapping("/{id}")
    @SaCheckRole("admin")
    public Result<SystemAnnouncement> updateSystemAnnouncement(@RequestBody SystemAnnouncement systemAnnouncement) {
        if (systemAnnouncement == null) {
            return Result.validateFailed("系统公告数据不能为空");
        }
        boolean isUpdated = systemAnnouncementService.updateById(systemAnnouncement);
        if (isUpdated) {
            return Result.success(systemAnnouncementService.getById(systemAnnouncement.getAnnouncementId()));
        } else {
            return Result.error("更新系统公告信息失败");
        }
    }

    @DeleteMapping("/{id}")
    @SaCheckRole("admin")
    public Result<String> deleteSystemAnnouncement(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("系统公告ID无效");
        }

        SystemAnnouncement existingSystemAnnouncement = systemAnnouncementService.getById(id);
        if (existingSystemAnnouncement == null) {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的系统公告");
        }

        boolean isRemoved = systemAnnouncementService.removeById(id);
        if (isRemoved) {
            return Result.success("系统公告删除成功");
        } else {
            return Result.error("系统公告删除失败");
        }
    }
}