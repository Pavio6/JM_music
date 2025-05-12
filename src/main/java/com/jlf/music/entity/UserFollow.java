package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 用户关注表
 */
@TableName("user_follow")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserFollow {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 关注者id
     */
    @TableField("follower_id")
    private Long followerId;
    /**
     * 被关注者id
     */
    @TableField("followed_id")
    private Long followedId;
    /**
     * 关注者类型
     */
    @TableField("follow_type")
    private Integer followType;
    /**
     * 关注时间
     */
    @TableField("follow_time")
    private Date followTime;
}
