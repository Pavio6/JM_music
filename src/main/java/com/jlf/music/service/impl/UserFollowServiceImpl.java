package com.jlf.music.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.PageRequest;
import com.jlf.music.common.enumerate.FollowTargetType;
import com.jlf.music.common.enumerate.VisibilityType;
import com.jlf.music.controller.qry.FollowListQry;
import com.jlf.music.controller.vo.*;
import com.jlf.music.entity.SingerInfo;
import com.jlf.music.entity.SysUser;
import com.jlf.music.entity.UserFollow;
import com.jlf.music.entity.UserPrivacy;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.SingerInfoMapper;
import com.jlf.music.mapper.SysUserMapper;
import com.jlf.music.mapper.UserFollowMapper;
import com.jlf.music.mapper.UserPrivacyMapper;
import com.jlf.music.service.UserFollowService;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.hibernate.validator.spi.scripting.ScriptEvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.jlf.music.common.constant.RedisConstant.*;

@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow>
        implements UserFollowService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SingerInfoMapper singerInfoMapper;
    @Resource
    private UserFollowMapper userFollowMapper;
    @Resource
    private UserPrivacyMapper userPrivacyMapper;

    /**
     * 关注/取关用户/歌手
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
        /*String followUserKey = getFollowKeyPrefix(FollowTargetType.USER) + userId;
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
        }*/
        Long followCount = userFollowMapper.selectCount(new LambdaQueryWrapper<UserFollow>()
                .eq(UserFollow::getFollowerId, userId));
        Long followerCount = userFollowMapper.selectCount(new LambdaQueryWrapper<UserFollow>()
                .eq(UserFollow::getFollowedId, userId));
        // 封装返回对象
        return new FollowStatsVo()
                .setFollowCount(followCount)
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
     * 获取用户关注列表
     */
    @Override
    public IPage<SimpleItemVo> getFollowListByUserId(FollowListQry followListQry, Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new ServiceException("该用户不存在");
        }
        UserPrivacy userPrivacy = userPrivacyMapper.selectById(userId);
        if (userPrivacy.getFollowingVisibility().equals(VisibilityType.PRIVATE)) {
            throw new ServiceException("用户关注列表不可见");
        }
        Integer value;
        try {
            FollowTargetType followTargetType = FollowTargetType.valueOf(followListQry.getType());
            value = followTargetType.getValue();
        } catch (Exception e) {
            throw new ServiceException("不合法的枚举类");
        }
        Page<UserFollow> page = new Page<>(followListQry.getPageNum(), followListQry.getPageSize());
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<UserFollow>()
                .eq(UserFollow::getFollowType, value)
                .eq(UserFollow::getFollowerId, userId)
                .orderByDesc(UserFollow::getFollowTime);
        page = userFollowMapper.selectPage(page, wrapper);
        List<Long> followedList = page.getRecords().stream().map(UserFollow::getFollowedId).toList();
        if (CollUtil.isEmpty(followedList)) {
            return null;
        }
        List<SimpleItemVo> list = new ArrayList<>();
        // 如果关注列表查询的是用户
        if (FollowTargetType.USER.getValue().equals(value)) {
            List<SysUser> sysUsers = sysUserMapper.selectBatchIds(followedList);
            list = sysUsers.stream()
                    .map(user -> new SimpleItemVo()
                            .setId(user.getUserId())
                            .setName(user.getUserName())
                            .setCover(user.getUserAvatar()))
                    .toList();
        } else if (FollowTargetType.SINGER.getValue().equals(value)) {
            List<SingerInfo> singerInfos = singerInfoMapper.selectBatchIds(followedList);
            list = singerInfos.stream()
                    .map(singer -> new SimpleItemVo()
                            .setId(singer.getSingerId())
                            .setName(singer.getSingerName())
                            .setCover(singer.getSingerAvatar()))
                    .toList();
        }
        // 返回结果
        return new Page<SimpleItemVo>()
                .setCurrent(page.getCurrent())
                .setPages(page.getPages())
                .setTotal(page.getTotal())
                .setSize(page.getSize())
                .setRecords(list);
    }

    /**
     * 获取用户粉丝列表
     */
    @Override
    public IPage<SimpleItemVo> getFanListByUserId(PageRequest pageRequest, Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new ServiceException("该用户不存在");
        }
        UserPrivacy userPrivacy = userPrivacyMapper.selectById(userId);
        if (userPrivacy.getFollowersVisibility().equals(VisibilityType.PRIVATE)) {
            throw new ServiceException("用户粉丝列表不可见");
        }
        Page<UserFollow> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<UserFollow>()
                .eq(UserFollow::getFollowedId, userId)
                .eq(UserFollow::getFollowType, FollowTargetType.USER.getValue())
                .orderByDesc(UserFollow::getFollowTime);
        page = userFollowMapper.selectPage(page, wrapper);
        List<Long> followerId = page.getRecords().stream().map(UserFollow::getFollowerId).toList();
        if (CollUtil.isEmpty(followerId)) {
            return null;
        }
        List<SysUser> sysUsers = sysUserMapper.selectBatchIds(followerId);
        List<SimpleItemVo> list = sysUsers.stream()
                .map(user -> new SimpleItemVo()
                        .setId(user.getUserId())
                        .setName(user.getUserName())
                        .setCover(user.getUserAvatar()))
                .toList();
        return new Page<SimpleItemVo>()
                .setCurrent(page.getCurrent())
                .setPages(page.getPages())
                .setTotal(page.getTotal())
                .setSize(page.getSize())
                .setRecords(list);
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
