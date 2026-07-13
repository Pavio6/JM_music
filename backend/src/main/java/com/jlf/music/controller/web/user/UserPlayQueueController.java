package com.jlf.music.controller.web.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jlf.music.common.Result;
import com.jlf.music.common.enumerate.PlayModeType;
import com.jlf.music.common.enumerate.QueueType;
import com.jlf.music.controller.vo.QueueStateVo;
import com.jlf.music.entity.SongInfo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.service.PlayQueueService;
import com.jlf.music.service.SongInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 播放队列
 */
@RestController
@RequestMapping("/api/user/play/queue")
public class UserPlayQueueController {
    @Resource
    private PlayQueueService playQueueService;
    @Resource
    private SongInfoService songInfoService;

    /**
     * 添加歌曲到队列 (一次只能添加一首)
     * 播放歌曲 即添加
     *
     * @param songId 歌曲id
     * @return Boolean
     */
    @PostMapping("/add/{songId}")
    public Result<Boolean> addSongToQueue(@PathVariable("songId") Long songId) {
        if (!songInfoService.exists(new LambdaQueryWrapper<SongInfo>()
                .eq(SongInfo::getSongId, songId))) {
            throw new ServiceException("歌曲不存在");
        }

        return Result.success(playQueueService.addSongToFront(songId));
    }

    /**
     * 从播放队列中批量移除歌曲
     * 如果移除的歌曲中包含正在播放的歌曲  那么播放队列的当前播放索引值将随机变为队列中某首歌
     *
     * @param songIds 要移除的歌曲 ID 列表
     * @return Boolean
     */
    @DeleteMapping("/batchDelete")
    public Result<Boolean> removeSongsFromQueue(@RequestParam("songIds") List<Long> songIds) {
        return Result.success(playQueueService.removeSongsFromQueue(songIds));
    }

    /**
     * 清空当前用户队列信息
     *
     * @return Boolean
     */
    @DeleteMapping("/clearAll")
    public Result<Boolean> clearAll() {
        return Result.success(playQueueService.clearAll());
    }


    /**
     * 切换播放模式
     *
     * @param playMode 新的播放模式
     * @return Boolean
     */
    @PutMapping("/change/play-mode")
    public Result<Boolean> changePlayMode(@RequestParam("playMode") PlayModeType playMode) {
        return Result.success(playQueueService.changePlayMode(playMode));
    }

    /**
     * 获取当前播放队列状态
     *
     * @return 播放队列数据
     */
    @GetMapping("/current")
    public Result<QueueStateVo> getCurrentQueue() {
        return Result.success(playQueueService.getCurrentQueue());
    }

    /**
     * 切换整个播放队列中的歌曲
     * 完整队列切换操作 清空当前队列并创建新的队列
     *
     * @param songIds 歌曲列表
     * @return Boolean
     */
    @PostMapping("/switch")
    public Result<Boolean> switchSongsInPlayQueue(@RequestParam List<Long> songIds) {
        return Result.success(playQueueService.switchSongsInPlayQueue(songIds));
    }

    /**
     * 修改用户正在播放的队列的歌曲
     *
     * @param songId 歌曲id
     * @return boolean
     */
    @PutMapping("/{songId}/update")
    public Result<Boolean> updateIsPlayingSong(@PathVariable Long songId) {
        return Result.success(playQueueService.updateIsPlayingSong(songId));
    }

}
