package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.controller.vo.SongMvDetailVo;
import com.jlf.music.entity.SongMv;
import com.jlf.music.mapper.SongMvMapper;
import com.jlf.music.service.SongMvService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @Author: JLF
 * @Description: xxx
 * @Date 2025/3/19 11:14
 */
@Service
public class SongMvServiceImpl extends ServiceImpl<SongMvMapper, SongMv>
        implements SongMvService {
    @Resource
    private SongMvMapper songMvMapper;

    /**
     * 根据mvId获取MV详细信息
     *
     * @param mvId 视频id
     * @return SongMvDetailVo
     */
    @Override
    public SongMvDetailVo getMvDetail(Long mvId) {
        return songMvMapper.selectMvDetailById(mvId);
    }
}
