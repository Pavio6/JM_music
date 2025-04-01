package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author JLF
 * @date 2025/3/30 10:04
 * @description xxx
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SongTypeDistributionItem {
    private Long typeId;
    private String typeName;
    private Long count;
}
