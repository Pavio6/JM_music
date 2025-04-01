package com.jlf.music.controller.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author JLF
 * @date 2025/3/31 8:26
 * @description 歌曲详情
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SongDetailVo {
    private Long songId; // 歌曲ID
    private String songName; // 歌曲名称
    private LocalTime songDuration; // 歌曲时长
    private String songLyrics; // 歌词（可选）
    private LocalDate songReleaseDate; // 发行日期
    private String songFilePath; // 歌曲文件路径（可选）
    private String songCover; // 歌曲封面（可选）
    private Long singerId; // 歌手ID
    private Long albumId; // 专辑ID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime; // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime; // 更新时间
    private Integer deleteFlag; // 删除标志（0:未删除, 1:已删除）
    private Double playCount; // 播放次数
    private String singerName; // 关联歌手名称（可选）
    private String albumName; // 关联专辑名称（可选）
}
