package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlf.music.entity.SongPlayDaily;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SongPlayDailyMapper extends BaseMapper<SongPlayDaily> {

    void batchInsert(@Param("stats") List<SongPlayDaily> stats);
}
