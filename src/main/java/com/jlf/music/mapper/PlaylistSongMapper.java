package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlf.music.entity.PlaylistSong;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlaylistSongMapper extends BaseMapper<PlaylistSong> {

    /**
     * 批量插入歌单歌曲信息
     * @param playlistSongs 歌单歌曲list
     * @return int
     */
    int insertBatch(@Param("list") List<PlaylistSong> playlistSongs);
}
