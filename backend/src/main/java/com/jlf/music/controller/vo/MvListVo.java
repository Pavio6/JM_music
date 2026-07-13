package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/15 15:48
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MvListVo {
    /**
     * 音乐视频的唯一标识符。
     */
    private Long mvId;

    /**
     * 与音乐视频关联的歌手的唯一标识符。
     */
    private Long singerId;

    /**
     * 与音乐视频关联的歌手名称。
     */
    private String singerName;

    /**
     * 与音乐视频关联的歌曲的唯一标识符。
     */
    private Long songId;

    /**
     * 与音乐视频关联的歌曲名称。
     */
    private String songName;

    /**
     * 音乐视频的480p分辨率文件路径。
     */
    private String mvFilePath480p;

    /**
     * 音乐视频的720p分辨率文件路径。
     */
    private String mvFilePath720p;

    /**
     * 音乐视频的1080p分辨率文件路径。
     */
    private String mvFilePath1080p;

    /**
     * 音乐视频的发布日期（字符串格式）。
     */
    private LocalDate mvReleaseDate;

    /**
     * 音乐视频的简介或描述（可选）。
     */
    private String mvBio;
}
