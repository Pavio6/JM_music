package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jlf.music.controller.vo.PlaylistBasicInfoVo;
import com.jlf.music.entity.PlaylistInfo;
import com.jlf.music.entity.PlaylistSong;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PlaylistSongMapper extends BaseMapper<PlaylistSong> {

    /**
     * 批量插入歌单歌曲信息
     *
     * @param playlistSongs 歌单歌曲list
     * @return int
     */
    int insertBatch(@Param("list") List<PlaylistSong> playlistSongs);

    /**
     * 获取指定歌单中的所有歌曲 IDs，按创建时间降序排列
     *
     * @param sourceId 歌单 ID
     * @return 歌曲 IDs 列表
     */
    @Select("SELECT song_id FROM playlist_song " +
            "WHERE playlist_id = #{sourceId} " +
            "ORDER BY create_time DESC")
    List<Long> getPlaylistSongIds(Long sourceId);

    IPage<PlaylistBasicInfoVo> selectPlaylistByTagType(@Param("page") Page<PlaylistInfo> page,
                                                       @Param("tagType") String valueByName);
}
