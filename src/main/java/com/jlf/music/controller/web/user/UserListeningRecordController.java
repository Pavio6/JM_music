package com.jlf.music.controller.web.user;

import com.jlf.music.common.Result;
import com.jlf.music.controller.vo.SongRankingDailyVo;
import com.jlf.music.controller.vo.Top3ListeningSingerVo;
import com.jlf.music.controller.vo.UserListenTimeVo;
import com.jlf.music.service.UserListeningRecordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户播放记录
 */
@RestController
@RequestMapping("/api/user/listening")
public class UserListeningRecordController {
    @Resource
    private UserListeningRecordService userListeningRecordService;

    /**
     * 记录用户播放时长
     *
     * @param songId       歌曲id
     * @param playDuration 播放时长
     * @return Boolean
     */
    @PostMapping("/record")
    public Result<Boolean> recordListening(@RequestParam Long songId, @RequestParam Integer playDuration) {
        return Result.success(userListeningRecordService.recordListening(songId, playDuration));
    }

    /**
     * 获取用户当天的听歌时长
     *
     * @return 时长(秒)
     */
    @GetMapping("/daily-duration")
    public Result<UserListenTimeVo> getDailyListeningDuration() {
        return Result.success(userListeningRecordService.getDailyListeningDuration());
    }

    /**
     * 获取用户当天听歌最久的三位歌手及其时长
     *
     * @return List<Top3ListeningSingerVo>
     */
    @GetMapping("/most-listened-singers")
    public Result<List<Top3ListeningSingerVo>> getTop3ListeningSingerOfDay() {
        return Result.success(userListeningRecordService.getTop3ListeningSingerOfDay());
    }

    /**
     * 获取用户当天歌曲排名
     *
     * @return List<SongRankingDailyVo>
     */
    @GetMapping("/song-ranking")
    public Result<List<SongRankingDailyVo>> getSongRankingOfDay() {
        return Result.success(userListeningRecordService.getSongRankingOfDay());
    }
}
