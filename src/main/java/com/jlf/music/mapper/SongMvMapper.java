package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlf.music.controller.vo.SongMvDetailVo;
import com.jlf.music.entity.SongMv;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: JLF
 * @Description: xxx
 * @Date 2025/3/19 11:13
 */
@Mapper
public interface SongMvMapper extends BaseMapper<SongMv> {
    /**
     * 根据mvId获取MV详细信息
     *
     * @param mvId 视频id
     * @return SongMvDetailVo
     */
    SongMvDetailVo selectMvDetailById(@Param("mvId") Long mvId);
}
