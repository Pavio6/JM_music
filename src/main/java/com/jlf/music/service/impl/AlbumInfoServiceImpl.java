package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.enumerate.QueueType;
import com.jlf.music.common.enumerate.TargetType;
import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.controller.dto.AlbumFormDTO;
import com.jlf.music.controller.qry.AlbumQry;
import com.jlf.music.controller.vo.AlbumVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.*;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.*;
import com.jlf.music.service.AlbumInfoService;
import com.jlf.music.service.FileService;
import com.jlf.music.service.SongInfoService;
import com.jlf.music.utils.CopyUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Resource
    private SongInfoService songInfoService;
    @Resource
    private UserFavoriteMapper userFavoriteMapper;
    @Resource
    private PlayQueueMapper playQueueMapper;
    @Autowired
    private TypeInfoMapper typeInfoMapper;
    @Autowired
    private SingerInfoMapper singerInfoMapper;

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

    /**
     * 删除专辑
     */
    @Override
    public Boolean deleteAlbum(Long albumId) {
        if (albumInfoMapper.selectById(albumId) == null) {
            throw new ServiceException("该专辑不存在");
        }
        // 判断用户是否收藏了该专辑
        if (userFavoriteMapper.selectCount(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getTargetType, TargetType.ALBUM.getValue())
                .eq(UserFavorite::getTargetId, albumId)) > 0) {
            throw new ServiceException("无法删除专辑，该专辑已被用户收藏");
        }

        // 删除专辑及其对应的所有歌曲
        List<SongInfo> list = songInfoMapper.selectList(new LambdaQueryWrapper<SongInfo>()
                .eq(SongInfo::getAlbumId, albumId));
        for (SongInfo songInfo : list) {
            songInfoService.deleteSongInfo(songInfo.getSongId());
        }
        return albumInfoMapper.deleteById(albumId) > 0;
    }

    /**
     * 获取专辑类型
     */
    @Override
    public List<TypeInfo> getAlbumType() {
        return typeInfoMapper.selectList(null);
    }

    /**
     * 获取专辑详情
     */
    @Override
    public AlbumVo getAlbumDetailByAlbumId(Long albumId) {
        AlbumInfo albumInfo = albumInfoMapper.selectById(albumId);
        if (albumInfo == null) {
            throw new ServiceException("专辑不存在");
        }
        List<SongBasicInfoVo> albumWithSongs = getAlbumWithSongs(albumId);
        AlbumVo albumVo = CopyUtils.classCopy(albumInfo, AlbumVo.class);
        albumVo.setSongs(albumWithSongs);
        String singerName = singerInfoMapper.selectById(albumInfo.getSingerId()).getSingerName();
        albumVo.setSingerName(singerName);
        String typeName = typeInfoMapper.selectById(albumInfo.getTypeId()).getTypeName();
        albumVo.setTypeName(typeName);
        return albumVo;
    }
}
