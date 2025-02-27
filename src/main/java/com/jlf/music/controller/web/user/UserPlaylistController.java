package com.jlf.music.controller.web.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.PageRequest;
import com.jlf.music.common.Result;
import com.jlf.music.controller.dto.EditPlaylistDTO;
import com.jlf.music.controller.dto.PlaylistDetailDTO;
import com.jlf.music.controller.qry.PlaylistPageQry;
import com.jlf.music.controller.vo.PlaylistBasicInfoVo;
import com.jlf.music.service.PlaylistInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户歌单控制层
 */
@RestController
@RequestMapping("api/user/playlist")
public class UserPlaylistController {
    @Resource
    private PlaylistInfoService playlistInfoService;

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
     * @param id              歌单id
     * @param editPlaylistDTO 编辑的值
     * @return Boolean
     */
    @PutMapping("/{id}/edit")
    public Result<Boolean> editPlaylistProperties(@PathVariable(value = "id") Long id,
                                                  EditPlaylistDTO editPlaylistDTO,
                                                  @RequestParam(required = false) MultipartFile playlistCover) {
        return Result.success(playlistInfoService.editPlaylistProperties(id, editPlaylistDTO, playlistCover));
    }

    /**
     * 添加歌曲到歌单中
     *
     * @param id      歌单ID
     * @param songIds 歌曲ID列表
     * @return Boolean
     */
    @PutMapping("/{id}/addSongs")
    public Result<Boolean> addSongsToPlaylist(@PathVariable(value = "id") Long id,
                                              @RequestParam List<Long> songIds) {
        return Result.success(playlistInfoService.addSongsToPlaylist(id, songIds));
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
     * @param playlistId 歌单 ID
     * @param songId     歌曲 ID
     * @param playDuration 播放时长
     */
    @PostMapping("/{playlistId}/play")
    public Result<Boolean> playPlaylist(@PathVariable("playlistId") Long playlistId,
                                @RequestParam Long songId,
                                @RequestParam Integer playDuration) {
        return Result.success(playlistInfoService.incrementPlayCount(playlistId,songId, playDuration));

    }

    /**
     * 分页获取用户创建的歌单列表
     * @param userId 用户id
     * @return IPage<PlaylistBasicInfoVo>
     */
    @GetMapping("/{userId}/list")
    public Result<IPage<PlaylistBasicInfoVo>> getPlaylistsByUserId(@PathVariable("userId") Long userId,
                                                                   PageRequest pageRequest) {
        return Result.success(playlistInfoService.getPlaylistsByUserId(userId, pageRequest));
    }

}
