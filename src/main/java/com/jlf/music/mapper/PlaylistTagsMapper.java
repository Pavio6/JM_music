package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlf.music.entity.PlaylistTags;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlaylistTagsMapper extends BaseMapper<PlaylistTags> {
    /**
     * 批量插入歌单-标签list
     * @param playlistTags 歌单-标签list
     */
    void insertBatch(@Param("playlistTags") List<PlaylistTags> playlistTags);
}
