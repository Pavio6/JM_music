package com.jlf.music.controller.web.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.PageRequest;
import com.jlf.music.common.Result;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.controller.vo.SongLyricsAndAudioVo;
import com.jlf.music.service.SongInfoService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Max;
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
     * 获取最新的歌曲信息列表
     *
     * @return List<SongBasicInfoVo>
     */
   /* @GetMapping("/latest")
    public Result<List<SongBasicInfoVo>> getLatestSongs() {
        return Result.success(songInfoService.getLatestSongs());
    }*/

    /**
     * 根据歌曲ID查询歌曲音频歌词文件URL
     * TODO 应该是为了播放音乐 此时可以记录音乐的播放量
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
     *
     * @return 歌曲列表
     */
    @GetMapping("/new")
    public Result<List<SongBasicInfoVo>> getNewSongs() {

        return Result.success(songInfoService.getNewSongs());
    }

    /**
     * 获取热歌榜
     *
     * @return 歌曲列表
     */
    @GetMapping("/hot")
    public Result<List<SongBasicInfoVo>> getHotSongs() {

        return Result.success(songInfoService.getHotSongs());
    }

    /**
     * 获取飙升榜
     *
     * @return 歌曲列表
     */
    @GetMapping("/rising")
    public Result<List<SongBasicInfoVo>> getRisingSongs() {

        return Result.success(songInfoService.getRisingSongs());
    }
}
