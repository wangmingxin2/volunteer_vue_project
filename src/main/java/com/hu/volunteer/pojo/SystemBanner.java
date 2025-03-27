package com.hu.volunteer.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系统轮播图表
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("system_banner")
public class SystemBanner implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "banner_id", type = IdType.AUTO)
    private Integer bannerId;

    /**
     * 轮播图标题
     */
    private String title;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 轮播图描述
     */
    private String description;

    /**
     * 轮播图类型：0-系统公告, 1-活动宣传, 2-组织宣传
     */
    private Integer bannerType;

    /**
     * 状态：0-禁用, 1-启用
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;


}
