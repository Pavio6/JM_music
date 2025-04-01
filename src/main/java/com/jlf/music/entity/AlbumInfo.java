package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * 专辑信息表
 */
@Data
@Accessors(chain = true)
@TableName("album_info")
@AllArgsConstructor
@NoArgsConstructor
public class AlbumInfo implements Serializable {
    @TableId(value = "album_id", type = IdType.AUTO)
    private Long albumId;

    @TableField("album_name")
    private String albumName;

    @TableField("album_release_date")
    private LocalDate albumReleaseDate;

    @TableField("album_cover")
    private String albumCover;

    @TableField("album_bio")
    private String albumBio;

    @TableField("type_id")
    private Long typeId;

    @TableField("singer_id")
    private Long singerId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private Integer deleteFlag;

}
