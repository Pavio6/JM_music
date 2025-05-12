package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlf.music.common.enumerate.TargetType;
import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.controller.dto.SongFormDTO;
import com.jlf.music.controller.qry.SongQry;
import com.jlf.music.controller.vo.SongDetailVo;
import com.jlf.music.controller.vo.SongLyricsAndAudioVo;
import com.jlf.music.controller.vo.SongBasicInfoVo;
import com.jlf.music.entity.*;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.*;
import com.jlf.music.service.FileService;
import com.jlf.music.service.SongInfoService;
import com.jlf.music.utils.CopyUtils;
import com.jlf.music.utils.MinIOPathParser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jlf.music.common.constant.MusicConstant.*;
import static com.jlf.music.common.constant.RedisConstant.*;

@Service
@Slf4j
public class SongInfoServiceImpl extends ServiceImpl<SongInfoMapper, SongInfo>
        implements SongInfoService {
    private static final Integer SONG_LIMIT = 50;
    private static final int MAX_WEEKS_TO_CHECK = 4; // 最大扩展时间范围（周）
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
    @Resource
    private PlaylistInfoMapper playlistInfoMapper;
    @Resource
    private SongMvMapper songMvMapper;
    @Autowired
    private PlaylistSongMapper playlistSongMapper;
    @Autowired
    private UserFavoriteMapper userFavoriteMapper;
    @Autowired
    private PlayQueueDetailMapper playQueueDetailMapper;

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
     * 默认获取上一周 如果上一周没有歌曲 则递归获取上两周 最多四周
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
        // 定义时间范围和步长
        LocalDate now = LocalDate.now();
        int weeksToCheck = 1; // 初始查询上周的数据
        List<SongBasicInfoVo> newSongs;
        do {
            // 计算查询的时间范围
            // minusWeeks 方法表示从当前日期减去指定的周数
            LocalDate startDate = now.minusWeeks(weeksToCheck);
            LocalDate endDate = now.minusWeeks(weeksToCheck - 1);
            // 查询数据库
            newSongs = queryNewSongsFromDB(startDate, endDate);
            // 如果查询到数据，或者达到最大时间范围，退出循环
            if (!newSongs.isEmpty() || weeksToCheck >= MAX_WEEKS_TO_CHECK) {
                break;
            }
            // 扩大时间范围
            weeksToCheck++;
        } while (true);
        // 如果最终没有查询到数据，返回默认推荐列表
        if (newSongs.isEmpty()) {
            return null;
        }
        // 异步更新缓存
        List<SongBasicInfoVo> finalNewSongs = newSongs;
        CompletableFuture.runAsync(() -> updateCache(finalNewSongs, NEW_SONGS_CACHE_KEY));
        return newSongs;
    }

    /**
     * 获取热歌榜
     * 根据歌曲播放量降序获取热歌榜
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
        if (hotSongs.isEmpty()) {
            return null;
        }
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
        if (risingSongs.isEmpty()) {
            return null;
        }
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
    public Boolean addSong(SongFormDTO songFormDTO, MultipartFile songLyricsFile, MultipartFile songFile, MultipartFile songCoverFile, MultipartFile mvFilePath480p, MultipartFile mvFilePath720p, MultipartFile mvFilePath1080p) {
        String songLyrics = songLyricsFile != null ? fileService.uploadSongFile(songLyricsFile, UploadFileType.SONG_LYRICS) : null;
        String songFilePath = songFile != null ? fileService.uploadSongFile(songFile, UploadFileType.SONG_AUDIO) : null;
        String songCover = songCoverFile != null ? fileService.uploadSongFile(songCoverFile, UploadFileType.SONG_COVER) : null;
        String mv480 = mvFilePath480p != null ? fileService.uploadSongFile(mvFilePath480p, UploadFileType.SONG_MV) : null;
        String mv720 = mvFilePath720p != null ? fileService.uploadSongFile(mvFilePath720p, UploadFileType.SONG_MV) : null;
        String mv1080 = mvFilePath1080p != null ? fileService.uploadSongFile(mvFilePath1080p, UploadFileType.SONG_MV) : null;
        SongInfo songInfo = CopyUtils.classCopy(songFormDTO, SongInfo.class);
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
        songInfoMapper.insert(songInfo);
        // 判断是否需要新增mv
        if (mv480 == null && mv720 == null && mv1080 == null && songFormDTO.getMvReleaseDate() == null && songFormDTO.getMvBio() == null) {
            return true;
        }
        SongMv songMv = new SongMv();
        songMv.setMvBio(songFormDTO.getMvBio())
                .setMvReleaseDate(songFormDTO.getMvReleaseDate())
                .setSongId(songInfo.getSongId())
                .setSongName(songInfo.getSongName());
        if (mv480 != null) {
            songMv.setMvFilePath480p(mv480);
        }
        if (mv720 != null) {
            songMv.setMvFilePath720p(mv720);
        }
        if (mv1080 != null) {
            songMv.setMvFilePath1080p(mv1080);
        }
        return songMvMapper.insert(songMv) > 0;
    }

    /**
     * 根据id增加播放量
     *
     * @param targetId   歌曲/歌单id
     * @param targetType 歌曲/歌单
     * @return Boolean
     */
    @Override
    public Boolean incrementPlayCountByTargetId(Long targetId, String targetType) {

        if (targetType.equals("SONG")) {
            SongInfo songInfo = songInfoMapper.selectById(targetId);
            if (songInfo == null) {
                throw new ServiceException("歌曲id" + targetId + "不存在");
            }
            // 计算今日的歌曲播放量
            // 通过定时任务每天定时将redis中的播放量信息存放到数据库中
            String songDailyPlayCountKey = SONG_DAILY_PLAY_COUNT_KEY_PREFIX + targetId;
            stringRedisTemplate.opsForValue().increment(songDailyPlayCountKey, 1);
        } else if (targetType.equals("PLAYLIST")) {
            PlaylistInfo playlistInfo = playlistInfoMapper.selectById(targetId);
            if (playlistInfo == null) {
                throw new ServiceException("歌单id" + targetId + "不存在");
            }
            // 通过定时任务每天定时将redis中的播放量信息存放到数据库中
            // 歌单播放量key
            String playlistPlayCountKey = PLAYLIST_PLAY_COUNT_KEY_PREFIX + targetId;
            // redis中对应的歌单加一
            stringRedisTemplate.opsForValue().increment(playlistPlayCountKey, 1);
        } else if (targetType.equals("MV")) {
            SongMv songMv = songMvMapper.selectById(targetId);
            if (songMv == null) {
                throw new ServiceException("MV id" + targetId + "不存在");
            }
            String mvDailyPlayCountKey = MV_DAILY_PLAY_COUNT_KEY_PREFIX + targetId;
            stringRedisTemplate.opsForValue().increment(mvDailyPlayCountKey, 1);
        } else {
            throw new ServiceException("类型不匹配");
        }
        return true;
    }

    /**
     * 删除歌曲
     */
    @Override
    public Boolean deleteSongById(Long songId) {
        SongInfo songInfo = this.getById(songId);
        if (songInfo == null) {
            throw new ServiceException("歌曲不存在，删除失败");
        }
        // 判断歌曲是否已被添加到专辑中
        if (albumInfoMapper.selectById(songInfo.getAlbumId()) != null) {
            throw new ServiceException("无法删除歌曲，该歌曲已被添加到专辑中");
        }
        // 执行删除歌曲基本的方法
        deleteSongInfo(songId);
        // 执行删除操作
        return songInfoMapper.deleteById(songId) > 0;
    }

    /**
     * 删除song基本信息
     */
    @Override
    public void deleteSongInfo(Long songId) {
        SongInfo songInfo = songInfoMapper.selectById(songId);
        // 判断歌曲是否已被添加到歌单中
        if (playlistSongMapper.selectCount(new LambdaQueryWrapper<PlaylistSong>()
                .eq(PlaylistSong::getSongId, songId)) > 0) {
            throw new ServiceException("无法删除歌曲，该歌曲已被添加到歌单中");
        }
        // 判断歌曲是否被用户收藏
        if (userFavoriteMapper.selectCount(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getTargetType, TargetType.SONG.getValue())
                .eq(UserFavorite::getTargetId, songId)) > 0) {
            throw new ServiceException("无法删除歌曲，该歌曲已被用户收藏");
        }
        // 判断歌曲是否在用户播放列表中
        if (playQueueDetailMapper.selectCount(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getSongId, songId)) > 0) {
            throw new ServiceException("无法删除歌曲，该歌曲已存在于用户的播放列表中");
        }
        // 删除minio中存储的歌曲文件等信息
        if (songInfo.getSongCover() != null) {
            fileService.deleteFile(songInfo.getSongCover());
        }
        if (songInfo.getSongFilePath() != null) {
            fileService.deleteFile(songInfo.getSongFilePath());
        }
        if (songInfo.getSongLyrics() != null) {
            fileService.deleteFile(songInfo.getSongLyrics());
        }
    }

    /**
     * 查询数据库获取新歌 (最近7天, 默认50条)
     */
    private List<SongBasicInfoVo> queryNewSongsFromDB(LocalDate startDate, LocalDate endDate) {
        // 获取七天前的日期
        return songInfoMapper.selectNewSongs(startDate, endDate);
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
                    2, TimeUnit.HOURS // 设置2小时过期
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
