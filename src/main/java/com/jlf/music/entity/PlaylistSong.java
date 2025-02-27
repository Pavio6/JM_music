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
 * 歌单-歌曲关联表
 */
@Data
@Accessors(chain = true)
@TableName("playlist_song")
public class PlaylistSong implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long playlistId;
    private Long songId;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;

    public PlaylistSong(Long playlistId, Long songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }
}
