package com.jlf.music.common.enumerate;

import lombok.Getter;

/**
 * 文件类型
 */
@Getter
public enum UploadFileType {
    SONG_COVER("jm-admin-song-cover"), // 歌曲封面
    SONG_AUDIO("jm-admin-song-audio"), // 歌曲音频文件
    SONG_LYRICS("jm-admin-song-lyrics"), // 歌曲歌词文件
    SINGER_AVATAR("jm-admin-singer-avatar"), // 歌手头像
    USER_AVATAR("jm-user-avatar"), // 用户头像
    PLAYLIST_COVER("jm-admin-playlist-cover");  // 歌单封面图
    private final String
            bucketName;
    UploadFileType(String bucketName) {
        this.bucketName = bucketName;
    }
}
