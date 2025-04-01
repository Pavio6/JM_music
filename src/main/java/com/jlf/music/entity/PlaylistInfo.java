package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.jlf.music.common.enumerate.CreatorType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 歌单信息表
 */
@Data
@Accessors(chain = true)
@TableName("playlist_info")
public class PlaylistInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 歌单ID
     */
    @TableId(value = "playlist_id", type = IdType.AUTO)
    private Long playlistId;

    /**
     * 歌单名称
     */
    @TableField("playlist_name")
    private String playlistName;

    /**
     * 歌单封面
     */
    @TableField("playlist_cover")
    private String playlistCover;

    /**
     * 歌单简介
     */
    @TableField("playlist_bio")
    private String playlistBio;

    /**
     * 创建者类型（USER 或 ADMIN）
     */
    @TableField("creator_type")
    private CreatorType creatorType;

    /**
     * 创建者ID
     * 根据创建者类型关联 user 表或 admin 表
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 歌单播放量
     */
    @TableField("play_count")
    private Long playCount;

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