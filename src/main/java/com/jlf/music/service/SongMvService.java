package com.jlf.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.common.PageRequest;
import com.jlf.music.controller.dto.SongMvDTO;
import com.jlf.music.controller.qry.SongMvQry;
import com.jlf.music.controller.vo.MvListVo;
import com.jlf.music.controller.vo.SongMvDetailVo;
import com.jlf.music.controller.vo.SongMvVo;
import com.jlf.music.entity.SongMv;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 分页获取mv列表
     */
    IPage<MvListVo> getMvList(@Param("qry") SongMvQry songMvQry);

    /**
     * 编辑歌曲MV
     */
    Boolean updateSongMv(Long mvId, SongMvDTO songMvDTO, MultipartFile mvFilePath480p, MultipartFile mvFilePath720p, MultipartFile mvFilePath1080p);

    /**
     * 删除mv
     */
    Boolean removeSongMvById(Long mvId);

    /**
     * 获取mv分页
     */
    IPage<SongMvVo> getMvPage(PageRequest pageRequest);
}
