package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlf.music.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
    /**
     * 获取用户收藏的所有歌曲 IDs，按收藏时间降序排列
     *
     * @param userId 用户 ID
     * @return 歌曲 IDs 列表
     */
    @Select("SELECT target_id FROM user_favorite " +
            "WHERE user_id = #{userId} AND target_type = 0 " +
            "ORDER BY collection_time DESC")
    List<Long> getFavoriteSongIds(Long userId);
}
