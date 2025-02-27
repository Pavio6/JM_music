package com.jlf.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.common.PageRequest;
import com.jlf.music.common.enumerate.TargetType;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.UserFavorite;

public interface UserFavoriteService extends IService<UserFavorite> {
    /**
     * 收藏/取消收藏歌曲/歌单/专辑
     *
     * @param targetId     目标id
     * @param isFavorite   是否喜欢
     * @param favoriteType 喜欢类型
     * @return Boolean
     */
    Boolean likeOrDislike(Long targetId, Boolean isFavorite, TargetType favoriteType);

    /**
     * 获取用户的收藏歌曲列表
     *
     * @param userId 用户id
     * @return List<SongBasicInfoVo>
     */
    IPage<SongBasicInfoVo> getFavoriteSongsList(Long userId, PageRequest pageRequest);


}
