package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class PlaylistSong implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 歌单ID
     */
    @TableField("playlist_id")
    private Long playlistId;

    /**
     * 歌曲ID
     */
    @TableField("song_id")
    private Long songId;

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
     * 构造函数
     *
     * @param playlistId 歌单ID
     * @param songId     歌曲ID
     */
    public PlaylistSong(Long playlistId, Long songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }
}