package com.hu.volunteer.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 组织信息表
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("organize_info")
public class OrganizeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "org_id", type = IdType.AUTO)
    private Integer orgId;

    /**
     * 负责人ID，关联用户表user_id
     */
    private Integer managerId;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织总人数
     */
    private Integer count;

    /**
     * 组织描述
     */
    private String description;

    /**
     * 组织Logo URL
     */
    private String logo;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 地址
     */
    private String address;

    /**
     * 成立日期
     */
    private LocalDate establishmentDate;

    /**
     * 状态：0-待审核, 1-正常, 2-已禁用
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
