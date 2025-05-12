package com.jlf.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.common.enumerate.PlayModeType;
import com.jlf.music.common.enumerate.QueueType;
import com.jlf.music.controller.vo.QueueStateVo;
import com.jlf.music.entity.PlayQueue;

import java.util.List;

public interface PlayQueueService extends IService<PlayQueue> {
    /**
     * 创建空的播放队列
     * @param userId 用户id
     */
    Boolean createEmptyQueue(Long userId);

    /**
     * 添加歌曲到队列
     * @param songId 歌曲id
     * @return Boolean
     */
    Boolean addSongToFront(Long songId);

    /**
     * 切换播放模式
     *
     * @param playMode 新的播放模式
     * @return Boolean
     */
    Boolean changePlayMode(PlayModeType playMode);

    /**
     * 获取当前播放队列状态
     *
     * @return 播放队列数据
     */
    QueueStateVo getCurrentQueue();

    /**
     * 从播放队列中批量移除歌曲
     * @param songIds 要移除的歌曲 ID 列表
     * @return Boolean
     */
    Boolean removeSongsFromQueue(List<Long> songIds);

    /**
     * 清空当前用户队列信息
     * @return Boolean
     */
    Boolean clearAll();
    /**
     * 切换整个播放队列中的歌曲
     * 完整队列切换操作 清空当前队列并创建新的队列
     *
     * @param songIds 歌曲列表
     * @return Boolean
     */
    Boolean switchSongsInPlayQueue(List<Long> songIds);

    /**
     * 修改用户正在播放的队列的歌曲
     *
     * @param songId 歌曲id
     * @return boolean
     */
    Boolean updateIsPlayingSong(Long songId);
}
