package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author JLF
 * @date 2025/3/30 16:06
 * @description 专辑下拉框
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AlbumOptionVo {
    private Long albumId;
    private String albumName;
    private Long signerId;
}
