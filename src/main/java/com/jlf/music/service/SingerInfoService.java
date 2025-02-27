package com.jlf.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.qry.SingerByRegionQry;
import com.jlf.music.controller.qry.SingerQry;
import com.jlf.music.controller.vo.SingerBasicInfoVo;
import com.jlf.music.controller.vo.SingerVo;
import com.jlf.music.entity.SingerInfo;

import javax.imageio.spi.IIOServiceProvider;

public interface SingerInfoService extends IService<SingerInfo> {
    /**
     * 分页查询singer
     * @param singerQry 分页参数
     * @return singerVo
     */
    IPage<SingerVo> getSingersByPage(SingerQry singerQry);

    /**
     * 通过区域分页获取歌手列表
     * @param singerByRegionQry 分页信息
     * @return 歌手基本信息
     */
    IPage<SingerBasicInfoVo> getSingerPageByRegion(SingerByRegionQry singerByRegionQry);
}
