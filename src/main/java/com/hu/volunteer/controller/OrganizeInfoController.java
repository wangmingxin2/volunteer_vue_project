package com.hu.volunteer.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hu.volunteer.pojo.OrganizeInfo;
import com.hu.volunteer.service.IOrganizeInfoService;
import com.hu.volunteer.utils.Result;
import com.hu.volunteer.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 组织信息表 前端控制器
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */

@CrossOrigin
@RestController
@RequestMapping("/organize")
public class OrganizeInfoController {

    @Autowired
    private IOrganizeInfoService organizeInfoService;

    /**
     * 创建组织信息
     *
     * @param organizeInfo 组织信息
     * @return 创建结果
     */
    @PostMapping
    public Result<String> createOrganizeInfo(@RequestBody OrganizeInfo organizeInfo) {
        if (organizeInfo == null) {
            return Result.validateFailed("组织信息数据不能为空");
        }
        boolean isSaved = organizeInfoService.save(organizeInfo);
        if (isSaved) {
            return Result.success("组织信息创建成功");
        } else {
            return Result.error("组织信息创建失败");
        }
    }

    /**
     * 根据ID获取组织信息
     *
     * @param id 组织信息ID
     * @return 组织信息
     */
    @GetMapping("/{id}")
    public Result<OrganizeInfo> getOrganizeInfoById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("组织信息ID无效");
        }

        OrganizeInfo organizeInfo = organizeInfoService.getById(id);
        if (organizeInfo != null) {
            return Result.success(organizeInfo);
        } else {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的组织信息");
        }
    }

    /**
     * 获取所有组织信息
     *
     * @return 组织信息列表
     */
    @GetMapping
    public Result<List<OrganizeInfo>> getAllOrganizeInfos() {
        List<OrganizeInfo> organizeInfos = organizeInfoService.list();
        return Result.success(organizeInfos);
    }
    /**
     * 获取所有组织信息
     *
     * @return 组织信息列表
     */
    @GetMapping("/top5")
    public Result<List<OrganizeInfo>> getTop5OrganizeInfos() {
        List<OrganizeInfo> organizeInfos = organizeInfoService.listTop5();
        return Result.success(organizeInfos);
    }
    /**
     * 分页获取组织信息列表
     *
     * @param page 当前页码
     * @param size 每页大小
     * @return 分页组织信息列表
     */
    @GetMapping("/page")
    public Result<IPage<OrganizeInfo>> getOrganizeInfoPage(@RequestParam int page, @RequestParam int size) {
        Page<OrganizeInfo> organizeInfoPage = new Page<>(page, size);
        IPage<OrganizeInfo> organizeInfoIPage = organizeInfoService.page(organizeInfoPage);
        return Result.success(organizeInfoIPage);
    }
    /**
     * 更新组织信息
     *
     * @param organizeInfo 更新的组织信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<OrganizeInfo> updateOrganizeInfo(@RequestBody OrganizeInfo organizeInfo) {
        if (organizeInfo == null) {
            return Result.validateFailed("组织信息数据不能为空");
        }
        boolean isUpdated = organizeInfoService.updateById(organizeInfo);
        if (isUpdated) {
            // 返回更新后的组织信息
            return Result.success(organizeInfoService.getById(organizeInfo.getOrgId()));
        } else {
            return Result.error("更新组织信息失败");
        }
    }

    /**
     * 删除组织信息
     *
     * @param id 组织信息ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteOrganizeInfo(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.validateFailed("组织信息ID无效");
        }

        // 检查组织信息是否存在
        OrganizeInfo existingOrganizeInfo = organizeInfoService.getById(id);
        if (existingOrganizeInfo == null) {
            return Result.error(ResultCode.NOT_FOUND, "未找到ID为" + id + "的组织信息");
        }

        boolean isRemoved = organizeInfoService.removeById(id);
        if (isRemoved) {
            return Result.success("组织信息删除成功");
        } else {
            return Result.error("组织信息删除失败");
        }
    }
}
