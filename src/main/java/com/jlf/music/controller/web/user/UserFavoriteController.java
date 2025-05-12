package com.jlf.music.controller.web.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.PageRequest;
import com.jlf.music.common.Result;
import com.jlf.music.common.enumerate.TargetType;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.service.UserFavoriteService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 用户喜欢/收藏
 */
@RestController
@RequestMapping("/api/user/favorite")
public class UserFavoriteController {
    @Resource
    private UserFavoriteService userFavoriteService;

    /**
     * 收藏/取消收藏歌曲/歌单/专辑
     *
     * @param targetId     目标id
     * @param isFavorite   是否喜欢
     * @param favoriteType 喜欢类型
     * @return Boolean
     */
    @PostMapping("/{targetId}/{isFavorite}")
    public Result<Boolean> likeOrDislike(@PathVariable("targetId") Long targetId,
                                         @PathVariable("isFavorite") Boolean isFavorite,
                                         @RequestParam(value = "favoriteType") TargetType favoriteType) {
        return Result.success(userFavoriteService.likeOrDislike(targetId, isFavorite, favoriteType));
    }

    /**
     * 获取用户的收藏歌曲列表
     *
     * @param userId 用户id
     * @return List<SongBasicInfoVo>
     */
    @GetMapping("/{userId}/list")
    public Result<IPage<SongBasicInfoVo>> getFavoriteSongsList(@PathVariable("userId") Long userId,
                                                               PageRequest pageRequest) {
        return Result.success(userFavoriteService.getFavoriteSongsList(userId, pageRequest));
    }

    /**
     * 获取用户个人收藏歌曲列表
     */
    @GetMapping("/mine")
    public Result<IPage<SongBasicInfoVo>> getMineFavoriteSongsList(PageRequest pageRequest) {
        return Result.success(userFavoriteService.getMineFavoriteSongsList(pageRequest));
    }
}
