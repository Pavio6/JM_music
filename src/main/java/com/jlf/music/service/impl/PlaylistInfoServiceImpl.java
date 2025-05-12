package com.jlf.music.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.PageRequest;
import com.jlf.music.common.enumerate.CreatorType;
import com.jlf.music.common.enumerate.TagType;
import com.jlf.music.common.enumerate.TargetType;
import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.controller.dto.EditPlaylistDTO;
import com.jlf.music.controller.dto.PlaylistDetailDTO;
import com.jlf.music.controller.dto.PlaylistFormDTO;
import com.jlf.music.controller.qry.PlaylistCollectQry;
import com.jlf.music.controller.qry.PlaylistPageQry;
import com.jlf.music.controller.vo.PlaylistBasicInfoVo;
import com.jlf.music.controller.vo.SimpleItemVo;
import com.jlf.music.controller.vo.PlaylistSongVo;
import com.jlf.music.entity.*;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.*;
import com.jlf.music.service.*;
import com.jlf.music.utils.CopyUtils;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.jlf.music.common.constant.MusicConstant.*;

@Service
@Slf4j
public class PlaylistInfoServiceImpl extends ServiceImpl<PlaylistInfoMapper, PlaylistInfo> implements PlaylistInfoService {
    @Resource
    private PlaylistInfoMapper playlistInfoMapper;
    @Resource
    private SongInfoMapper songInfoMapper;
    @Resource
    private FileService fileService;
    @Resource
    private PlaylistTagsMapper playlistTagsMapper;
    @Resource
    private PlaylistSongMapper playlistSongMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private TagsInfoMapper tagsInfoMapper;
    @Resource
    private PlaylistTagsService playlistTagsService;
    @Resource
    private PlaylistSongService playlistSongService;
    @Resource
    private UserFavoriteMapper userFavoriteMapper;
    @Autowired
    private AlbumInfoMapper albumInfoMapper;

