package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.controller.vo.RegionBasicVo;
import com.jlf.music.entity.RegionInfo;
import com.jlf.music.mapper.RegionInfoMapper;
import com.jlf.music.service.RegionInfoService;
import com.jlf.music.utils.CopyUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionInfoServiceImpl extends ServiceImpl<RegionInfoMapper, RegionInfo>
        implements RegionInfoService {
    /**
     * 获取地域列表
     *
     * @return 地域list
     */
    @Override
    public List<RegionBasicVo> getRegionList() {
        List<RegionInfo> list = this.list();
        return CopyUtils.classCopyList(list, RegionBasicVo.class);
    }
}
