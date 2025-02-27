package com.jlf.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.qry.AlbumQry;
import com.jlf.music.controller.vo.AlbumVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.AlbumInfo;

import java.util.List;

public interface AlbumInfoService extends IService<AlbumInfo> {
    /**
     * 分页或通过名称模糊查询album
     * @param albumQry 专辑名称和分页参数
     * @return AlbumVo
     */
    IPage<AlbumVo> getAlbumsByPage(AlbumQry albumQry);

    /**
     * 根据专辑ID获取专辑中所有歌曲
     * @param albumId 专辑ID
     * @return songVo
     */
    List<SongBasicInfoVo> getAlbumWithSongs(Long albumId);
}
