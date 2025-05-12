package com.jlf.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.dto.SingerFormDTO;
import com.jlf.music.controller.qry.SingerByRegionQry;
import com.jlf.music.controller.qry.SingerQry;
import com.jlf.music.controller.vo.SingerBasicInfoVo;
import com.jlf.music.controller.vo.SingerDetailInfoVo;
import com.jlf.music.controller.vo.SingerVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.SingerInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.spi.IIOServiceProvider;
import java.util.List;

public interface SingerInfoService extends IService<SingerInfo> {
    /**
     * 分页查询singer
     *
     * @param singerQry 分页参数
     * @return singerVo
     */
    IPage<SingerVo> getSingersByPage(SingerQry singerQry);

    /**
     * 通过区域分页获取歌手列表
     *
     * @param singerByRegionQry 分页信息
     * @return 歌手基本信息
     */
    IPage<SingerBasicInfoVo> getSingerPageByRegion(SingerByRegionQry singerByRegionQry);

    /**
     * 获取歌手详细信息
     *
     * @param singerId 歌手id
     */
    SingerDetailInfoVo getSingerDetailById(Long singerId);

    /**
     * 根据歌手id获取热门歌曲列表信息
     * 根据歌曲播放量排序
     *
     * @param singerId 歌手id
     */
    List<SongBasicInfoVo> searchBySingerId(Long singerId);
    /**
     * 更新歌手
     */
    Boolean updateSingerById(Long singerId, SingerFormDTO singerFormDTO, MultipartFile avatarFile);

    /**
     * 新增歌手
     */
    Boolean addSinger(SingerFormDTO singerFormDTO, MultipartFile avatarFile);

    /**
     * 删除歌手
     */
    Boolean deleteSinger(Long singerId);

}
