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
 * 志愿服务活动表
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("volunteer_activity")
public class VolunteerActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "activity_id", type = IdType.AUTO)
    private Integer activityId;

    /**
     * 所属组织ID
     */
    private Integer orgId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动类型
     */
    private String activityType;

    /**
     * 活动图片
     */
    private String actibityImg;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动地点
     */
    private String location;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 志愿者名额
     */
    private Integer volunteerQuota;

    /**
     * 已报名人数
     */
    private Integer signedCount;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 状态：0-筹备中, 1-报名中, 2-进行中, 3-已完成, 4-已取消
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
