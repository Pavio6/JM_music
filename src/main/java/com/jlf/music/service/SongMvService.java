package com.jlf.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.common.Result;
import com.jlf.music.controller.vo.SongMvDetailVo;
import com.jlf.music.entity.SongMv;

/**
 * @Author: JLF
 * @Description: xxx
 * @Date 2025/3/19 11:14
 */
public interface SongMvService extends IService<SongMv> {
    /**
     * 根据mvId获取MV详细信息
     *
     * @param mvId 视频id
     * @return SongMvDetailVo
     */
    SongMvDetailVo getMvDetail(Long mvId);
}
