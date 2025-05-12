package com.jlf.music.controller.web.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.dto.SongFormDTO;
import com.jlf.music.controller.qry.SongQry;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.controller.vo.SongDetailVo;
import com.jlf.music.entity.SongInfo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.service.SongInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/song")
public class AdminSongInfoController {
    @Resource
    private SongInfoService songInfoService;

    /**
     * 分页或通过名称模糊查询歌曲信息
     */
    @GetMapping("/list")
    public Result<IPage<SongBasicInfoVo>> getSongsByPage(SongQry songQry) {
        return Result.success(songInfoService.getSongsByPage(songQry));
    }

    /**
     * 更新歌曲
     */
    @PutMapping("/{songId}")
    public Result<Boolean> updateSong(
            @PathVariable Long songId,
            @RequestPart(value = "songLyricsFile", required = false) MultipartFile songLyricsFile,
            @RequestPart(value = "songFile", required = false) MultipartFile songFile,
            @RequestPart(value = "songCoverFile", required = false) MultipartFile songCoverFile,
            @RequestPart(value = "songFormDTO") SongFormDTO songFormDTO) {

        return Result.success(songInfoService.updateSong(songId, songLyricsFile, songFile, songCoverFile, songFormDTO));
    }

    /**
     * 获取歌曲详情
     *
     * @param songId 歌曲id
     */
    @GetMapping("/{songId}")
    public Result<SongDetailVo> getSongDetail(@PathVariable Long songId) {
        if (songInfoService.getById(songId) == null) {
            throw new ServiceException("歌曲不存在");
        }
        return Result.success(songInfoService.getSongDetail(songId));
    }

    /**
     * 新增歌曲
     */
    @PostMapping
    public Result<Boolean> addSong(@RequestPart("songFormDTO") SongFormDTO songFormDTO,
                                   @RequestPart(value = "songLyricsFile", required = false) MultipartFile songLyricsFile,
                                   @RequestPart(value = "songFile", required = false) MultipartFile songFile,
                                   @RequestPart(value = "songCoverFile", required = false) MultipartFile songCoverFile,
                                   @RequestPart(value = "mvFilePath480p", required = false) MultipartFile mvFilePath480p,
                                   @RequestPart(value = "mvFilePath720p", required = false) MultipartFile mvFilePath720p,
                                   @RequestPart(value = "mvFilePath1080p", required = false) MultipartFile mvFilePath1080p) {
        return Result.success(songInfoService.addSong(songFormDTO, songLyricsFile, songFile, songCoverFile, mvFilePath480p, mvFilePath720p, mvFilePath1080p));
    }

    /**
     * 删除歌曲
     */
    @DeleteMapping("/{songId}")
    public Result<Boolean> deleteSong(@PathVariable Long songId) {
        return Result.success(songInfoService.deleteSongById(songId));
    }
}
