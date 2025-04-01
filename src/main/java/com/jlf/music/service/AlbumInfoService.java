package com.jlf.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.dto.AlbumFormDTO;
import com.jlf.music.controller.qry.AlbumQry;
import com.jlf.music.controller.vo.AlbumVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.AlbumInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AlbumInfoService extends IService<AlbumInfo> {
    /**
     * 分页或根据条件模糊查询album
     *
     * @param albumQry 模糊条件和分页参数
     * @return AlbumVo
     */
    IPage<AlbumVo> getAlbumsByPage(AlbumQry albumQry);

    /**
     * 根据专辑ID获取专辑中所有歌曲
     * @param albumId 专辑ID
     * @return songVo
     */
    List<SongBasicInfoVo> getAlbumWithSongs(Long albumId);
    /**
     * 更新专辑
     */
    Boolean updateAlbum(MultipartFile albumCoverFile, AlbumFormDTO albumFormDTO, Long albumId);

    /**
     * 新增专辑
     */
    Boolean addAlbum(AlbumFormDTO albumFormDTO, MultipartFile albumCoverFile);
}
