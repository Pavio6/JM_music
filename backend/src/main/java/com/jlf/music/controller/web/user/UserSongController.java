package com.jlf.music.controller.web.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.common.enumerate.TargetType;
import com.jlf.music.controller.qry.SongQry;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.controller.vo.SongDetailVo;
import com.jlf.music.controller.vo.SongLyricsAndAudioVo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.service.SongInfoService;
import jakarta.annotation.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户歌曲控制层
 */
@RestController
@RequestMapping("/api/user/song")
public class UserSongController {
    @Resource
    private SongInfoService songInfoService;


    /**
     * 根据歌曲ID查询歌曲音频歌词文件URL
     *
     * @return 音频文件URL
     */
    @GetMapping("/{songId}/audio-and-lyrics")
    public Result<SongLyricsAndAudioVo> getSongAudioAndLyricsById(@PathVariable(value = "songId") Long songId) {
        return Result.success(songInfoService.getSongAudioAndLyricsById(songId));
    }

    /**
     * 根据id下载歌曲音频
     *
     * @param songId 歌曲id
     * @return 音频文件流
     */
    @GetMapping("/download/{songId}")
    public ResponseEntity<InputStreamResource> downloadSong(@PathVariable(value = "songId") Long songId) {
        return songInfoService.downloadSong(songId);

    }

    /**
     * 获取新歌榜
     * 根据发行日期降序返回
     *
     * @return 歌曲列表
     */
    @GetMapping("/new")
    public Result<List<SongBasicInfoVo>> getNewSongs() {

        return Result.success(songInfoService.getNewSongs());
    }

    /**
     * 获取热歌榜
     * 根据播放量降序返回
     *
     * @return 歌曲列表
     */
    @GetMapping("/hot")
    public Result<List<SongBasicInfoVo>> getHotSongs() {

        return Result.success(songInfoService.getHotSongs());
    }

    /**
     * 获取飙升榜
     * 根据前两天 - 前一天的播放量的值 进行降序返回
     *
     * @return 歌曲列表
     */
    @GetMapping("/rising")
    public Result<List<SongBasicInfoVo>> getRisingSongs() {

        return Result.success(songInfoService.getRisingSongs());
    }

    /**
     * 根据id增加播放量
     *
     * @param targetId   歌曲/歌单id
     * @param targetType 歌曲/歌单
     * @return Boolean
     */
    @PostMapping("/{targetId}/playCount")
    public Result<Boolean> incrementPlayCount(@PathVariable(value = "targetId") Long targetId, @RequestParam String targetType) {
        return Result.success(songInfoService.incrementPlayCountByTargetId(targetId, targetType));
    }

    /**
     * 获取歌曲详情
     */
    @GetMapping("/{songId}/detail")
    public Result<SongDetailVo> getSongDetail(@PathVariable Long songId) {
        if (songInfoService.getById(songId) == null) {
            throw new ServiceException("歌曲不存在");
        }
        return Result.success(songInfoService.getSongDetail(songId));
    }

    /**
     * 获取歌曲列表 - (用户编辑歌单 添加歌曲时)
     */
    @GetMapping("/list")
    public Result<List<SongBasicInfoVo>> getSongList(SongQry songQry) {
        IPage<SongBasicInfoVo> songsByPage = songInfoService.getSongsByPage(songQry);
        return Result.success(songsByPage.getRecords());
    }

}
