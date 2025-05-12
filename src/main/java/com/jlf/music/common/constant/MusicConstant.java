package com.jlf.music.common.constant;

/**
 * 歌单有关常量定义
 */
public class MusicConstant {
    /**
     * 歌单每日播放量
     */
    public static final String PLAYLIST_PLAY_COUNT_KEY_PREFIX = "playlist:playCount:";
    /**
     * 歌曲每日播放量
     */
    public static final String SONG_DAILY_PLAY_COUNT_KEY_PREFIX = "song:daily:playCount:";
    /**
     * MV每日播放量
     */
    public static final String MV_DAILY_PLAY_COUNT_KEY_PREFIX = "mv:daily:playCount:";

    /**
     * 歌单播放时长阈值(秒)
     */
    public static final long PLAY_DURATION_THRESHOLD = 30;
    /**
     * 重复播放时间窗口(秒)
     */
    public static final long REPEAT_PLAY_WINDOW = 3600;
    /**
     * 用户每日听歌时长
     */
    public static final String DAILY_LISTENING_TIME_USER_KEY_PREFIX = "daily:listening:time:";
    /**
     * 用户听歌时长的时间窗口(分)
     */
    public static final long DAILY_LISTENING_DURATION_THRESHOLD = 5;

}
