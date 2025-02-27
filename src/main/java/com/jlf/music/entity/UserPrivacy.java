package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.jlf.music.common.enumerate.MessagePermissionType;
import com.jlf.music.common.enumerate.VisibilityType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 用户隐私表
 */
@TableName("user_privacy")
@Data
@Accessors(chain = true)
public class UserPrivacy {
    @TableId(value = "user_id", type = IdType.NONE)
    private Long userId;
    /**
     * 用户资料权限
     */
    @TableField(value = "profile_visibility")
    private VisibilityType profileVisibility;
    /**
     * 用户粉丝列表权限
     */
    @TableField(value = "followers_visibility")
    private VisibilityType followersVisibility;
    /**
     * 用户关注列表权限
     */
    @TableField(value = "following_visibility")
    private VisibilityType followingVisibility;
    /**
     * 用户歌单权限
     */
    @TableField(value = "playlist_visibility")
    private VisibilityType playlistVisibility;
    /**
     * 用户私信权限
     */
    @TableField(value = "message_permission")
    private MessagePermissionType messagePermission;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;
}
