package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.controller.dto.AlbumFormDTO;
import com.jlf.music.controller.qry.AlbumQry;
import com.jlf.music.controller.vo.AlbumVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.AlbumInfo;
import com.jlf.music.entity.SongInfo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.AlbumInfoMapper;
import com.jlf.music.mapper.SongInfoMapper;
import com.jlf.music.service.AlbumInfoService;
import com.jlf.music.service.FileService;
import com.jlf.music.utils.CopyUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AlbumInfoServiceImpl extends ServiceImpl<AlbumInfoMapper, AlbumInfo>
        implements AlbumInfoService {
    @Resource
    private AlbumInfoMapper albumInfoMapper;
    @Resource
    private SongInfoMapper songInfoMapper;
    @Resource
    private FileService fileService;

    /**
     * 分页或根据条件模糊查询album
     *
     * @param albumQry 模糊条件和分页参数
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

    /**
     * 更新专辑
     */
    @Override
    public Boolean updateAlbum(MultipartFile albumCoverFile, AlbumFormDTO albumFormDTO, Long albumId) {
        String albumCover = albumCoverFile != null ? fileService.uploadImageFile(albumCoverFile, UploadFileType.ALBUM_COVER) : null;
        AlbumInfo albumInfo = albumInfoMapper.selectById(albumId);
        if (albumInfo == null) {
            throw new ServiceException("专辑不存在");
        }
        // 更新非空字段
        if (albumFormDTO.getAlbumName() != null && !albumFormDTO.getAlbumName().isBlank()) {
            albumInfo.setAlbumName(albumFormDTO.getAlbumName());
        }
        if (albumFormDTO.getAlbumReleaseDate() != null) {
            albumInfo.setAlbumReleaseDate(albumFormDTO.getAlbumReleaseDate());
        }
        if (albumFormDTO.getAlbumBio() != null && !albumFormDTO.getAlbumBio().isBlank()) {
            albumInfo.setAlbumBio(albumFormDTO.getAlbumBio());
        }
        if (albumFormDTO.getTypeId() != null) {
            albumInfo.setTypeId(albumFormDTO.getTypeId());
        }
        if (albumFormDTO.getSingerId() != null) {
            albumInfo.setSingerId(albumFormDTO.getSingerId());
        }

        // 更新文件路径
        if (albumCover != null) {
            // 删除之前存放在 minio 中的文件
            fileService.deleteFile(albumInfo.getAlbumCover());
            albumInfo.setAlbumCover(albumCover);
        }

        return albumInfoMapper.updateById(albumInfo) > 0;
    }

    /**
     * 新增专辑
     */
    @Override
    public Boolean addAlbum(AlbumFormDTO albumFormDTO, MultipartFile albumCoverFile) {
        String albumCover = albumCoverFile != null ? fileService.uploadImageFile(albumCoverFile, UploadFileType.ALBUM_COVER) : null;
        AlbumInfo albumInfo = new AlbumInfo();
        CopyUtils.classCopy(albumFormDTO, albumInfo);
        if (albumCover != null) {
            albumInfo.setAlbumCover(albumCover);
        }
        return albumInfoMapper.insert(albumInfo) > 0;
    }
}
