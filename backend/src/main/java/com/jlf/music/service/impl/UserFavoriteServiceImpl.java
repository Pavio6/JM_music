package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.PageRequest;
import com.jlf.music.common.enumerate.TargetType;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.UserFavorite;
import com.jlf.music.mapper.SongInfoMapper;
import com.jlf.music.mapper.UserFavoriteMapper;
import com.jlf.music.service.UserFavoriteService;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.jlf.music.common.constant.RedisConstant.*;

@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite>
        implements UserFavoriteService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SongInfoMapper songInfoMapper;

    /**
     * 收藏/取消收藏歌曲/歌单/专辑
     *
     * @param targetId     目标id
     * @param isFavorite   是否喜欢
     * @param favoriteType 喜欢类型
     * @return Boolean
     */
    @Override
    public Boolean likeOrDislike(Long targetId, Boolean isFavorite, TargetType favoriteType) {
        // 获取登录用户id
        Long userId = SecurityUtils.getUserId();
        // 根据收藏类型获取redis存储的key prefix
        String favoriteKeyPrefix = getFavoriteKeyPrefix(favoriteType);
        // 用户收藏的key
        String favoriteKey = favoriteKeyPrefix + userId;
        // 收藏/喜欢
        if (isFavorite) {
            UserFavorite userFavorite = new UserFavorite();
            userFavorite.setUserId(userId)
                    .setTargetId(targetId)
                    .setTargetType(favoriteType.getValue());
            if (this.save(userFavorite)) {
                // 将收藏的歌曲/歌单/专辑id存放到redis的set集合中
                stringRedisTemplate.opsForSet().add(favoriteKey, targetId.toString());
            }
            // 取消收藏/喜欢
        } else {
            boolean isSuccess = this.remove(new LambdaQueryWrapper<UserFavorite>()
                    .eq(UserFavorite::getUserId, userId)
                    .eq(UserFavorite::getTargetId, targetId)
                    .eq(UserFavorite::getTargetType, favoriteType.getValue()));
            if (isSuccess) {
                stringRedisTemplate.opsForSet().remove(favoriteKey, targetId.toString());
            } else {
                log.error("歌曲/歌单/专辑取消收藏操作失败");
            }
        }
        return true;
    }

    /**
     * 获取用户的收藏歌曲列表
     *
     * @param userId 用户id
     * @return List<SongBasicInfoVo>
     */
    @Override
    public IPage<SongBasicInfoVo> getFavoriteSongsList(Long userId, PageRequest pageRequest) {
        Page<SongBasicInfoVo> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        List<UserFavorite> list = this.list(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTargetType, TargetType.SONG));
        // 获取歌曲id列表
        List<Long> songIds = list.stream()
                .map(UserFavorite::getTargetId)
                .filter(Objects::nonNull)
                .toList();
        return songInfoMapper.getFavoriteSongs(page, songIds);
    }

    /**
     * 获取用户个人收藏歌曲列表
     */
    @Override
    public IPage<SongBasicInfoVo> getMineFavoriteSongsList(PageRequest pageRequest) {
        Long userId = SecurityUtils.getUserId();
        Page<SongBasicInfoVo> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        List<Long> songIds = this.list(new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getTargetType, TargetType.SONG))
                .stream()
                .map(UserFavorite::getTargetId)
                .filter(Objects::nonNull)
                .toList();
        return songInfoMapper.getFavoriteSongs(page, songIds);
    }
//    // 获取登录用户
//    Long userId = UserHolder.getUser().getUserId();
//    // 防止用户对自己进行关注
//        if (userId.equals(followedId)) {
//        throw new ServiceException("用户不能对自己进行关注操作");
//    }
//    // 根据关注者类型获取key的前缀
//    String followsKeyPrefix = getFollowKeyPrefix(followType);
//    String followersKeyPrefix = getFollowersKeyPrefix(followType);
//
//    // 关注者的关注集合key
//    String followsKey = followsKeyPrefix + userId;
//    // 被关注者的粉丝集合key
//    String followersKey = followersKeyPrefix + followedId;
//
//        if (isFollow) {
//        // 关注 新增数据
//        UserFollow userFollow = new UserFollow();
//        userFollow.setFollowType(FollowTargetType.getCodeByName(followType))
//                .setFollowerId(userId) // 关注者id
//                .setFollowedId(followedId); // 被关注者id
//
//        if (this.save(userFollow)) {
//            // 将关注的对象id放入redis的set集合中
//            stringRedisTemplate.opsForSet().add(followsKey, followedId.toString());
//            // 被关注者的粉丝集合
//            stringRedisTemplate.opsForSet().add(followersKey, userId.toString());
//        }
//    } else {
//        // 取关 删除数据
//        boolean isSuccess = this.remove(new LambdaQueryWrapper<UserFollow>()
//                .eq(UserFollow::getFollowedId, followedId)
//                .eq(UserFollow::getFollowerId, userId)
//                .eq(UserFollow::getFollowType, FollowTargetType.getCodeByName(followType)));
//
//        if (isSuccess) {
//            // 从关注者的关注集合和被关注者的粉丝集合中移除
//            stringRedisTemplate.opsForSet().remove(followsKey, followedId.toString());
//            // 移除被关注者粉丝集合
//            stringRedisTemplate.opsForSet().remove(followersKey, userId.toString());
//        } else {
//            log.error("删除的数据不存在");
//        }
//    }
//        return true;

    /**
     * 用户收藏歌曲/歌单/转的keyPrefix
     *
     * @param favoriteType 收藏的类型
     * @return String
     */
    private String getFavoriteKeyPrefix(TargetType favoriteType) {
        if (favoriteType == TargetType.SONG) return FAVORITE_SONG_KEY_PREFIX;
        else if ((favoriteType == TargetType.PLAYLIST)) return FAVORITE_PLAYLIST_KEY_PREFIX;
        else if ((favoriteType == TargetType.ALBUM)) return FAVORITE_ALBUM_KEY_PREFIX;
        throw new IllegalArgumentException("Unsupported favorite type: " + favoriteType);
    }
}
