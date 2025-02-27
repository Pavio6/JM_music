package com.jlf.music.controller.vo;

import com.jlf.music.common.enumerate.PlayModeType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 播放队列数据
 */
@Data
@Accessors(chain = true)
public class QueueStateVo {
    /**
     * 播放模式
     */
    private PlayModeType playMode;
    /**
     * 当前播放的索引
     */
    private Integer currentIndex;
    /**
     * 当前播放的歌曲ID
     */
    private Long currentSongId;
    /**
     * 播放队列歌曲列表信息
     */
    private List<SongSimpleInfoVo> songsInfoList;

}
