package com.jlf.music.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户粉丝和关注数
 */
@Data
@Accessors(chain = true)
public class FollowStatsVo {
    /**
     * 关注数量
     */
    private Long followCount;

    /**
     * 粉丝数量
     */
    private Long followerCount;
}
