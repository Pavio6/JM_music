package com.jlf.music.controller.dto;

import com.jlf.music.common.enumerate.MessagePermissionType;
import com.jlf.music.common.enumerate.VisibilityType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserPrivacyDTO {
    /**
     * 用户资料权限
     */
    private VisibilityType profileVisibility;
    /**
     * 用户粉丝列表权限
     */
    private VisibilityType followersVisibility;
    /**
     * 用户关注列表权限
     */
    private VisibilityType followingVisibility;
    /**
     * 用户歌单权限
     */
    private VisibilityType playlistVisibility;
    /**
     * 用户私信权限
     */
    private MessagePermissionType messagePermission;
}