    /**
     * 创建歌单
     *
     * @param playlistName 歌单名称
     * @return Boolean
     */
    @Override
    public Boolean createPlaylist(String playlistName) {
        if (StrUtil.isBlank(playlistName)) {
            throw new ServiceException("请输入歌单名称");
        }
        if (StrUtil.length(playlistName) > 20) {
            throw new ServiceException("歌单名称长度不能超过20个字符");
        }
        // 获取用户id
        Long userId = SecurityUtils.getUserId();
        SysUser sysUser = sysUserMapper.selectById(userId);
        // 同一个用户不能创建相同名称的歌单
        LambdaQueryWrapper<PlaylistInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlaylistInfo::getCreatorId, userId).eq(PlaylistInfo::getPlaylistName, playlistName);
        if (playlistInfoMapper.selectCount(wrapper) > 0) {
            throw new ServiceException("歌单名已存在");
        }
        PlaylistInfo playlistInfo = new PlaylistInfo();
        playlistInfo.setPlaylistName(playlistName).setCreatorId(userId).setCreatorType(sysUser.getType() == 0 ? CreatorType.USER : CreatorType.ADMIN);
        return playlistInfoMapper.insert(playlistInfo) > 0;
    }

    /**
     * 添加歌曲到歌单中
     *
     * @param playlistId 歌单ID
     * @param songIds    歌曲ID列表
     * @return Boolean
     */
    @Transactional
    @Override
    public Boolean addSongsToPlaylist(Long playlistId, List<Long> songIds) {
        // 歌单是否存在
        PlaylistInfo playlistInfo = playlistInfoMapper.selectById(playlistId);
        if (playlistInfo == null) {
            throw new ServiceException("歌单不存在");
        }
        // 歌曲是否是存在数据库中
        List<SongInfo> songList = songInfoMapper.selectBatchIds(songIds);
        if (songIds.size() != songList.size()) {
            throw new ServiceException("部分歌曲不存在, 插入失败!");
        }
        // 歌曲是否已经存在歌单中
        for (Long songId : songIds) {
            LambdaQueryWrapper<PlaylistSong> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PlaylistSong::getPlaylistId, playlistId).eq(PlaylistSong::getSongId, songId);
            if (playlistSongMapper.selectCount(wrapper) > 0) {
                throw new ServiceException("不能重复添加歌曲");
            }
        }
        // 批量插入新关联
        List<PlaylistSong> playlistSongs = songIds.stream().map(songId -> new PlaylistSong(playlistId, songId)).toList();
        // 批量插入歌曲
        return playlistSongMapper.insertBatch(playlistSongs) == songIds.size();
    }

    /**
     * 编辑歌单属性
     *
     * @param playlistId      歌单id
     * @param editPlaylistDTO 编辑的值
     * @param playlistCover   歌单封面图
     * @return Boolean
     */
    @Override
    @Transactional
    public Boolean editPlaylistProperties(Long playlistId, EditPlaylistDTO editPlaylistDTO, MultipartFile playlistCover) {
        // 歌单是否存在
        PlaylistInfo playlistInfo = playlistInfoMapper.selectById(playlistId);
        if (playlistInfo == null) {
            throw new ServiceException("歌单不存在");
        }
        // 使用 LambdaUpdateWrapper 构建更新条件和更新内容
        LambdaUpdateWrapper<PlaylistInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PlaylistInfo::getPlaylistId, playlistId);
        // 检查歌单名称是否为空
        String playlistName = editPlaylistDTO.getPlaylistName();
        // 歌单名称是否为空
        if (!StrUtil.isBlank(playlistName)) {
            // 歌单名称长度是否大于20
            if (StrUtil.length(playlistName) > 20) {
                throw new ServiceException("歌单名称长度不能超过20个字符");
            } else {
                // 获取用户id
                Long userId = SecurityUtils.getUserId();
                // 同一个用户不能创建相同名称的歌单
                LambdaQueryWrapper<PlaylistInfo> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(PlaylistInfo::getCreatorId, userId).eq(PlaylistInfo::getPlaylistName, playlistName);
                if (playlistInfoMapper.selectCount(wrapper) > 0) {
                    throw new ServiceException("歌单名已存在");
                } else {
                    // 设置更新条件
                    updateWrapper.set(PlaylistInfo::getPlaylistName, playlistName);
                }
            }
        }
        // 歌单简介是否为空
        if (!StrUtil.isBlank(editPlaylistDTO.getPlaylistBio())) {
            if (editPlaylistDTO.getPlaylistBio().length() > 500) {
                throw new ServiceException("歌单简介长度不能超过500字");
            }
            updateWrapper.set(PlaylistInfo::getPlaylistBio, editPlaylistDTO.getPlaylistBio());
        }
        // 歌单封面图是否为空
        if (playlistCover != null && !playlistCover.isEmpty()) {
            // 查询歌单的封面图
            PlaylistInfo info = playlistInfoMapper.selectById(playlistId);
            if (info.getPlaylistCover() != null) {
                // 删除存储在minio中的歌单封面图
                fileService.deleteFile(info.getPlaylistCover());
            }
            // 更新歌单封面图
            String coverUrl = fileService.uploadImageFile(playlistCover, UploadFileType.PLAYLIST_COVER);
            updateWrapper.set(PlaylistInfo::getPlaylistCover, coverUrl);
        }
        // 执行更新操作
        playlistInfoMapper.update(null, updateWrapper);
        // 标签是否为空
        if (!CollUtil.isEmpty(editPlaylistDTO.getTagIds())) {
            // 标签个数是否超过5个
            if (editPlaylistDTO.getTagIds().size() > 5) {
                throw new ServiceException("歌单标签最多五个");
            }
            List<PlaylistTags> playlistTags = playlistTagsMapper.selectList(new LambdaQueryWrapper<PlaylistTags>().eq(PlaylistTags::getPlaylistId, playlistId));
            // 如果之前歌单中有标签 则删除之前的 没有则直接添加
            if (!playlistTags.isEmpty()) {
                for (PlaylistTags tag : playlistTags) {
                    playlistTagsMapper.delete(new LambdaQueryWrapper<PlaylistTags>().eq(PlaylistTags::getPlaylistId, tag.getPlaylistId()).eq(PlaylistTags::getTagId, tag.getTagId()));
                }
            }
            // 将列表转换为流并过滤 返回List集合
            List<PlaylistTags> newPlaylistTags = editPlaylistDTO.getTagIds().stream().map(tagId -> new PlaylistTags(playlistId, tagId)).toList();
            // 插入歌单-标签list
            playlistTagsService.saveBatch(newPlaylistTags);
        }
        return true;
    }

    /**
     * 分页查询歌单列表 支持分类查询
     *
     * @param playlistPageQry 查询参数
     * @return IPage<List < PlaylistBasicInfoVo>>
     */
    @Override
    public IPage<PlaylistBasicInfoVo> getPlaylistPage(PlaylistPageQry playlistPageQry) {
        Page<PlaylistInfo> page = new Page<>(playlistPageQry.getPageNum(), playlistPageQry.getPageSize());
        if (ObjectUtil.isNotEmpty(playlistPageQry.getTagType())) {
            log.info("tagType: {}", playlistPageQry.getTagType());
            return playlistSongMapper.selectPlaylistByTagType(page, TagType.getValueByName(playlistPageQry.getTagType()));
        }
        // 如果传递tagId 查询所有拥有tagId的歌单列表
        if (ObjectUtil.isNotEmpty(playlistPageQry.getTagId())) {
            if (tagsInfoMapper.selectById(playlistPageQry.getTagId()) == null) {
                throw new ServiceException("标签id不存在");
            }
            // 查询所有拥有该标签的记录
            List<PlaylistTags> playlistTags = playlistTagsMapper.selectList(new LambdaQueryWrapper<PlaylistTags>().eq(PlaylistTags::getTagId, playlistPageQry.getTagId()));
            if (CollectionUtils.isEmpty(playlistTags)) {
                return new Page<>(playlistPageQry.getPageNum(),
                        playlistPageQry.getPageSize(),
                        0L);
            }
            // 过滤出所有歌单id
            List<Long> playlistIds = playlistTags.stream().map(PlaylistTags::getPlaylistId).toList();
            // 通过歌单id 获取所有歌单
            page = playlistInfoMapper.selectPage(page, new LambdaQueryWrapper<PlaylistInfo>().in(PlaylistInfo::getPlaylistId, playlistIds));
            return CopyUtils.covertPage(page, PlaylistBasicInfoVo.class);

        }
        // 返回默认的歌单列表
        return CopyUtils.covertPage(playlistInfoMapper.selectPage(page, null), PlaylistBasicInfoVo.class);
    }

    /**
     * 根据id获取歌单详细信息
     *
     * @param playlistId 歌单id
     * @return PlaylistDetailDTO
     */
    @Override
    public PlaylistDetailDTO getPlaylistDetailById(Long playlistId) {
        // 获取歌单详细信息
        PlaylistDetailDTO playlistDetail = playlistInfoMapper.findPlaylistDetail(playlistId);
        // 判断该歌单是否被该用户收藏
        Long userId = SecurityUtils.getUserId();
        if (userFavoriteMapper.selectOne(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTargetType, TargetType.PLAYLIST.getValue())
                .eq(UserFavorite::getTargetId, playlistId)) == null) {
            return playlistDetail.setIsCollected(false);
        }
        return playlistDetail.setIsCollected(true);
    }

    /**
     * 增加播放量
     * 定期同步到数据库
     *
     * @param playlistId   歌单 ID
     * @param playDuration 播放时长
     */
    @Override
    public Boolean incrementPlayCount(Long playlistId, Integer playDuration) {
        Long userId = SecurityUtils.getUserId();
        if (playDuration < PLAY_DURATION_THRESHOLD) {
            // 如果播放时长未达到阈值，则不增加播放量
            throw new ServiceException("播放时长未达到阈值, 不增加播放量");
        }
        // TODO 暂时不记录用户播放记录
        // userListeningRecordService.recordListening(songId, playDuration);
        // 播放量
//        String playCountKey = PLAYLIST_PLAY_COUNT_KEY_PREFIX + playlistId;
        // 检查用户是否在重复播放
        String lastPlayTimeKey = "playlist:lastPlayTime:" + userId + ":" + playlistId;
        // 获取用户最后一次播放该歌单的时间
        String lastPlayTimeStr = stringRedisTemplate.opsForValue().get(lastPlayTimeKey);
        // 转为long类型 不存在则为0
        long lastPlayTime = lastPlayTimeStr != null ? Long.parseLong(lastPlayTimeStr) : 0;
        // 获取当前时间(秒)
        long currentTime = System.currentTimeMillis() / 1000;
        // 当前时间距离最后一次播放时间已经3600秒
        if (currentTime - lastPlayTime > REPEAT_PLAY_WINDOW) {
            // 增加播放量
//            stringRedisTemplate.opsForValue().increment(playCountKey, 1); // 增加播放量
            stringRedisTemplate.opsForValue().set(lastPlayTimeKey, String.valueOf(currentTime)); // 更新最后播放时间
        }
        return true;
    }

    /**
     * 分页获取用户创建的歌单列表
     *
     * @param userId      用户id
     * @param pageRequest 分页参数
     * @return IPage<PlaylistBasicInfoVo>
     */
    @Override
    public IPage<PlaylistBasicInfoVo> getPlaylistsByUserId(Long userId, PageRequest pageRequest) {
        Page<PlaylistInfo> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        page = playlistInfoMapper.selectPage(page, new LambdaQueryWrapper<PlaylistInfo>()
                .eq(PlaylistInfo::getCreatorId, userId));
        List<PlaylistBasicInfoVo> list = page.getRecords().stream()
                .map(playlist -> new PlaylistBasicInfoVo()
                        .setPlaylistId(playlist.getPlaylistId())
                        .setPlaylistName(playlist.getPlaylistName())
                        .setPlaylistCover(playlist.getPlaylistCover()))
                .collect(Collectors.toList());
        return new Page<PlaylistBasicInfoVo>().setRecords(list)
                .setSize(page.getSize())
                .setTotal(page.getTotal())
                .setCurrent(page.getCurrent())
                .setPages(page.getPages());
    }

    /**
     * 获取歌单列表
     */
    @Override
    public IPage<PlaylistBasicInfoVo> selectPlaylist(PlaylistPageQry playlistPageQry) {
        Page<PlaylistBasicInfoVo> page = new Page<>(playlistPageQry.getPageNum(), playlistPageQry.getPageSize());
        IPage<PlaylistBasicInfoVo> playlistPage = playlistInfoMapper.getPlaylistList(page, playlistPageQry);
        List<PlaylistBasicInfoVo> playlistList = playlistPage.getRecords();
        for (PlaylistBasicInfoVo vo : playlistList) {
            List<TagsInfo> tags = playlistInfoMapper.getTags(vo.getPlaylistId());
            vo.setTags(tags);
            List<PlaylistSongVo> songs = playlistInfoMapper.getSongs(vo.getPlaylistId());
            vo.setSongs(songs);
        }
        return playlistPage;
    }

    /**
     * 添加歌单
     */
    @Override
    @Transactional
    public Boolean addPlaylist(PlaylistFormDTO playlistFormDTO, MultipartFile playlistCoverFile) {
        PlaylistInfo playlistInfo = new PlaylistInfo();
        if (playlistFormDTO.getPlaylistName() != null && !playlistFormDTO.getPlaylistName().isBlank()) {
            if (playlistInfoMapper.selectOne(new LambdaQueryWrapper<PlaylistInfo>().eq(PlaylistInfo::getPlaylistName, playlistFormDTO.getPlaylistName())) != null) {
                throw new ServiceException("歌单已存在, 不能重复创建歌单");
            }
            playlistInfo.setPlaylistName(playlistFormDTO.getPlaylistName());
        }
        String playlistCover = playlistCoverFile != null ? fileService.uploadImageFile(playlistCoverFile, UploadFileType.PLAYLIST_COVER) : null;
        playlistInfo.setPlaylistCover(playlistCover).setCreatorType(CreatorType.ADMIN).setCreatorId(SecurityUtils.getUserId()).setPlaylistBio(playlistFormDTO.getPlaylistBio());
        playlistInfoMapper.insert(playlistInfo);
        if (playlistFormDTO.getTagIds() != null && !playlistFormDTO.getTagIds().isEmpty()) {
            if (playlistFormDTO.getTagIds().size() > 3) {
                throw new ServiceException("歌单标签不能超过3个");
            }
            List<PlaylistTags> playlistTags = new ArrayList<>();
            for (Long tagId : playlistFormDTO.getTagIds()) {
                playlistTags.add(new PlaylistTags(playlistInfo.getPlaylistId(), tagId));
            }
            playlistTagsService.saveBatch(playlistTags);
        }
        if (playlistFormDTO.getSongIds() != null && !playlistFormDTO.getSongIds().isEmpty()) {
            List<PlaylistSong> playlistSongs = new ArrayList<>();
            for (Long songId : playlistFormDTO.getSongIds()) {
                playlistSongs.add(new PlaylistSong(playlistInfo.getPlaylistId(), songId));
            }
            playlistSongService.saveBatch(playlistSongs);
        }
        return true;
    }

    /**
     * 更新歌单
     */
    @Override
    @Transactional
    public Boolean updatePlaylist(PlaylistFormDTO playlistFormDTO, MultipartFile playlistCoverFile, Long playlistId) {
        PlaylistInfo playlistInfo = this.getById(playlistId);
        if (playlistInfo == null) {
            throw new ServiceException("歌单不存在");
        }
        String playlistCover = playlistCoverFile != null ? fileService.uploadImageFile(playlistCoverFile, UploadFileType.PLAYLIST_COVER) : null;
        LambdaUpdateWrapper<PlaylistInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PlaylistInfo::getPlaylistId, playlistId);
        PlaylistInfo updateInfo = new PlaylistInfo();
        updateInfo.setPlaylistName(playlistFormDTO.getPlaylistName()).setStatus(playlistFormDTO.getStatus()).setPlaylistBio(playlistFormDTO.getPlaylistBio());
        if (playlistCover != null) {
            // 删除之前的图片
            fileService.deleteFile(playlistInfo.getPlaylistCover());
            updateInfo.setPlaylistCover(playlistCover);
        }
        // 更新playlistInfo
        this.update(updateInfo, wrapper);
        // 删除旧的tags 并更新新的tags
        /*List<Long> oldTagsId = playlistTagsMapper.selectList(new LambdaQueryWrapper<PlaylistTags>()
                        .eq(PlaylistTags::getPlaylistId, playlistId))
                .stream()
                .map(PlaylistTags::getTagId)
                .toList();*/
        playlistTagsMapper.delete(new LambdaQueryWrapper<PlaylistTags>().eq(PlaylistTags::getPlaylistId, playlistId));
        // 循环更新新的tags
        for (Long tagId : playlistFormDTO.getTagIds()) {
            PlaylistTags playlistTags = new PlaylistTags(playlistInfo.getPlaylistId(), tagId);
            playlistTagsMapper.insert(playlistTags);
        }
        /*List<Long> oldSongsId = playlistSongMapper.selectList(new LambdaQueryWrapper<PlaylistSong>()
                        .eq(PlaylistSong::getPlaylistId, playlistId))
                .stream()
                .map(PlaylistSong::getSongId)
                .toList();*/
        playlistSongMapper.delete(new LambdaQueryWrapper<PlaylistSong>().eq(PlaylistSong::getPlaylistId, playlistId));

        for (Long songId : playlistFormDTO.getSongIds()) {
            PlaylistSong playlistSong = new PlaylistSong(playlistInfo.getPlaylistId(), songId);
            playlistSongMapper.insert(playlistSong);
        }
        return true;
    }

    /**
     * 获取管理端歌单详情
     */
    @Override
    public PlaylistBasicInfoVo getAdminPlaylistDetail(Long playlistId) {
        PlaylistInfo playlistInfo = this.getById(playlistId);
        PlaylistBasicInfoVo vo = CopyUtils.classCopy(playlistInfo, PlaylistBasicInfoVo.class);
        vo.setCreatorName(sysUserMapper.selectById(playlistInfo.getCreatorId()).getUserName()).setCreateTime(playlistInfo.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        List<TagsInfo> tags = playlistInfoMapper.getTags(playlistId);
        vo.setTags(tags);
        List<PlaylistSongVo> songs = playlistInfoMapper.getSongs(playlistId);
        vo.setSongs(songs);
        return vo;
    }

    /**
     * 删除歌单
     */
    @Override
    @Transactional
    public Boolean deletePlaylist(Long playlistId) {
        PlaylistInfo playlistInfo = this.getById(playlistId);
        if (playlistInfo == null) {
            throw new ServiceException("该歌单不存在");
        }
        // 查询是否有用户收藏该歌单
        Long count = userFavoriteMapper.selectCount(new LambdaQueryWrapper<UserFavorite>().eq(UserFavorite::getTargetType, TargetType.PLAYLIST.getValue()).eq(UserFavorite::getTargetId, playlistId));
        if (count > 0) {
            throw new ServiceException("无法删除歌单，该歌单已被用户收藏");
        }
        // 删除歌单信息 及歌单歌曲信息 歌单标签信息
        playlistInfoMapper.deleteById(playlistId);
        playlistSongMapper.delete(new LambdaQueryWrapper<PlaylistSong>().eq(PlaylistSong::getPlaylistId, playlistId));
        playlistTagsMapper.delete(new LambdaQueryWrapper<PlaylistTags>().eq(PlaylistTags::getPlaylistId, playlistId));
        return true;
    }

    /**
     * 从歌单中移除歌曲
     *
     * @param playlistId 歌单id
     * @param songIds    歌曲id
     * @return boolean
     */
    @Override
    public Boolean removeSongsFromPlaylist(Long playlistId, List<Long> songIds) {
        Long userId = SecurityUtils.getUserId();
        PlaylistInfo playlistInfo = playlistInfoMapper.selectById(playlistId);
        if (playlistInfo == null) {
            throw new ServiceException("歌单不存在");
        }
        // 判断歌单是否是用户创建的
        if (!Objects.equals(playlistInfo.getCreatorId(), userId)) {
            throw new ServiceException("用户只能更改自己的歌单列表");
        }
        // 判断歌曲id是否存在于歌曲歌单表中
        for (Long songId : songIds) {
            PlaylistSong playlistSong = playlistSongMapper.selectOne(new LambdaQueryWrapper<PlaylistSong>().eq(PlaylistSong::getPlaylistId, playlistId).eq(PlaylistSong::getSongId, songId));
            if (playlistSong == null) {
                throw new ServiceException("无法删除，部分歌曲不存在于歌单中");
            }
        }
        // 循环遍历  执行删除操作
        for (Long songId : songIds) {
            playlistSongMapper.delete(new LambdaQueryWrapper<PlaylistSong>().eq(PlaylistSong::getPlaylistId, playlistId).eq(PlaylistSong::getSongId, songId));
        }
        return true;
    }

    /**
     * 获取用户收藏列表
     */
    @Override
    public IPage<SimpleItemVo> getPlaylistCollectByUserId(Long userId, PlaylistCollectQry playlistCollectQry) {
        if (sysUserMapper.selectById(userId) == null) {
            throw new ServiceException("该用户不存在");
        }
        Page<SimpleItemVo> page = new Page<>(playlistCollectQry.getPageNum(), playlistCollectQry.getPageSize());
        if (Objects.equals(playlistCollectQry.getType(), "PLAYLIST")) {
            return userFavoriteMapper.selectUserPlaylistCollect(
                    page,
                    userId,
                    TargetType.PLAYLIST.getValue()
            );
        } else if (Objects.equals(playlistCollectQry.getType(), "ALBUM")) {
            return userFavoriteMapper.selectUserAlbumCollect(
                    page,
                    userId,
                    TargetType.ALBUM.getValue()
            );
        } else {
            throw new ServiceException("类型只能是歌单或专辑");
        }
    }

    /**
     * 获取用户个人创建的歌单列表
     */
    @Override
    public IPage<PlaylistBasicInfoVo> getPlaylistsMine(PageRequest pageRequest) {
        Long userId = SecurityUtils.getUserId();
        return this.getPlaylistsByUserId(userId, pageRequest);
    }

}
