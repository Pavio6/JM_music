package com.jlf.music.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.controller.qry.SingerByRegionQry;
import com.jlf.music.controller.qry.SingerQry;
import com.jlf.music.controller.vo.SingerBasicInfoVo;
import com.jlf.music.controller.vo.SingerVo;
import com.jlf.music.entity.RegionInfo;
import com.jlf.music.entity.SingerInfo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.RegionInfoMapper;
import com.jlf.music.mapper.SingerInfoMapper;
import com.jlf.music.service.SingerInfoService;
import com.jlf.music.utils.CopyUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class SingerInfoServiceImpl extends ServiceImpl<SingerInfoMapper, SingerInfo>
        implements SingerInfoService {
    @Resource
    private SingerInfoMapper singerInfoMapper;
    @Resource
    private RegionInfoMapper regionInfoMapper;

    /**
     * 分页查询singer
     *
     * @param singerQry 分页参数
     * @return singerVo
     */
    @Override
    public IPage<SingerVo> getSingersByPage(SingerQry singerQry) {
        Page<SingerInfo> page = new Page<>(singerQry.getPageNum(), singerQry.getPageSize());
        LambdaQueryWrapper<SingerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(singerQry.getSingerName()), SingerInfo::getSingerName, singerQry.getSingerName());
        singerInfoMapper.selectPage(page, wrapper);
        return CopyUtils.covertPage(page, SingerVo.class);
    }

    /**
     * 通过区域分页获取歌手列表
     *
     * @param singerByRegionQry 分页信息
     * @return 歌手基本信息
     */
    @Override
    public IPage<SingerBasicInfoVo> getSingerPageByRegion(SingerByRegionQry singerByRegionQry) {
        Page<SingerInfo> page = new Page<>(singerByRegionQry.getPageNum(), singerByRegionQry.getPageSize());
        // 区域id存在
        if (ObjectUtil.isNotEmpty(singerByRegionQry.getRegionId())) {
            // 判断区域id是否存在
            if (regionInfoMapper.selectById(singerByRegionQry.getRegionId()) == null) {
                throw new ServiceException("不存在该区域名称");
            }
            // 进行分页查询
            page = singerInfoMapper.selectPage(page, new LambdaQueryWrapper<SingerInfo>()
                    .eq(SingerInfo::getRegionId, singerByRegionQry.getRegionId()));
            return CopyUtils.covertPage(page, SingerBasicInfoVo.class);
        }
        return CopyUtils.covertPage(singerInfoMapper.selectPage(page, null), SingerBasicInfoVo.class);
    }
}
