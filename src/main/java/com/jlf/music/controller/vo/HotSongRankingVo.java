package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author JLF
 * @date 2025/3/30 10:32
 * @description 热门歌曲响应对象
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class HotSongRankingVo {
    private List<HotSongRankingItem> data;
    /**
     * 开始日期
     */
    private String startDate;
    /**
     * 截止日期
     */
    private String endDate;
}
