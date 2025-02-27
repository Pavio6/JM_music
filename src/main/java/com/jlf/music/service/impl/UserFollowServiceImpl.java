package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.enumerate.FollowTargetType;
import com.jlf.music.controller.vo.FollowStatsVo;
import com.jlf.music.controller.vo.SingerFollowsCountVo;
import com.jlf.music.entity.UserFollow;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.UserFollowMapper;
import com.jlf.music.service.UserFollowService;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import static com.jlf.music.common.constant.RedisConstant.*;

@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow>
        implements UserFollowService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 注/取关用户/歌手
     *
     * @param followedId 被关注者id
     * @param isFollow   是否关注
     * @param followType 关注者类型
     * @return Boolean
     */
    @Override
    public Boolean followOrUnfollow(Long followedId, Boolean isFollow, FollowTargetType followType) {
        // 获取登录用户
        Long userId = SecurityUtils.getUserId();
        // 防止用户对自己进行关注
        if (userId.equals(followedId)) {
            throw new ServiceException("用户不能对自己进行关注操作");
        }
        // 根据关注者类型获取key的前缀
        String followsKeyPrefix = getFollowKeyPrefix(followType);
        String followersKeyPrefix = getFollowersKeyPrefix(followType);

        // 关注者的关注集合key
        String followsKey = followsKeyPrefix + userId;
        // 被关注者的粉丝集合key
        String followersKey = followersKeyPrefix + followedId;

        if (isFollow) {
            // 关注 新增数据
            UserFollow userFollow = new UserFollow();
            userFollow.setFollowType(followType.getValue())
                    .setFollowerId(userId) // 关注者id
                    .setFollowedId(followedId); // 被关注者id

            if (this.save(userFollow)) {
                // 将关注的对象id放入redis的set集合中
                stringRedisTemplate.opsForSet().add(followsKey, followedId.toString());
                // 被关注者的粉丝集合
                stringRedisTemplate.opsForSet().add(followersKey, userId.toString());
            }
        } else {
            // 取关 删除数据
            boolean isSuccess = this.remove(new LambdaQueryWrapper<UserFollow>()
                    .eq(UserFollow::getFollowedId, followedId)
                    .eq(UserFollow::getFollowerId, userId)
                    .eq(UserFollow::getFollowType, followType.getValue()));

            if (isSuccess) {
                // 从关注者的关注集合和被关注者的粉丝集合中移除
                stringRedisTemplate.opsForSet().remove(followsKey, followedId.toString());
                // 移除被关注者粉丝集合
                stringRedisTemplate.opsForSet().remove(followersKey, userId.toString());
            } else {
                log.error("取消关注操作失败");
            }
        }
        return true;
    }

    /**
     * 查询用户的关注数和粉丝数
     *
     * @param userId 用户ID
     * @return 包含用户关注数，歌手关注数，粉丝数的结果对象
     */
    @Override
    public FollowStatsVo getFollowAndFollowerCount(Long userId) {
        String followUserKey = getFollowKeyPrefix(FollowTargetType.USER) + userId;
        String followSingerKey = getFollowKeyPrefix(FollowTargetType.SINGER) + userId;
        String userFollowerKey = getFollowersKeyPrefix(FollowTargetType.USER) + userId;
        // 用户关注的用户数量
        Long followUserCount = stringRedisTemplate.opsForSet().size(followUserKey);
        // 用户关注的歌手数量
        Long followSingerCount = stringRedisTemplate.opsForSet().size(followSingerKey);
        // 用户的粉丝数量
        Long followerCount = stringRedisTemplate.opsForSet().size(userFollowerKey);
        // 用户关注数
        if (followUserCount == null) {
            throw new NullPointerException("用户关注用户数为null");
        }
        if (followSingerCount == null) {
            throw new NullPointerException("用户关注歌手数为null");
        }
        // 封装返回对象
        return new FollowStatsVo()
                .setFollowCount(followUserCount + followSingerCount)
                .setFollowerCount(followerCount);
    }

    /**
     * 查询歌手的粉丝数
     *
     * @param singerId 歌手ID
     * @return 粉丝数量
     */
    @Override
    public SingerFollowsCountVo getSingerFollowsCount(Long singerId) {
        String singerFollowerKey = getFollowersKeyPrefix(FollowTargetType.SINGER) + singerId;
        // 返回歌手的粉丝数量
        Long singerFollowsCount = stringRedisTemplate.opsForSet().size(singerFollowerKey);
        return new SingerFollowsCountVo(singerFollowsCount);
    }

    /**
     * 用户关注用户/歌手的keyPrefix
     *
     * @param followType 关注者类型
     * @return String
     */
    private String getFollowKeyPrefix(FollowTargetType followType) {
        if (followType == FollowTargetType.USER) {
            return FOLLOWS_USER_KEY_PREFIX;
        } else if (followType == FollowTargetType.SINGER) {
            return FOLLOWS_SINGER_KEY_PREFIX;
        }
        throw new IllegalArgumentException("Unsupported follow type: " + followType);
    }

    /**
     * 被关注者(用户/歌手)的keyPrefix
     *
     * @param followType 被关注者的类型
     * @return String
     */
    private String getFollowersKeyPrefix(FollowTargetType followType) {
        if (followType == FollowTargetType.USER) {
            return FOLLOWERS_USER_KEY_PREFIX;
        } else if (followType == FollowTargetType.SINGER) {
            return FOLLOWERS_SINGER_KEY_PREFIX;
        }
        throw new IllegalArgumentException("Unsupported follow type: " + followType);
    }
}
