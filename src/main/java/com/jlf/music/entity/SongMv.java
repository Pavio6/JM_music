package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * 歌曲-MV表
 */
@Data
@Accessors(chain = true)
@TableName("song_mv")
public class SongMv implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * MV的ID，主键，自增
     */
    @TableId(value = "mv_id", type = IdType.AUTO)
    private Long mvId;

    /**
     * 480p(标清) 文件路径
     */
    @TableField("mv_file_path_480p")
    private String mvFilePath480p;

    /**
     * 720p(高清) 文件路径
     */
    @TableField("mv_file_path_720p")
    private String mvFilePath720p;

    /**
     * 1080p(超清) 文件路径
     */
    @TableField("mv_file_path_1080p")
    private String mvFilePath1080p;

    /**
     * MV发行日期
     */
    @TableField("mv_release_date")
    private LocalDate mvReleaseDate;

    /**
     * MV简介
     */
    @TableField("mv_bio")
    private String mvBio;

    /**
     * 歌曲ID，外键关联歌曲信息表
     */
    @TableField("song_id")
    private Long songId;

    /**
     * 创建时间，插入时自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间，插入和更新时自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志: 0 未删除 1 已删除，插入时自动填充默认值 0
     */
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private Integer deleteFlag;
}