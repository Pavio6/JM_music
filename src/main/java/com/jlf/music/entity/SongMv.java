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
 * 歌曲-MV表
 */
@Data
@Accessors(chain = true)
@TableName("song_mv")
public class SongMv implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "mv_id", type = IdType.AUTO)
    private Long mvId;
    private String mvFilePath480p;
    private String mvFilePath720p;
    private String mvFilePath1080p;
    private LocalTime mvDuration;
    private LocalDate mvReleaseDate;
    private String mvBio;
    private Long songId;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;

}
