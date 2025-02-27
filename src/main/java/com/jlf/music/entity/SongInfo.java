package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * 歌曲信息表
 */
@Data
@Accessors(chain = true)
@TableName("song_info")
public class SongInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "song_id", type = IdType.AUTO)
    private Long songId;
    private String songName;
    private LocalTime songDuration;
    private String songLyrics;
    private LocalDate songReleaseDate;
    private String songFilePath;
    private String songCover;
    private Long singerId;
    private Long albumId;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Byte deleteFlag;
}
