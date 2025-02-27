package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * 歌手信息表
 */
@Data
@Accessors(chain = true)
@TableName("singer_info")
public class SingerInfo implements Serializable  {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "singer_id", type = IdType.AUTO)
    private Long singerId;
    private String singerName;
    private String singerNat;
    private String singerBio;
    private LocalDate singerBirth;
    private LocalDate singerDebutDate;
    private Integer singerSex;
    private String singerAvatar;
    /**
     * 地区id
     */
    private Integer regionId;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;
}
