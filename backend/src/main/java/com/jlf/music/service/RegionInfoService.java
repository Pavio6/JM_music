package com.jlf.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.vo.RegionBasicVo;
import com.jlf.music.entity.RegionInfo;

import java.util.List;

public interface RegionInfoService extends IService<RegionInfo> {
    /**
     * 获取地域列表
     * @return 地域list
     */
    List<RegionBasicVo> getRegionList();

}
