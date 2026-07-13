package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Description 轮播信息实体类
 * @Author JLF
 * @Date 2025/4/29 17:09
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("carousel_info")
public class CarouselInfo {
    /**
     * 轮播图ID
     */
    @TableId(value = "carousel_id", type = IdType.AUTO)
    private Long carouselId;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 副标题
     */
    @TableField("sub_title")
    private String subTitle;

    /**
     * 图片URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 目标类型 0-SONG 1-PLAYLIST 2-ALBUM 3-ARTICLE 4-EXTERNAL_LINK
     */
    @TableField("target_type")
    private Integer targetType;

    /**
     * 目标ID
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 外部链接URL
     */
    @TableField("external_url")
    private String externalUrl;

    /**
     * 排序顺序(数字越小越靠前)
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 展示开始时间
     */
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 展示结束时间
     */
    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 状态 0-下线 1-上线
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 创建者ID
     */
    @TableField("create_by")
    private Long createBy;
}
