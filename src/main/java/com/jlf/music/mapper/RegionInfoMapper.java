package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.jlf.music.entity.RegionInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegionInfoMapper extends BaseMapper<RegionInfo> {
}
