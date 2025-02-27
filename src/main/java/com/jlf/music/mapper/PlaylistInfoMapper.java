package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlf.music.controller.dto.PlaylistDetailDTO;
import com.jlf.music.entity.PlaylistInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface PlaylistInfoMapper extends BaseMapper<PlaylistInfo> {


    /**
     * 获取歌单详细信息
     * @param playlistId 歌单id
     * @return PlaylistDetailDTO
     */
    PlaylistDetailDTO findPlaylistDetail(@Param("playlistId") Long playlistId);
}
