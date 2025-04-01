package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 区域信息表
 * 用于存储区域的基本信息，包括区域名称、创建时间、更新时间以及删除标志。
 */
@Data
@Accessors(chain = true)
@TableName("region_info")
public class RegionInfo {

    /**
     * 区域ID，主键，自增
     */
    @TableId(value = "region_id", type = IdType.AUTO)
    private Integer regionId;

    /**
     * 区域名称
     */
    @TableField("region_name")
    private String regionName;

    /**
     * 创建时间，插入时自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间，插入和更新时自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志 (0: 未删除, 1: 已删除)，插入时自动填充
     */
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private Integer deleteFlag;
}