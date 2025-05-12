package com.jlf.music.controller.web.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.PageRequest;
import com.jlf.music.common.Result;
import com.jlf.music.common.enumerate.FollowTargetType;
import com.jlf.music.controller.qry.FollowListQry;
import com.jlf.music.controller.vo.FollowStatsVo;
import com.jlf.music.controller.vo.SimpleItemVo;
import com.jlf.music.controller.vo.SingerFollowsCountVo;
import com.jlf.music.service.UserFollowService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 用户关注
 */
@RestController
@RequestMapping("/api/user/follow")
public class UserFollowController {
    @Resource
    private UserFollowService userFollowService;

    /**
     * 关注/取关用户/歌手
     *
     * @param followedId 被关注者id
     * @param isFollow   是否关注
     * @param followType 关注者类型 user / singer
     * @return Boolean
     */
    @PostMapping("/{followedId}/{isFollow}")
    public Result<Boolean> followOrUnfollow(@PathVariable("followedId") Long followedId,
                                            @PathVariable("isFollow") Boolean isFollow,
                                            FollowTargetType followType) {
        return Result.success(userFollowService.followOrUnfollow(followedId, isFollow, followType));
    }

    /**
     * 查询用户的关注数和粉丝数
     *
     * @param userId 用户ID
     * @return 包含用户关注数，歌手关注数，粉丝数的结果对象
     */
    @GetMapping("/stats")
    public Result<FollowStatsVo> getUserFollowStats(@RequestParam Long userId) {
        return Result.success(userFollowService.getFollowAndFollowerCount(userId));
    }

    /**
     * TODO 方法编写的位置需要改变  后续处理
     * 查询歌手的粉丝数
     *
     * @param singerId 歌手ID
     * @return 粉丝数量
     */
    @GetMapping("/singer/stats")
    public Result<SingerFollowsCountVo> getSingerFollowsCount(@RequestParam Long singerId) {
        return Result.success(userFollowService.getSingerFollowsCount(singerId));
    }

    /**
     * 获取用户的关注列表
     */
    @GetMapping("/list/{userId}")
    public Result<IPage<SimpleItemVo>> getFollowListByUserId(FollowListQry followListQry, @PathVariable Long userId) {
        return Result.success(userFollowService.getFollowListByUserId(followListQry, userId));
    }

    /**
     * 获取用户的粉丝列表
     */
    @GetMapping("/fan/list/{userId}")
    public Result<IPage<SimpleItemVo>> getFanListByUserId(PageRequest pageRequest, @PathVariable Long userId) {
        return Result.success(userFollowService.getFanListByUserId(pageRequest, userId));
    }
}
