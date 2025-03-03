package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jlf.music.controller.qry.SongQry;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.controller.vo.SongSimpleInfoVo;
import com.jlf.music.entity.SongInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SongInfoMapper extends BaseMapper<SongInfo> {
    /**
     * 分页查询歌曲信息
     *
     * @param page    分页page
     * @param songQry 分页参数
     * @return SongVo
     */
    IPage<SongBasicInfoVo> getSongsByPage(IPage<SongInfo> page, @Param("songQry") SongQry songQry);

    @Select({
            "SELECT " +
                    "   s.song_id AS songId, " +
                    "   s.song_name AS songName, " +
                    "   s.song_duration AS songDuration, " +
                    "   s.song_cover AS songCover, " +
                    "   si.singer_name AS singerName, " +
                    "   ai.album_name AS albumName " +
                    "FROM " +
                    "   song_info s " +
                    "LEFT JOIN " +
                    "   singer_info si ON s.singer_id = si.singer_id " +
                    "LEFT JOIN " +
                    "   album_info ai ON s.album_id = ai.album_id " +
                    "WHERE " +
                    "   s.delete_flag = 0 and s.album_id = #{albumId}"
    })
    List<SongBasicInfoVo> getSongsByAlbumId(@Param("albumId") Long albumId);

    /**
     * 查询最近歌曲列表
     * DESC(Descending) 降序
     */
    @Select("SELECT " +
            "s.song_id AS songId, " +
            "s.song_name AS songName, " +
            "s.song_duration AS songDuration, " +
            "s.song_cover AS songCover, " +
            "si.singer_name AS singerName " +
            "FROM song_info s " +
            "LEFT JOIN singer_info si ON s.singer_id = si.singer_id " +
            "WHERE s.delete_flag = 0 " +
            "ORDER BY s.create_time DESC " +
            "LIMIT 2")
    List<SongBasicInfoVo> findLatestSongs();

    /**
     * 分页查询用户喜欢歌曲列表
     *
     * @param page    分页参数
     * @param songIds 歌曲ids
     * @return IPage<SongBasicInfoVo>
     */
    IPage<SongBasicInfoVo> getFavoriteSongs(Page<SongBasicInfoVo> page, @Param("songIds") List<Long> songIds);

    List<SongSimpleInfoVo> searchSongs(@Param("keyword") String keyword, @Param("limit") Integer limit);

    /**
     * 获取播放列表歌曲信息
     * @param songIds 歌曲ids
     * @return SongSimpleInfoVo
     */
    List<SongSimpleInfoVo> selectSongDetails(@Param("songIds") List<Long> songIds);

    /**
     * 获取专辑中的所有歌曲 按歌曲插入时间降序
     *
     * @param sourceId 专辑id
     * @return 歌曲ids
     */
    @Select("SELECT song_id FROM song_info WHERE album_id = #{sourceId} AND delete_flag = 0 ORDER BY create_time DESC ")
    List<Long> getAlbumSongIds(@Param("sourceId") Long sourceId);

    /**
     * 获取热歌榜
     *
     * @return 歌曲列表
     */
    List<SongBasicInfoVo> selectHotSongs();

    /**
     * 获取新歌榜
     */
    @Select("SELECT s.song_id AS songId, s.song_name AS songName, s.song_duration AS songDuration, " +
            "s.song_cover AS songCover, s.song_release_date AS songReleaseDate, " +
            "si.singer_id as singerId, si.singer_name AS singerName,a.album_id as albumId, a.album_name AS albumName " +
            "FROM song_info s " +
            "LEFT JOIN singer_info si ON s.singer_id = si.singer_id AND si.delete_flag = 0 " +
            "LEFT JOIN album_info a ON s.album_id = a.album_id AND a.delete_flag = 0 " +
            "WHERE s.song_release_date >= #{startTime} AND s.delete_flag = 0 " +
            "ORDER BY s.song_release_date DESC " +
            "LIMIT #{limit}")
    List<SongBasicInfoVo> selectNewSongs(@Param("startTime") LocalDateTime startTime, @Param("limit") int limit);

    /**
     * 获取飙升榜
     *
     * @return 歌曲列表
     */
    List<SongBasicInfoVo> selectRisingSongs();
}
