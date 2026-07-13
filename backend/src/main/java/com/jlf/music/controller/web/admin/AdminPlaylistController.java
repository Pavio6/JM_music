package com.jlf.music.controller.web.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.dto.PlaylistFormDTO;
import com.jlf.music.controller.qry.PlaylistPageQry;
import com.jlf.music.controller.vo.PlaylistBasicInfoVo;
import com.jlf.music.service.PlaylistInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author JLF
 * @date 2025/4/1 15:07
 * @description 管理端歌单管理
 */
@RestController
@RequestMapping("/api/admin/playlist")
public class AdminPlaylistController {

    @Resource
    private PlaylistInfoService playlistInfoService;

    /**
     * 获取歌单列表
     */
    @GetMapping("/list")
    public Result<IPage<PlaylistBasicInfoVo>> list(PlaylistPageQry playlistPageQry) {
        return Result.success(playlistInfoService.selectPlaylist(playlistPageQry));
    }

    /**
     * 添加歌单
     */
    @PostMapping
    public Result<Boolean> addPlaylist(@RequestPart("playlistFormDTO") PlaylistFormDTO playlistFormDTO,
                                       @RequestPart(value = "playlistCoverFile", required = false) MultipartFile playlistCoverFile) {
        return Result.success(playlistInfoService.addPlaylist(playlistFormDTO, playlistCoverFile));
    }

    /**
     * 更新歌单
     */
    @PutMapping("{playlistId}")
    public Result<Boolean> updatePlaylist(@RequestPart("playlistFormDTO") PlaylistFormDTO playlistFormDTO,
                                          @RequestPart(value = "playlistCoverFile", required = false) MultipartFile playlistCoverFile,
                                          @PathVariable Long playlistId) {
        return Result.success(playlistInfoService.updatePlaylist(playlistFormDTO, playlistCoverFile, playlistId));

    }

    /**
     * 获取歌单详情
     */
    @GetMapping("/{playlistId}")
    public Result<PlaylistBasicInfoVo> getPlaylistDetail(@PathVariable Long playlistId) {
        return Result.success(playlistInfoService.getAdminPlaylistDetail(playlistId));
    }
    /**
     * 删除歌单
     */
    @DeleteMapping("/{playlistId}")
    public Result<Boolean> deletePlaylist(@PathVariable Long playlistId) {
        return Result.success(playlistInfoService.deletePlaylist(playlistId));
    }

}
