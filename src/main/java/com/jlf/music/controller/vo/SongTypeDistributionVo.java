package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author JLF
 * @date 2025/3/30 10:02
 * @description xxx
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SongTypeDistributionVo {
    private List<SongTypeDistributionItem> data;
    private Long total;
}
