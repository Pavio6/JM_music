package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("user_favorite")
@Accessors(chain = true)
public class UserFavorite {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 目标类型
     */
    @TableField("target_type")
    private Integer targetType;
    /**
     * 目标id
     */
    @TableField("target_id")
    private Long targetId;
    /**
     * 收藏时间
     */
    @TableField("collection_time")
    private Date collectionTime;
}
