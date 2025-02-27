package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.controller.qry.AlbumQry;
import com.jlf.music.controller.vo.AlbumVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.AlbumInfo;
import com.jlf.music.entity.SongInfo;
import com.jlf.music.mapper.AlbumInfoMapper;
import com.jlf.music.mapper.SongInfoMapper;
import com.jlf.music.service.AlbumInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumInfoServiceImpl extends ServiceImpl<AlbumInfoMapper, AlbumInfo>
        implements AlbumInfoService {
    @Resource
    private AlbumInfoMapper albumInfoMapper;
    @Resource
    private SongInfoMapper songInfoMapper;
    /**
     * 分页或通过名称模糊查询album
     *
     * @param albumQry 专辑名称和分页参数
     * @return AlbumVo
     */
    @Override
    public IPage<AlbumVo> getAlbumsByPage(AlbumQry albumQry) {
        Page<AlbumInfo> page = new Page<>(albumQry.getPageNum(), albumQry.getPageSize());
        return albumInfoMapper.getAlbumsByPage(page, albumQry);
    }

    /**
     * 根据专辑ID获取专辑中所有歌曲
     *
     * @param albumId 专辑ID
     * @return songVo
     */
    @Override
    public List<SongBasicInfoVo> getAlbumWithSongs(Long albumId) {
        LambdaQueryWrapper<SongInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SongInfo::getAlbumId, albumId);
        return songInfoMapper.getSongsByAlbumId(albumId);
    }
}
