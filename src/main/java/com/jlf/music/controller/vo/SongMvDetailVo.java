package com.jlf.music.controller.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @Author: JLF
 * @Description: xxx
 * @Date 2025/3/19 11:59
 */
@Data
@Accessors(chain = true)
public class SongMvDetailVo {
    private Long mvId;
    private String mvBio;
    private Long singerId;
    private String singerName;
    private String songName;
    private LocalDate mvReleaseDate;
    @TableField("mv_file_path_480p")
    private String mvFilePath480p;
    @TableField("mv_file_path_720p")
    private String mvFilePath720p;
    @TableField("mv_file_path_1080p")
    private String mvFilePath1080p;
}
