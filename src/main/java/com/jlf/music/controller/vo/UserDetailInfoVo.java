package com.jlf.music.controller.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.experimental.Accessors;


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
     * 用户喜欢的歌曲列表
     */
    private IPage<SongBasicInfoVo> favoriteSongsList;
    /**
     *
     */

}
