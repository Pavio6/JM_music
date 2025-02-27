package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * 专辑信息表
 */
@Data
@Accessors(chain = true)
@TableName("album_info")
public class AlbumInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "album_id", type = IdType.AUTO)
    private Long albumId;
    private String albumName;
    private LocalDate albumReleaseDate;
    private String albumCover;
    private String albumBio;
    private Long typeId;
    private Long singerId;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;

}
