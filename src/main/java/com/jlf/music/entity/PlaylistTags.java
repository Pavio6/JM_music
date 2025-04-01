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

    /**
     * 歌单ID
     */
    @TableField("playlist_id")
    private Long playlistId;

    /**
     * 标签ID
     */
    @TableField("tag_id")
    private Long tagId;

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
     * 删除标志 (0: 未删除, 1: 已删除)
     */
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private Integer deleteFlag;

    /**
     * 构造方法
     *
     * @param playlistId 歌单ID
     * @param tagId 标签ID
     */
    public PlaylistTags(Long playlistId, Long tagId) {
        this.playlistId = playlistId;
        this.tagId = tagId;
    }
}