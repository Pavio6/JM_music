package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jlf.music.controller.qry.SingerQry;
import com.jlf.music.controller.vo.SingerVo;
import com.jlf.music.entity.SingerInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SingerInfoMapper extends BaseMapper<SingerInfo> {

    IPage<SingerVo> getSingersByPage(@Param("page") Page<SingerVo> page, @Param("singerQry") SingerQry singerQry);
}
