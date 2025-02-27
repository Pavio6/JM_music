package com.jlf.music.controller.web.user;

import com.jlf.music.common.Result;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.controller.vo.SongLyricsAndAudioVo;
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
     * 获取最新的歌曲信息列表
     *
     * @return List<SongBasicInfoVo>
     */
    @GetMapping("/latest")
    public Result<List<SongBasicInfoVo>> getLatestSongs() {
        return Result.success(songInfoService.getLatestSongs());
    }

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


}
