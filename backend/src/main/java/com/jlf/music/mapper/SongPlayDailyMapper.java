package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlf.music.controller.vo.HotSongRankingItem;
import com.jlf.music.entity.SongPlayDaily;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SongPlayDailyMapper extends BaseMapper<SongPlayDaily> {

    void batchInsert(@Param("stats") List<SongPlayDaily> stats);

    /**
     * 获取热门歌曲排行榜数据（当前周期和上一周期的对比数据）
     *
     * @param startDate         当前统计周期的开始日期（包含）
     * @param endDate           当前统计周期的结束日期（包含）
     * @param previousStartDate 上一统计周期的开始日期（包含）
     * @param previousEndDate   上一统计周期的结束日期（包含）
     * @param days              统计周期天数（通常为7天或14天）
     *                          - 用于计算日期范围跨度
     *                          - 包含结束日期，所以实际计算时用 days-1
     * @return 热门歌曲排行项列表，包含：
     * - 歌曲信息
     * - 当前周期统计量（如播放量）
     * - 上一周期统计量
     * - 变化趋势（如上升/下降百分比）
     */
    List<HotSongRankingItem> getHotSongRanking(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("previousStartDate") LocalDate previousStartDate,
            @Param("previousEndDate") LocalDate previousEndDate,
            @Param("days") int days
    );
}
