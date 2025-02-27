package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 歌单-标签关联表
 */
@Data
@Accessors(chain = true)
@TableName("playlist_tags")
public class PlaylistTags implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long playlistId;
    private Long tagId;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;

    public PlaylistTags(Long playlistId, Long tagId) {
        this.playlistId = playlistId;
        this.tagId = tagId;
    }
}
