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
 * 系统公告表
 * </p>
 *
 * @author hujiale
 * @since 2025-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("system_announcement")
public class SystemAnnouncement implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "announcement_id", type = IdType.AUTO)
    private Integer announcementId;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 状态：0-草稿, 1-展示, 2-不展示
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
