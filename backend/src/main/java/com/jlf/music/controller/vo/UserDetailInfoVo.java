package com.jlf.music.controller.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.enumerate.MessagePermissionType;
import com.jlf.music.common.enumerate.VisibilityType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * 用户详细信息
 */
@Data
@Accessors(chain = true)
public class UserDetailInfoVo {
    private Long userId;
    private String userName;
    private String userBio;
    private String userAvatar;
    private FollowStatsVo followStatsVo;
    /**
     * 是否已关注
     */
    private Boolean isFollowed;

    /**
     * 私信权限
     */
    private MessagePermissionType messagePermission;
    /**
     * 歌单可见性
     */
    private VisibilityType playlistVisibility;
    /**
     * 关注列表可见性
     */
    private VisibilityType followingVisibility;
    /**
     * 粉丝列表可见性
     */
    private VisibilityType followersVisibility;
    /**
     * 用户喜欢的歌曲列表
     */
    private List<SongBasicInfoVo> favoriteSongsList;
    /**
     * 用户喜欢的歌单列表
     */
    private List<PlaylistSimpleInfoVo> favoritePlaylistsList;
}
