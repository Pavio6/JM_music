package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jlf.music.controller.qry.SongMvQry;
import com.jlf.music.controller.vo.MvListVo;
import com.jlf.music.controller.vo.SongMvDetailVo;
import com.jlf.music.controller.vo.SongMvVo;
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

    /**
     * 获取mv列表
     */
    IPage<MvListVo> selectMvList(@Param("page") Page<MvListVo> page, @Param("qry") SongMvQry songMvQry);

    /**
     * 获取分页mv
     */
    IPage<SongMvVo> selectPageInfo(@Param("page") Page<SongMvVo> page);
}
