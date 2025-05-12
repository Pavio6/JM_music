package com.jlf.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.common.PageRequest;
import com.jlf.music.controller.dto.EditPlaylistDTO;
import com.jlf.music.controller.dto.PlaylistDetailDTO;
import com.jlf.music.controller.dto.PlaylistFormDTO;
import com.jlf.music.controller.qry.PlaylistCollectQry;
import com.jlf.music.controller.qry.PlaylistPageQry;
import com.jlf.music.controller.vo.PlaylistBasicInfoVo;
import com.jlf.music.controller.vo.SimpleItemVo;
import com.jlf.music.entity.PlaylistInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PlaylistInfoService extends IService<PlaylistInfo> {
    /**
     * 创建歌单
     *
     * @param playlistName 歌单名称
     * @return Boolean
     */
    Boolean createPlaylist(String playlistName);

    /**
     * 添加歌曲到歌单中
     *
     * @param playlistId      歌单ID
     * @param songIds 歌曲ID列表
     * @return Boolean
     */
    Boolean addSongsToPlaylist(Long playlistId, List<Long> songIds);

    /**
     * 编辑歌单属性
     *
     * @param playlistId              歌单id
     * @param editPlaylistDTO 编辑的值
     * @param playlistCover   歌单封面图
     * @return Boolean
     */
    Boolean editPlaylistProperties(Long playlistId, EditPlaylistDTO editPlaylistDTO, MultipartFile playlistCover);

    /**
     * 分页查询歌单列表
     *
     * @param playlistPageQry 查询参数
     * @return IPage<List < PlaylistBasicInfoVo>>
     */
    IPage<PlaylistBasicInfoVo> getPlaylistPage(PlaylistPageQry playlistPageQry);

    /**
     * 根据id获取歌单详细信息
     *
     * @param playlistId 歌单id
     * @return PlaylistDetailDTO
     */
    PlaylistDetailDTO getPlaylistDetailById(Long playlistId);

    /**
     * 增加播放量
     *
     * @param playlistId   歌单 ID
     * @param playDuration 播放时长
     */
    Boolean incrementPlayCount(Long playlistId, Integer playDuration);

    /**
     * 分页获取用户创建的歌单列表
     *
     * @param userId      用户id
     * @param pageRequest 分页参数
     * @return IPage<PlaylistBasicInfoVo>
     */
    IPage<PlaylistBasicInfoVo> getPlaylistsByUserId(Long userId, PageRequest pageRequest);

    /**
     * 获取歌单列表
     */
    IPage<PlaylistBasicInfoVo> selectPlaylist(PlaylistPageQry playlistPageQry);

    /**
     * 添加歌单
     */
    Boolean addPlaylist(PlaylistFormDTO playlistFormDTO, MultipartFile playlistCoverFile);

    /**
     * 更新歌单
     */
    Boolean updatePlaylist(PlaylistFormDTO playlistFormDTO, MultipartFile playlistCoverFile, Long playlistId);

    /**
     * 获取管理端歌单详情
     *
     */
    PlaylistBasicInfoVo getAdminPlaylistDetail(Long playlistId);

    /**
     * 删除歌单
     */
    Boolean deletePlaylist(Long playlistId);

    /**
     * 从歌单中移除歌曲
     * @param playlistId 歌单id
     * @param songIds 歌曲id
     * @return boolean
     */
    Boolean removeSongsFromPlaylist(Long playlistId, List<Long> songIds);

    /**
     * 获取用户收藏列表
     */
    IPage<SimpleItemVo> getPlaylistCollectByUserId(Long userId, PlaylistCollectQry playlistCollectQry);
    /**
     * 获取用户个人创建的歌单列表
     */
    IPage<PlaylistBasicInfoVo> getPlaylistsMine(PageRequest pageRequest);
}
