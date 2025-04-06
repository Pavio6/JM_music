package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.controller.dto.SongFormDTO;
import com.jlf.music.controller.qry.SongQry;
import com.jlf.music.controller.vo.SongDetailVo;
import com.jlf.music.controller.vo.SongLyricsAndAudioVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.SongInfo;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.AlbumInfoMapper;
import com.jlf.music.mapper.SingerInfoMapper;
import com.jlf.music.mapper.SongInfoMapper;
import com.jlf.music.service.FileService;
import com.jlf.music.service.SongInfoService;
import com.jlf.music.utils.CopyUtils;
import com.jlf.music.utils.MinIOPathParser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jlf.music.common.constant.RedisConstant.*;

@Service
@Slf4j
public class SongInfoServiceImpl extends ServiceImpl<SongInfoMapper, SongInfo>
        implements SongInfoService {
    private static final Integer SONG_LIMIT = 50;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private SongInfoMapper songInfoMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private FileService fileService;
    @Resource
    private SingerInfoMapper singerInfoMapper;
    @Resource
    private AlbumInfoMapper albumInfoMapper;

    /**
     * 分页或通过名称模糊查询歌曲信息
     *
     * @param songQry 分页参数
     * @return songVo
     */
    @Override
    public IPage<SongBasicInfoVo> getSongsByPage(SongQry songQry) {
        IPage<SongBasicInfoVo> page = new Page<>(songQry.getPageNum(), songQry.getPageSize());
        return songInfoMapper.getSongsByPage(page, songQry);
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

    /**
     * 获取新歌榜
     *
     * @return 歌曲列表
     */
    @Override
    public List<SongBasicInfoVo> getNewSongs() {
        // 尝试从缓存中获取
        String cacheData = stringRedisTemplate.opsForValue().get(NEW_SONGS_CACHE_KEY);
        if (StringUtils.hasText(cacheData)) {
            // 反序列化缓存中的数据
            return deserializeSongs(cacheData);
        }
        // 缓存未命中 查询数据库
        List<SongBasicInfoVo> newSongs = queryNewSongsFromDB();
        // 如果新歌榜为空 不需要向数据库中存
        if (newSongs.isEmpty()) {
            return newSongs;
        }
        // 异步更新缓存（不阻塞当前请求）
        CompletableFuture.runAsync(() -> updateCache(newSongs, NEW_SONGS_CACHE_KEY));
        return newSongs;
    }

    /**
     * 获取热歌榜
     *
     * @return 歌曲列表
     */
    @Override
    public List<SongBasicInfoVo> getHotSongs() {
        String cacheDate = stringRedisTemplate.opsForValue().get(HOT_SONGS_CACHE_KEY);
        if (StringUtils.hasText(cacheDate)) {
            return deserializeSongs(cacheDate);
        }
        List<SongBasicInfoVo> hotSongs = songInfoMapper.selectHotSongs();
        CompletableFuture.runAsync(() -> updateCache(hotSongs, HOT_SONGS_CACHE_KEY));
        return hotSongs;
    }

    /**
     * 获取飙升榜
     *
     * @return 歌曲列表
     */
    @Override
    public List<SongBasicInfoVo> getRisingSongs() {
        String cacheDate = stringRedisTemplate.opsForValue().get(RISING_SONGS_CACHE_KEY);
        if (StringUtils.hasText(cacheDate)) {
            return deserializeSongs(cacheDate);
        }
        List<SongBasicInfoVo> risingSongs = songInfoMapper.selectRisingSongs();
        CompletableFuture.runAsync(() -> updateCache(risingSongs, RISING_SONGS_CACHE_KEY));
        return risingSongs;
    }

    /**
     * 更新歌曲
     */

    @Override
    @Transactional
    public Boolean updateSong(Long songId, MultipartFile songLyricsFile, MultipartFile songFile, MultipartFile songCoverFile, SongFormDTO songFormDTO) {
        String songLyrics = songLyricsFile != null ? fileService.uploadSongFile(songLyricsFile, UploadFileType.SONG_LYRICS) : null;
        String songFilePath = songFile != null ? fileService.uploadSongFile(songFile, UploadFileType.SONG_AUDIO) : null;
        String songCover = songCoverFile != null ? fileService.uploadImageFile(songCoverFile, UploadFileType.SONG_COVER) : null;
        // 查询当前歌曲信息
        SongInfo songInfo = songInfoMapper.selectById(songId);
        if (songInfo == null) {
            throw new ServiceException("歌曲不存在");
        }
        // 更新非空字段
        if (songFormDTO.getSongName() != null && !songFormDTO.getSongName().isBlank()) {
            songInfo.setSongName(songFormDTO.getSongName());
        }
        if (songFormDTO.getSongDuration() != null) {
            songInfo.setSongDuration(songFormDTO.getSongDuration());
        }
        if (songFormDTO.getSongReleaseDate() != null) {
            songInfo.setSongReleaseDate(songFormDTO.getSongReleaseDate());
        }
        if (songFormDTO.getSingerId() != null) {
            songInfo.setSingerId(songFormDTO.getSingerId());
        }
        if (songFormDTO.getAlbumId() != null) {
            songInfo.setAlbumId(songFormDTO.getAlbumId());
        }

        // 更新文件路径
        if (songLyrics != null) {
            // 删除之前存放在minio中的文件
            fileService.deleteFile(songInfo.getSongLyrics());
            songInfo.setSongLyrics(songLyrics);
        }
        if (songFilePath != null) {
            fileService.deleteFile(songInfo.getSongFilePath());
            songInfo.setSongFilePath(songFilePath);
        }
        if (songCover != null) {
            fileService.deleteFile(songInfo.getSongCover());
            songInfo.setSongCover(songCover);
        }

        return songInfoMapper.updateById(songInfo) > 0;
    }

    /**
     * 获取歌曲详情
     *
     * @param songId 歌曲id
     */
    @Override
    public SongDetailVo getSongDetail(Long songId) {
        SongDetailVo songDetailVo = new SongDetailVo();
        SongInfo songInfo = this.getById(songId);
        CopyUtils.classCopy(songInfo, songDetailVo);
        String albumName = albumInfoMapper.selectById(songInfo.getAlbumId()).getAlbumName();
        String singerName = singerInfoMapper.selectById(songInfo.getSingerId()).getSingerName();
        return songDetailVo.setAlbumName(albumName)
                .setSingerName(singerName);

    }

    /**
     * 新增歌曲
     */
    @Override
    @Transactional
    public Boolean addSong(SongFormDTO songFormDTO, MultipartFile songLyricsFile, MultipartFile songFile, MultipartFile songCoverFile) {
        String songLyrics = songLyricsFile != null ? fileService.uploadSongFile(songLyricsFile, UploadFileType.SONG_LYRICS) : null;
        String songFilePath = songFile != null ? fileService.uploadSongFile(songFile, UploadFileType.SONG_AUDIO) : null;
        String songCover = songCoverFile != null ? fileService.uploadSongFile(songCoverFile, UploadFileType.SONG_COVER) : null;
        SongInfo songInfo = new SongInfo();
        CopyUtils.classCopy(songFormDTO, songInfo);
        // 更新文件路径
        if (songLyrics != null) {
            songInfo.setSongLyrics(songLyrics);
        }
        if (songFilePath != null) {
            songInfo.setSongFilePath(songFilePath);
        }
        if (songCover != null) {
            songInfo.setSongCover(songCover);
        }
        // songInfo.setPlayCount(0.0);
        return songInfoMapper.insert(songInfo) > 0;
    }




    /**
     * 查询数据库获取新歌 (最近7天, 默认100条)
     */
    private List<SongBasicInfoVo> queryNewSongsFromDB() {
        // 获取七天前的日期
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        return songInfoMapper.selectNewSongs(startTime, SONG_LIMIT);
    }

    /**
     * TODO 后续添加歌曲时更新缓存使用
     * 添加单曲到缓存（增量更新）
     */
    private void addSongToCache(SongInfo song) {
        try {
            // 获取现有缓存
            List<SongBasicInfoVo> existing = Optional.ofNullable(stringRedisTemplate.opsForValue().get(NEW_SONGS_CACHE_KEY))
                    .map(this::deserializeSongs)
                    .orElseGet(ArrayList::new);

            // 插入新歌并保持排序
            insertSongInOrder(existing, song);

            // 保留前50条
            if (existing.size() > SONG_LIMIT) {
                existing = existing.subList(0, SONG_LIMIT);
            }

            // 更新缓存
            stringRedisTemplate.opsForValue().set(
                    NEW_SONGS_CACHE_KEY,
                    serializeSongs(existing),
                    4, TimeUnit.HOURS // 重置过期时间
            );
        } catch (Exception e) {
            log.error("增量更新缓存失败", e);
        }
    }

    /**
     * 按发布时间插入新歌
     */
    private void insertSongInOrder(List<SongBasicInfoVo> list, SongInfo newSong) {
        SongBasicInfoVo newSongVo = CopyUtils.classCopy(newSong, SongBasicInfoVo.class);
        // 如果列表为空，直接插入
        if (list.isEmpty()) {
            list.add(newSongVo);
            return;
        }
        // 二分查找插入位置
        int index = Collections.binarySearch(
                list,
                newSongVo,
                Comparator.comparing(SongBasicInfoVo::getSongReleaseDate).reversed()
        );

        if (index < 0) {
            index = -(index + 1);
        }
        list.add(index, newSongVo);
    }

    /**
     * 更新缓存（全量替换）
     */
    private void updateCache(List<SongBasicInfoVo> songs, String redisKey) {
        try {
            // 序列化歌曲列表
            String json = serializeSongs(songs);
            stringRedisTemplate.opsForValue().set(
                    redisKey,
                    json,
                    4, TimeUnit.HOURS // 设置4小时过期
            );
        } catch (Exception e) {
            log.error("缓存更新失败", e);
        }
    }

    /**
     * 序列化歌曲列表
     */
    private String serializeSongs(List<SongBasicInfoVo> songs) throws JsonProcessingException {
        return objectMapper.writeValueAsString(
                songs.stream().limit(SONG_LIMIT).collect(Collectors.toList())
        );
    }

    /**
     * 反序列化歌曲列表
     */
    private List<SongBasicInfoVo> deserializeSongs(String json) {
        try {
            return objectMapper.readValue(json,
                    new TypeReference<>() {
                    });
        } catch (Exception e) {
            log.warn("缓存反序列化失败", e);
            return Collections.emptyList();
        }
    }

}
