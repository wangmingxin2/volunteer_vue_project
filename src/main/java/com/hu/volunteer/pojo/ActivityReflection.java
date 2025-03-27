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
 * 活动心得表
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("activity_reflection")
public class ActivityReflection implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "reflection_id", type = IdType.AUTO)
    private Integer reflectionId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 活动ID
     */
    private Integer activityId;

    /**
     * 心得标题
     */
    private String title;

    /**
     * 心得内容
     */
    private String content;

    /**
     * 图片URL
     */
    private String images;

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 是否公开：0-仅自己可见, 1-公开
     */
    private Integer isPublic;

    /**
     * 审核状态：0-待审核, 1-已通过, 2-已拒绝
     */
    private Integer auditStatus;

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
