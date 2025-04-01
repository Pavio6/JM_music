package com.jlf.music.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author JLF
 * @date 2025/3/31 17:10
 * @description 更新专辑表单数据
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AlbumFormDTO {
    // 专辑ID（可选）
    private Long albumId;
    // 专辑名称
    private String albumName;
    // 专辑发行日期
    private LocalDate albumReleaseDate;
    // 专辑简介（可选）
    private String albumBio;
    // 类型ID
    private Long typeId;
    // 歌手ID
    private Long singerId;
}
