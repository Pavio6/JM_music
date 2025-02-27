package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.controller.qry.SongQry;
import com.jlf.music.controller.vo.SongLyricsAndAudioVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.SongInfo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.SongInfoMapper;
import com.jlf.music.service.FileService;
import com.jlf.music.service.SongInfoService;
import com.jlf.music.utils.CopyUtils;
import com.jlf.music.utils.MinIOPathParser;
import jakarta.annotation.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class SongInfoServiceImpl extends ServiceImpl<SongInfoMapper, SongInfo>
        implements SongInfoService {
    @Resource
    private SongInfoMapper songInfoMapper;
    @Resource
    private FileService fileService;

    /**
     * 分页或通过名称模糊查询歌曲信息
     *
     * @param songQry 分页参数
     * @return songVo
     */
    @Override
    public IPage<SongBasicInfoVo> getSongsByPage(SongQry songQry) {
        IPage<SongInfo> page = new Page<>(songQry.getPageNum(), songQry.getPageSize());
        return songInfoMapper.getSongsByPage(page, songQry);
    }

    /**
     * 获取最近的歌曲信息
     *
     * @return List<SongBasicInfoVo>
     */
    @Override
    public List<SongBasicInfoVo> getLatestSongs() {
        return songInfoMapper.findLatestSongs();
    }

    /**
     * 根据songId获取歌曲音频和歌词文件url
     *
     * @param songId 歌曲ID
     * @return SongLyricsAndAudioVo
     */
    @Override
    public SongLyricsAndAudioVo getSongAudioAndLyricsById(Long songId) {
        SongInfo songInfo = songInfoMapper.selectById(songId);
        if (songInfo == null) {
            throw new ServiceException("歌曲不存在");
        }
        return CopyUtils.classCopy(songInfo, SongLyricsAndAudioVo.class);

    }

    /**
     * 下载歌曲
     *
     * @param songId 歌曲id
     * @return ResponseEntity<InputStreamResource>
     */
    @Override
    public ResponseEntity<InputStreamResource> downloadSong(Long songId) {
        SongInfo songInfo = songInfoMapper.selectById(songId);
        if (songInfo == null) {
            throw new ServiceException("歌曲不存在");
        }
        String filePath = songInfo.getSongFilePath();
        // 解析桶名和文件名
        MinIOPathParser.BucketAndFileName bucketAndFileName = MinIOPathParser.parseBucketAndFileName(filePath);
        String bucketName = bucketAndFileName.bucketName();
        String fileName = bucketAndFileName.fileName();
        // 获取音频流
        InputStream inputStream = fileService.downloadFile(bucketName, fileName);
        // 设置HTTP响应头
        HttpHeaders httpHeaders = new HttpHeaders();
        // 使用 URL 编码处理文件名
        String encodedFileName = URLEncoder.encode(songInfo.getSongName() + ".mp3", StandardCharsets.UTF_8);

        // 设置 Content-Disposition
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");

        // 响应内容为MP3音频数据
        httpHeaders.setContentType(MediaType.valueOf("audio/mpeg"));
        // 返回流数据
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(new InputStreamResource(inputStream));
    }
}
