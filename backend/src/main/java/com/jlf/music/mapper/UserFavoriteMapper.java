package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jlf.music.controller.vo.SimpleItemVo;
import com.jlf.music.controller.vo.SongSimpleInfoVo;
import com.jlf.music.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

    /**
     * 获取用户喜欢歌曲列表
     */
    IPage<SongSimpleInfoVo> findUserFavoriteSongs(@Param("page") Page<SongSimpleInfoVo> page,
                                                  @Param("userId") Long userId);

    IPage<SimpleItemVo> selectUserPlaylistCollect(
            @Param("page") Page<SimpleItemVo> page,
            @Param("userId") Long userId,
            @Param("playlistType") Integer playlistType);

    IPage<SimpleItemVo> selectUserAlbumCollect(
            @Param("page") Page<SimpleItemVo> page,
            @Param("userId") Long userId,
            @Param("albumType") Integer albumType);
}
