package com.jlf.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.common.enumerate.FollowTargetType;
import com.jlf.music.controller.vo.FollowStatsVo;
import com.jlf.music.controller.vo.SingerFollowsCountVo;
import com.jlf.music.entity.UserFollow;

public interface UserFollowService extends IService<UserFollow> {
    /**
     * 关注/取关用户/歌手
     * @param followedId 被关注者id
     * @param isFollow 是否关注
     * @param followType 关注者类型
     * @return Boolean
     */
    Boolean followOrUnfollow(Long followedId, Boolean isFollow, FollowTargetType followType);

    /**
     * 查询用户的关注数和粉丝数
     *
     * @param userId 用户ID
     * @return 包含用户关注数，歌手关注数，粉丝数的结果对象
     */
    FollowStatsVo getFollowAndFollowerCount(Long userId);

    /**
     * 查询歌手的粉丝数
     *
     * @param singerId 歌手ID
     * @return 粉丝数量
     */
    SingerFollowsCountVo getSingerFollowsCount(Long singerId);

}
