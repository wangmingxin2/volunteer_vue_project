package com.hu.volunteer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hu.volunteer.pojo.SystemBanner;
import com.hu.volunteer.service.ISystemBannerService;
import com.hu.volunteer.utils.Result;
import com.hu.volunteer.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 系统轮播图表 前端控制器
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@CrossOrigin
@RestController
@RequestMapping("/banner")
public class SystemBannerController {

    @Autowired
    private ISystemBannerService systemBannerService;

    @PostMapping
    public Result<String> createSystemBanner(@RequestBody SystemBanner systemBanner) {
        if (systemBanner == null) {
            return Result.validateFailed("系统轮播图数据不能为空");
        }
        boolean isSaved = systemBannerService.save(systemBanner);
        if (isSaved) {
            return Result.success("系统轮播图创建成功");
        } else {
            return Result.error("系统轮播图创建失败");
        }
    }

    @GetMapping("/{id}")
    public Result<SystemBanner> getSystemBannerById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("系统轮播图ID无效");
        }

        SystemBanner systemBanner = systemBannerService.getById(id);
        if (systemBanner != null) {
            return Result.success(systemBanner);
        } else {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的系统轮播图");
        }
    }

    @GetMapping
    public Result<List<SystemBanner>> getAllSystemBanners() {
        List<SystemBanner> systemBanners = systemBannerService.list();
        return Result.success(systemBanners);
    }
    /**
     * 分页获取系统横幅列表，按状态过滤
     *
     * @param status 状态
     * @param page 当前页码，默认第一页
     * @param size 每页大小，默认五条
     * @return 分页系统横幅列表
     */
    @GetMapping("/page")
    public Result<IPage<SystemBanner>> getSystemBannerPage(
            @RequestParam Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<SystemBanner> bannerPage = new Page<>(page, size);
        IPage<SystemBanner> bannerIPage = systemBannerService.getPageByStatus(bannerPage, status);
        return Result.success(bannerIPage);
    }
    @PutMapping("/{id}")
    public Result<SystemBanner> updateSystemBanner(@RequestBody SystemBanner systemBanner) {
        if (systemBanner == null) {
            return Result.validateFailed("系统轮播图数据不能为空");
        }
        boolean isUpdated = systemBannerService.updateById(systemBanner);
        if (isUpdated) {
            return Result.success(systemBannerService.getById(systemBanner.getBannerId()));
        } else {
            return Result.error("更新系统轮播图信息失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteSystemBanner(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("系统轮播图ID无效");
        }

        SystemBanner existingSystemBanner = systemBannerService.getById(id);
        if (existingSystemBanner == null) {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的系统轮播图");
        }

        boolean isRemoved = systemBannerService.removeById(id);
        if (isRemoved) {
            return Result.success("系统轮播图删除成功");
        } else {
            return Result.error("系统轮播图删除失败");
        }
    }
}