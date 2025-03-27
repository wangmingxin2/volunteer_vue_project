package com.hu.volunteer.pojo;

import java.math.BigDecimal;
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
 * 活动报名表
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("activity_signup")
public class ActivitySignup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "signup_id", type = IdType.AUTO)
    private Integer signupId;

    /**
     * 活动ID
     */
    private Integer activityId;

    /**
     * 志愿者ID
     */
    private Integer userId;

    /**
     * 组织ID
     */
    private Integer orgId;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    private String emergencyPhone;

    /**
     * 报名时间
     */
    private LocalDateTime signupTime;

    /**
     * 审核状态：0-待审核, 1-已通过, 2-已拒绝
     */
    private Integer auditStatus;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核人
     */
    private String auditBy;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 签到时间
     */
    private LocalDateTime signinTime;

    /**
     * 签到地点
     */
    private String signinLocation;

    /**
     * 签退时间
     */
    private LocalDateTime signoutTime;

    /**
     * 服务时长(小时)
     */
    private BigDecimal serviceHours;

    /**
     * 状态：0-已取消, 1-正常
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
