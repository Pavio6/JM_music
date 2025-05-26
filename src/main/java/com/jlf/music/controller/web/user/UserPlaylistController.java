package com.jlf.music.controller.web.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.PageRequest;
import com.jlf.music.common.Result;
import com.jlf.music.controller.dto.EditPlaylistDTO;
import com.jlf.music.controller.dto.PlaylistDetailDTO;
import com.jlf.music.controller.qry.PlaylistCollectQry;
import com.jlf.music.controller.qry.PlaylistPageQry;
import com.jlf.music.controller.vo.PlaylistBasicInfoVo;
import com.jlf.music.controller.vo.SimpleItemVo;
import com.jlf.music.entity.PlaylistInfo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.PlaylistInfoMapper;
import com.jlf.music.service.PlaylistInfoService;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户歌单控制层
 */
@RestController
@RequestMapping("/api/user/playlist")
public class UserPlaylistController {
    @Resource
    private PlaylistInfoService playlistInfoService;
    @Resource
    private PlaylistInfoMapper playlistInfoMapper;

    /**
     * 创建歌单
     *
     * @param playlistName 歌单名称
     * @return Boolean
     */
    @PostMapping("/create")
    public Result<Boolean> createPlaylist(@RequestParam String playlistName) {
        return Result.success(playlistInfoService.createPlaylist(playlistName));
    }

    /**
     * 编辑歌单属性
     *
     * @param playlistId      歌单id
     * @param editPlaylistDTO 编辑的值
     * @return Boolean
     */
    @PutMapping("/{playlistId}/edit")
    public Result<Boolean> editPlaylistProperties(@PathVariable(value = "playlistId") Long playlistId,
                                                  EditPlaylistDTO editPlaylistDTO,
                                                  @RequestParam(required = false) MultipartFile playlistCover) {
        return Result.success(playlistInfoService.editPlaylistProperties(playlistId, editPlaylistDTO, playlistCover));
    }

    /**
     * 添加歌曲到歌单中
     *
     * @param playlistId 歌单ID
     * @param songIds    歌曲ID列表
     * @return Boolean
     */
    @PutMapping("/{playlistId}/addSongs")
    public Result<Boolean> addSongsToPlaylist(@PathVariable(value = "playlistId") Long playlistId,
                                              @RequestParam List<Long> songIds) {
        return Result.success(playlistInfoService.addSongsToPlaylist(playlistId, songIds));
    }

    /**
     * 从歌单中移除歌曲
     */
    @DeleteMapping("/{playlistId}/removeSongs")
    public Result<Boolean> removeSongsFromPlaylist(@PathVariable(value = "playlistId") Long playlistId,
                                                   @RequestParam List<Long> songIds) {
        return Result.success(playlistInfoService.removeSongsFromPlaylist(playlistId, songIds));
    }

    /**
     * 分页查询歌单列表(可通过标签分类查询)
     *
     * @param playlistPageQry 查询参数
     * @return IPage<List < PlaylistBasicInfoVo>>
     */
    @GetMapping("/page")
    public Result<IPage<PlaylistBasicInfoVo>> getPlaylistPage(PlaylistPageQry playlistPageQry) {
        return Result.success(playlistInfoService.getPlaylistPage(playlistPageQry));
    }

    /**
     * 根据id获取歌单详细信息
     *
     * @param playlistId 歌单id
     * @return PlaylistDetailDTO
     */
    @GetMapping("/{playlistId}")
    public Result<PlaylistDetailDTO> getPlaylistDetailById(@PathVariable("playlistId") Long playlistId) {
        return Result.success(playlistInfoService.getPlaylistDetailById(playlistId));
    }

    /**
     * TODO 不知道需要不 ?
     * 播放歌单
     *
     * @param playlistId   歌单 ID
     * @param playDuration 播放时长
     */
    @PostMapping("/{playlistId}/play")
    public Result<Boolean> playPlaylist(@PathVariable("playlistId") Long playlistId,
                                        @RequestParam Integer playDuration) {
        return Result.success(playlistInfoService.incrementPlayCount(playlistId, playDuration));

    }

    /**
     * 分页获取用户创建的歌单列表
     *
     * @param userId 用户id
     * @return IPage<PlaylistBasicInfoVo>
     */
    @GetMapping("/{userId}/create")
    public Result<IPage<PlaylistBasicInfoVo>> getPlaylistsByUserId(@PathVariable("userId") Long userId,
                                                                   PageRequest pageRequest) {
        return Result.success(playlistInfoService.getPlaylistsByUserId(userId, pageRequest));
    }

    /**
     * 获取用户个人创建的歌单列表
     */
    @GetMapping("/mine")
    public Result<IPage<PlaylistBasicInfoVo>> getPlaylistsMine(PageRequest pageRequest) {
        return Result.success(playlistInfoService.getPlaylistsMine(pageRequest));
    }

    /**
     * 获取用户收藏列表
     */
    @GetMapping("/{userId}/collect")
    public Result<IPage<SimpleItemVo>> getPlaylistCollectByUserId(@PathVariable("userId") Long userId,
                                                                  PlaylistCollectQry playlistCollectQry) {
        return Result.success(playlistInfoService.getPlaylistCollectByUserId(userId, playlistCollectQry));
    }

    /**
     * 删除歌单 (用户个人创建的)
     */
    @DeleteMapping("/delete/{playlistId}")
    public Result<Boolean> deleteMinePlaylist(@PathVariable("playlistId") Long playlistId) {
        PlaylistInfo playlistInfo = playlistInfoMapper.selectById(playlistId);
        if (playlistInfo == null) {
            throw new ServiceException("歌单不存在");
        }
        if (!playlistInfo.getCreatorId().equals(SecurityUtils.getUserId())) {
            throw new ServiceException("用户只能删除自己创建的歌单!");
        }
        return Result.success(playlistInfoService.deleteMinePlaylist(playlistId));
    }

}
