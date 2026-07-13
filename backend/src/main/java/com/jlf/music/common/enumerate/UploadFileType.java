package com.jlf.music.common.enumerate;

import lombok.Getter;

/**
 * 文件类型
 */
@Getter
public enum UploadFileType {
    /**
     * 歌曲封面
     */
    SONG_COVER("jm-admin-song-cover"),
    /**
     * 歌曲音频
     */
    SONG_AUDIO("jm-admin-song-audio"),
    /**
     * 歌曲视频
     */
    SONG_MV("jm-admin-song-mv"),
    /**
     * 歌曲歌词
     */
    SONG_LYRICS("jm-admin-song-lyrics"),
    /**
     * 歌手头像
     */
    SINGER_AVATAR("jm-admin-singer-avatar"),
    /**
     * 用户头像
     */
    USER_AVATAR("jm-user-avatar"),
    /**
     * 歌单封面
     */
    PLAYLIST_COVER("jm-admin-playlist-cover"),
    /**
     * 专辑封面
     */
    ALBUM_COVER("jm-admin-album-cover"),
    /**
     * 聊天图片
     */
    CHAT_IMAGE("jm-user-chat-image"),

    /**
     * 轮播图图片
     */
    CAROUSEL_IMAGE("jm-admin-carousel-image");

    /**
     * MinIO桶的名称
     */
    private final String bucketName;

    UploadFileType(String bucketName) {
        this.bucketName = bucketName;
    }
}
