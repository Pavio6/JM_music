package com.jlf.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.vo.SongRankingDailyVo;
import com.jlf.music.controller.vo.Top3ListeningSingerVo;
import com.jlf.music.entity.UserListeningRecord;

import java.util.List;

public interface UserListeningRecordService extends IService<UserListeningRecord> {
    /**
     * 记录用户听歌行为
     * @param songId 歌曲id
     * @param playDuration 播放时长
     * @return Boolean
     */
    Boolean recordListening(Long songId, Integer playDuration);
    /**
     * 获取用户当天的听歌时长
     * @return 时长
     */
    Integer getDailyListeningDuration();

    /**
     * 获取用户当天听歌最久的三位歌手及其时长
     */
    List<Top3ListeningSingerVo> getTop3ListeningSingerOfDay();
    /**
     * 获取用户当天听的歌曲排名
     * @return List<SongRankingDailyVo>
     */
    List<SongRankingDailyVo> getSongRankingOfDay();
}
