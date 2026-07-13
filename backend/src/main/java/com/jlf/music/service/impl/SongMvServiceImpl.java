package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.PageRequest;
import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.controller.dto.SongMvDTO;
import com.jlf.music.controller.qry.SongMvQry;
import com.jlf.music.controller.vo.MvListVo;
import com.jlf.music.controller.vo.SongMvDetailVo;
import com.jlf.music.controller.vo.SongMvVo;
import com.jlf.music.entity.SongMv;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.SongMvMapper;
import com.jlf.music.service.FileService;
import com.jlf.music.service.SongMvService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    @Resource
    private FileService fileService;


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

    /**
     * 分页获取mv列表
     */
    @Override
    public IPage<MvListVo> getMvList(SongMvQry songMvQry) {
        Page<MvListVo> page = new Page<>(songMvQry.getPageNum(), songMvQry.getPageSize());
        return songMvMapper.selectMvList(page, songMvQry);
    }

    /**
     * 编辑歌曲MV
     */
    @Override
    public Boolean updateSongMv(Long mvId, SongMvDTO songMvDTO, MultipartFile mvFilePath480p, MultipartFile mvFilePath720p, MultipartFile mvFilePath1080p) {
        SongMv songMv = this.getById(mvId);
        if (songMv == null) {
            throw new ServiceException("mv不存在");
        }
        LambdaUpdateWrapper<SongMv> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SongMv::getMvId, mvId);
        wrapper.set(songMvDTO.getMvBio() != null, SongMv::getMvBio, songMvDTO.getMvBio());
        wrapper.set(songMvDTO.getMvReleaseDate() != null, SongMv::getMvReleaseDate, songMvDTO.getMvReleaseDate());
        if (mvFilePath480p != null) {
            if (songMv.getMvFilePath480p() != null) {
                fileService.deleteFile(songMv.getMvFilePath480p());
            }
            String mv480p = fileService.uploadSongFile(mvFilePath480p, UploadFileType.SONG_MV);
            wrapper.set(SongMv::getMvFilePath480p, mv480p);
        }
        if (mvFilePath720p != null) {
            if (songMv.getMvFilePath720p() != null) {
                fileService.deleteFile(songMv.getMvFilePath720p());
            }
            String mv720p = fileService.uploadSongFile(mvFilePath720p, UploadFileType.SONG_MV);
            wrapper.set(SongMv::getMvFilePath720p, mv720p);
        }
        if (mvFilePath1080p != null) {
            if (songMv.getMvFilePath1080p() != null) {
                fileService.deleteFile(songMv.getMvFilePath1080p());
            }
            String mv1080p = fileService.uploadSongFile(mvFilePath1080p, UploadFileType.SONG_MV);
            wrapper.set(SongMv::getMvFilePath1080p, mv1080p);
        }
        return this.update(wrapper);
    }

    /**
     * 删除mv
     */
    @Override
    public Boolean removeSongMvById(Long mvId) {
        SongMv songMv = this.getById(mvId);
        if (songMv == null) {
            throw new ServiceException("歌曲MV不存在");
        }
        if (songMv.getMvFilePath480p() != null) {
            fileService.deleteFile(songMv.getMvFilePath480p());
        }
        if (songMv.getMvFilePath720p() != null) {
            fileService.deleteFile(songMv.getMvFilePath720p());
        }
        if (songMv.getMvFilePath1080p() != null) {
            fileService.deleteFile(songMv.getMvFilePath1080p());
        }
        return this.removeById(mvId);
    }

    /**
     * 获取mv分页
     */
    @Override
    public IPage<SongMvVo> getMvPage(PageRequest pageRequest) {
        Page<SongMvVo> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        return songMvMapper.selectPageInfo(page);
    }
}
