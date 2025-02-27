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
    @TableId(value = "playlist_id", type = IdType.AUTO)
    private Long playlistId;
    private String playlistName;
    private String playlistCover;
    private String playlistBio;
    /**
     * 创建者类型 USER ADMIN
     */
    private CreatorType creatorType;
    /**
     * 创建者ID
     * 根据创建者类型 关联user/admin表
     */
    private Long creatorId;
    /**
     * 歌单播放量
     */
    private Long playCount;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;

}
