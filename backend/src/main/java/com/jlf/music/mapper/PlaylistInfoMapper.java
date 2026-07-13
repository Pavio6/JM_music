package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jlf.music.controller.dto.PlaylistDetailDTO;
import com.jlf.music.controller.qry.PlaylistPageQry;
import com.jlf.music.controller.vo.PlaylistBasicInfoVo;
import com.jlf.music.controller.vo.PlaylistSongVo;
import com.jlf.music.entity.PlaylistInfo;
import com.jlf.music.entity.TagsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface PlaylistInfoMapper extends BaseMapper<PlaylistInfo> {


    /**
     * 获取歌单详细信息
     *
     * @param playlistId 歌单id
     * @return PlaylistDetailDTO
     */
    PlaylistDetailDTO findPlaylistDetail(@Param("playlistId") Long playlistId);

    /**
     * 获取歌单列表
     */
    IPage<PlaylistBasicInfoVo> getPlaylistList(@Param("page") Page<PlaylistBasicInfoVo> page,
                                               @Param("playlistPageQry") PlaylistPageQry playlistPageQry);

    /**
     * 获取歌单拥有的标签列表
     */
    List<TagsInfo> getTags(@Param("playlistId") Long playlistId);

    /**
     * 获取歌单拥有的歌曲列表
     */
    List<PlaylistSongVo> getSongs(@Param("playlistId") Long playlistId);
}
