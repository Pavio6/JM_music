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
import com.jlf.music.common.enumerate.UploadFileType;
import com.jlf.music.controller.dto.EditPlaylistDTO;
import com.jlf.music.controller.dto.PlaylistDetailDTO;
import com.jlf.music.controller.qry.PlaylistPageQry;
import com.jlf.music.controller.vo.PlaylistBasicInfoVo;
import com.jlf.music.entity.*;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.*;
import com.jlf.music.service.FileService;
import com.jlf.music.service.PlaylistInfoService;
import com.jlf.music.service.SysUserService;
import com.jlf.music.service.UserListeningRecordService;
import com.jlf.music.utils.CopyUtils;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.jlf.music.common.constant.PlaylistConstant.*;

@Service
public class PlaylistInfoServiceImpl extends ServiceImpl<PlaylistInfoMapper, PlaylistInfo>
        implements PlaylistInfoService {
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
    private UserListeningRecordService userListeningRecordService;

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
        wrapper.eq(PlaylistInfo::getCreatorId, userId)
                .eq(PlaylistInfo::getPlaylistName, playlistName);
        if (playlistInfoMapper.selectCount(wrapper) > 0) {
            throw new ServiceException("歌单名已存在");
        }
        PlaylistInfo playlistInfo = new PlaylistInfo();
        playlistInfo.setPlaylistName(playlistName)
                .setCreatorId(userId)
                .setCreatorType(sysUser.getType() == 0 ? CreatorType.USER : CreatorType.ADMIN);
        return playlistInfoMapper.insert(playlistInfo) > 0;
    }

    /**
     * 添加歌曲到歌单中
     *
     * @param id      歌单ID
     * @param songIds 歌曲ID列表
     * @return Boolean
     */
    @Transactional
    @Override
    public Boolean addSongsToPlaylist(Long id, List<Long> songIds) {
        // 歌单是否存在
        PlaylistInfo playlistInfo = playlistInfoMapper.selectById(id);
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
            wrapper.eq(PlaylistSong::getPlaylistId, id)
                    .eq(PlaylistSong::getSongId, songId);
            if (playlistSongMapper.selectCount(wrapper) > 0) {
                throw new ServiceException("不能重复添加歌曲");
            }
        }
        // 批量插入新关联
        List<PlaylistSong> playlistSongs = songIds.stream()
                .map(songId -> new PlaylistSong(id, songId))
                .toList();
        // 批量插入歌曲
        return playlistSongMapper.insertBatch(playlistSongs) == songIds.size();
    }

    /**
     * 编辑歌单属性
     *
     * @param id              歌单id
     * @param editPlaylistDTO 编辑的值
     * @param playlistCover   歌单封面图
     * @return Boolean
     */
    @Override
    @Transactional
    public Boolean editPlaylistProperties(Long id, EditPlaylistDTO editPlaylistDTO, MultipartFile playlistCover) {
        // 歌单是否存在
        PlaylistInfo playlistInfo = playlistInfoMapper.selectById(id);
        if (playlistInfo == null) {
            throw new ServiceException("歌单不存在");
        }
        // 使用 LambdaUpdateWrapper 构建更新条件和更新内容
        LambdaUpdateWrapper<PlaylistInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PlaylistInfo::getPlaylistId, id);
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
                wrapper.eq(PlaylistInfo::getCreatorId, userId)
                        .eq(PlaylistInfo::getPlaylistName, playlistName);
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
            updateWrapper.set(PlaylistInfo::getPlaylistBio, editPlaylistDTO.getPlaylistBio());
        }
        // 歌单封面图是否为空
        if (playlistCover != null && !playlistCover.isEmpty()) {
            // 查询歌单的封面图
            PlaylistInfo info = playlistInfoMapper.selectById(id);
            // 删除存储在minio中的歌单封面图
            fileService.deleteFile(info.getPlaylistCover());
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
            } else {
                // 标签是否已经存在歌单中
                for (Long tagId : editPlaylistDTO.getTagIds()) {
                    LambdaQueryWrapper<PlaylistTags> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(PlaylistTags::getPlaylistId, id)
                            .eq(PlaylistTags::getTagId, tagId);
                    if (playlistTagsMapper.selectCount(wrapper) > 0) {
                        throw new ServiceException("不能添加重复标签");
                    }
                }
                // 将列表转换为流并过滤 返回List集合
                List<PlaylistTags> playlistTags = editPlaylistDTO.getTagIds().stream()
                        .map(tagId -> new PlaylistTags(id, tagId))
                        .toList();
                // 插入歌单-标签list
                playlistTagsMapper.insertBatch(playlistTags);
            }
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
        // 如果传递tagId 查询所有拥有tagId的歌单列表
        if (ObjectUtil.isNotEmpty(playlistPageQry.getTagId())) {
            if (tagsInfoMapper.selectById(playlistPageQry.getTagId()) == null) {
                throw new ServiceException("标签id不存在");
            }
            // 查询所有拥有该标签的记录
            List<PlaylistTags> playlistTags = playlistTagsMapper.selectList(new LambdaQueryWrapper<PlaylistTags>()
                    .eq(PlaylistTags::getTagId, playlistPageQry.getTagId()));
            // 过滤出所有歌单id
            List<Long> playlistIds = playlistTags.stream()
                    .map(PlaylistTags::getPlaylistId)
                    .toList();
            // 通过歌单id 获取所有歌单
            page = playlistInfoMapper.selectPage(page, new LambdaQueryWrapper<PlaylistInfo>()
                    .in(PlaylistInfo::getPlaylistId, playlistIds));
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
        // 歌单播放量+1

        return playlistDetail;
    }

    /**
     * 播放歌单
     *
     * @param playlistId   歌单 ID
     * @param songId       歌曲 ID
     * @param playDuration 播放时长
     */
    @Override
    public Boolean incrementPlayCount(Long playlistId, Long songId, Integer playDuration) {
        Long userId = SecurityUtils.getUserId();
        if (playDuration < PLAY_DURATION_THRESHOLD) {
            // 如果播放时长未达到阈值，则不增加播放量
            throw new ServiceException("播放时长未达到阈值, 不增加播放量");
        }
        // 记录用户歌曲播放
        userListeningRecordService.recordListening(songId, playDuration);
        // 播放量
        String playCountKey = PLAYLIST_PLAY_COUNT_KEY_PREFIX + playlistId;
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
            stringRedisTemplate.opsForValue().increment(playCountKey, 1); // 增加播放量
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
        return null;
    }
}
