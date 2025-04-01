package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * 歌手信息表
 */
@Data
@Accessors(chain = true)
@TableName("singer_info")
public class SingerInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 歌手ID
     */
    @TableId(value = "singer_id", type = IdType.AUTO)
    private Long singerId;

    /**
     * 歌手姓名
     */
    @TableField("singer_name")
    private String singerName;

    /**
     * 歌手国籍
     */
    @TableField("singer_nat")
    private String singerNat;

    /**
     * 歌手简介
     */
    @TableField("singer_bio")
    private String singerBio;

    /**
     * 歌手出生日期
     */
    @TableField("singer_birth")
    private LocalDate singerBirth;

    /**
     * 歌手出道日期
     */
    @TableField("singer_debut_date")
    private LocalDate singerDebutDate;

    /**
     * 歌手性别（0-未知，1-男，2-女）
     */
    @TableField("singer_sex")
    private Integer singerSex;

    /**
     * 歌手头像
     */
    @TableField("singer_avatar")
    private String singerAvatar;

    /**
     * 地区ID
     */
    @TableField("region_id")
    private Integer regionId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志（0-未删除，1-已删除）
     */
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private Integer deleteFlag;
}