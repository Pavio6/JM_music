package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jlf.music.controller.qry.AlbumQry;
import com.jlf.music.controller.vo.AlbumSearchVo;
import com.jlf.music.controller.vo.AlbumVo;
import com.jlf.music.controller.vo.SongTypeDistributionItem;
import com.jlf.music.entity.AlbumInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlbumInfoMapper extends BaseMapper<AlbumInfo> {
    /**
     * 分页或根据条件模糊查询album
     *
     * @param albumQry 模糊条件和分页参数
     * @return AlbumVo
     */
    IPage<AlbumVo> getAlbumsByPage(Page<AlbumInfo> page, @Param("albumQry") AlbumQry albumQry);

    List<AlbumSearchVo> searchAlbums(@Param("keyword") String keyword,@Param("limit") Integer limit);

    List<SongTypeDistributionItem> getSongTypeDistributionItems();

}
