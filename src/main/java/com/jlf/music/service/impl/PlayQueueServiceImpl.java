package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.enumerate.PlayModeType;
import com.jlf.music.common.enumerate.QueueType;
import com.jlf.music.controller.vo.QueueStateVo;
import com.jlf.music.controller.vo.SongSimpleInfoVo;
import com.jlf.music.entity.PlayQueue;
import com.jlf.music.entity.PlayQueueDetail;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.*;
import com.jlf.music.service.PlayQueueService;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class PlayQueueServiceImpl extends ServiceImpl<PlayQueueMapper, PlayQueue>
        implements PlayQueueService {
    @Resource
    private PlayQueueMapper playQueueMapper;
    @Resource
    private PlayQueueDetailMapper playQueueDetailMapper;
    @Resource
    private SongInfoMapper songInfoMapper;
    @Resource
    private PlaylistSongMapper playlistSongMapper;
    @Resource
    private UserFavoriteMapper userFavoriteMapper;

    /**
     * 创建空的播放队列
     *
     * @param userId 用户id
     */
    @Override
    public Boolean createEmptyQueue(Long userId) {

        if (this.exists(new LambdaQueryWrapper<PlayQueue>()
                .eq(PlayQueue::getUserId, userId))) {
            log.warn("检测到已存在用户队列，用户ID：{}", userId);
            throw new ServiceException("用户因存在队列");
        }
        PlayQueue playQueue = new PlayQueue();
        playQueue.setUserId(userId)
                .setQueueType(QueueType.CUSTOM) // 播放队列类型为自定义
                .setPlayMode(PlayModeType.SEQUENCE) // 播放模式为顺序播放
                .setCurrentIndex(0); // 当前播放索引值
        return this.save(playQueue);
    }

    /**
     * 添加歌曲到队列
     *
     * @param songId 歌曲id
     * @return Boolean
     */
    @Transactional
    @Override
    public Boolean addSongToFront(Long songId) {
        Long userId = SecurityUtils.getUserId();
        // 获取用户的播放队列
        PlayQueue userPlayQueue = this.getOne(new LambdaQueryWrapper<PlayQueue>()
                .eq(PlayQueue::getUserId, userId));
        if (userPlayQueue == null) {
            throw new ServiceException("用户没有播放队列");
        }
        // 如果当前不是自定义队列，转换为自定义队列
        if (!userPlayQueue.getQueueType().equals(QueueType.CUSTOM)) {
            userPlayQueue.setQueueType(QueueType.CUSTOM)
                    .setSourceId(null);
            this.updateById(userPlayQueue);
        }
        // 播放队列为空
        if (playQueueDetailMapper.selectCount(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, userPlayQueue.getId())) == 0L) {
            PlayQueueDetail playQueueDetail = new PlayQueueDetail()
                    .setQueueId(userPlayQueue.getId())
                    .setSongId(songId)
                    .setSort(0);
            playQueueDetailMapper.insert(playQueueDetail);
        }
        // 不为空
        // 新歌所处的位置
        Integer insertPosition = userPlayQueue.getCurrentIndex() + 1;
        // 调整排序
        playQueueDetailMapper.update(null, new LambdaUpdateWrapper<PlayQueueDetail>()
                .setSql("sort = sort + 1")
                .eq(PlayQueueDetail::getQueueId, userPlayQueue.getId()) // queue_id = #{queueId}
                .ge(PlayQueueDetail::getSort, insertPosition)); // sort >= #{insertPosition}
        // 插入新纪录
        PlayQueueDetail detail = new PlayQueueDetail();
        detail.setQueueId(userPlayQueue.getId())
                .setSongId(songId)
                .setSort(insertPosition);
        playQueueDetailMapper.insert(detail);
        // 更新play_queue的currentIndex值
        return this.update(new LambdaUpdateWrapper<PlayQueue>()
                .set(PlayQueue::getCurrentIndex, insertPosition)
                .eq(PlayQueue::getId, userPlayQueue.getId()));
    }

    /**
     * 切换播放模式
     *
     * @param playMode 新的播放模式
     * @return Boolean
     */
    @Override
    public Boolean changePlayMode(PlayModeType playMode) {
        Long userId = SecurityUtils.getUserId();
        return this.update(new LambdaUpdateWrapper<PlayQueue>()
                .eq(PlayQueue::getUserId, userId)
                .set(PlayQueue::getPlayMode, playMode));
    }

    /**
     * 获取当前播放队列状态
     *
     * @return 播放队列数据
     */
    @Override
    public QueueStateVo getCurrentQueue() {
        Long userId = SecurityUtils.getUserId();
        // 判断用户播放队列是否存在
        PlayQueue playQueue = this.getOne(new LambdaQueryWrapper<PlayQueue>()
                .eq(PlayQueue::getUserId, userId));
        if (playQueue == null) {
            throw new ServiceException("用户播放队列不存在");
        }
        // 获取当前播放的索引值
        Integer currentIndex = playQueue.getCurrentIndex();
        // 获取当前播放的索引值对应的歌曲id
        Long currentSongId = playQueueDetailMapper.selectOne(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, playQueue.getId())
                .eq(PlayQueueDetail::getSort, currentIndex)).getSongId();
        // 获取当前播放队列的所有歌曲信息
        List<PlayQueueDetail> playQueueDetails = playQueueDetailMapper.selectList(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, playQueue.getId())
                .orderByAsc(PlayQueueDetail::getSort)); // 按照sort升序
        List<Long> songIds = playQueueDetails.stream()
                .map(PlayQueueDetail::getSongId)
                .toList();
        List<SongSimpleInfoVo> songSimpleInfoVoList = songInfoMapper.selectSongDetails(songIds);
        return new QueueStateVo()
                .setCurrentIndex(currentIndex)
                .setPlayMode(playQueue.getPlayMode())
                .setSongsInfoList(songSimpleInfoVoList)
                .setCurrentSongId(currentSongId);
    }

    /**
     * 切换播放队列类型
     * 完整队列切换操作 清空当前队列并创建新的队列
     *
     * @param queueType 播放队列类型
     * @param sourceId  数据源id 歌单/专辑id 可为空
     * @param songId    定位播放的起始歌曲
     * @return Boolean
     */
    @Override
    public Boolean switchPlayQueue(QueueType queueType, Long sourceId, Long songId) {
        // 参数校验
        EnumSet<QueueType> allQueueTypes = EnumSet.allOf(QueueType.class);
        if (allQueueTypes.contains(queueType)) {
            throw new IllegalStateException("非法的队列类型");
        }
        if ((queueType.equals(QueueType.ALBUM) || queueType.equals(QueueType.PLAYLIST)) && sourceId == null) {
            throw new ServiceException("专辑/歌单类型必须提供数据源id");
        }
        // 获取用户的播放队列
        Long userId = SecurityUtils.getUserId();
        PlayQueue userPlayQueue = this.getOne(new LambdaQueryWrapper<PlayQueue>()
                .eq(PlayQueue::getUserId, userId));
        List<PlayQueueDetail> playQueueDetails = playQueueDetailMapper.selectList(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, userPlayQueue.getId()));
        // 获取用户播放队列中所有歌曲id列表
        List<Long> originalSongIds = playQueueDetails.stream()
                .map(PlayQueueDetail::getSongId)
                .toList();
        // 清空当前播放队列的歌曲信息
        playQueueDetailMapper.delete(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, sourceId)
                .in(PlayQueueDetail::getSongId, originalSongIds));
        // 切换到自定义播放队列 -> 只有在向 歌单 专辑 我的喜欢 中添加其他随机歌曲时会更换为自定义播放队列
        // 所以一般切换播放队列类型 只能为 歌单 专辑 我的喜欢
        // 根据队列类型和sourceId获取所有歌曲
        List<Long> newSongIds = getSongIdsByType(userId, queueType, sourceId);
        // 插入新队列明细
        insertQueueDetails(userPlayQueue.getId(), newSongIds);
        // 查询当前歌曲对应的sort值
        Integer position = playQueueDetailMapper.selectOne(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, userPlayQueue.getId())
                .eq(PlayQueueDetail::getSongId, songId)).getSort();
        // 更新主记录信息
        updateMainQueue(userPlayQueue, queueType, sourceId, position);
        return true;
    }

    /**
     * 更新主队列信息
     */
    private void updateMainQueue(PlayQueue userPlayQueue, QueueType queueType, Long sourceId, Integer position) {
        userPlayQueue.setQueueType(queueType)
                .setSourceId(sourceId)
                .setCurrentIndex(position);
        // 更新操作
        this.updateById(userPlayQueue);
    }

    /**
     * 根据类型获取歌曲ID列表
     */
    private List<Long> getSongIdsByType(Long userId, QueueType queueType, Long sourceId) {
        return switch (queueType) {
            case ALBUM -> songInfoMapper.getAlbumSongIds(sourceId);
            case PLAYLIST -> playlistSongMapper.getPlaylistSongIds(sourceId);
            case FAVORITE -> userFavoriteMapper.getFavoriteSongIds(userId);
            default -> throw new IllegalStateException("未知的队列类型");
        };
    }

    /**
     * 插入队列明细
     */
    private void insertQueueDetails(Long queueId, List<Long> songIds) {
        List<PlayQueueDetail> details = new ArrayList<>();
        for (int i = 0; i < songIds.size(); i++) {
            PlayQueueDetail detail = new PlayQueueDetail();
            detail.setQueueId(queueId);
            detail.setSongId(songIds.get(i));
            detail.setSort(i);
            details.add(detail);
        }
        if (!details.isEmpty()) {
            playQueueDetailMapper.insertBatchSomeColumn(details);
        }
    }

    /**
     * 从播放队列中批量移除歌曲
     *
     * @param songIds 要移除的歌曲 ID 列表
     * @return Boolean
     */
    @Override
    public Boolean removeSongsFromQueue(List<Long> songIds) {
        if (songIds == null || songIds.isEmpty()) {
            return false;
        }
        // 获取当前用户的播放队列
        PlayQueue userPlayQueue = this.getOne(new LambdaQueryWrapper<PlayQueue>()
                .eq(PlayQueue::getUserId, SecurityUtils.getUserId()));
        // 获取播放队列id
        Long queueId = userPlayQueue.getId();

        // 获取当前播放索引
        Integer currentIndex = userPlayQueue.getCurrentIndex();
        // 获取当前播放索引对应的歌曲id
        PlayQueueDetail detail = playQueueDetailMapper.selectOne(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, queueId)
                .eq(PlayQueueDetail::getSort, currentIndex));
        Long songId = detail.getSongId();
        // 删除指定歌曲列表
        playQueueDetailMapper.delete(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, queueId)
                .in(PlayQueueDetail::getSongId, songIds));
        // 调整播放列表中歌曲sort值
        playQueueDetailMapper.updateSortAfterDeleteBatch(queueId);

        // 判断删除的歌曲列表中是否包含当前播放的歌曲索引值
        if (songIds.contains(songId)) {
            // 获取用户歌单列表剩余的歌曲数量
            Long count = playQueueDetailMapper.selectCount(new LambdaQueryWrapper<PlayQueueDetail>()
                    .eq(PlayQueueDetail::getQueueId, queueId));
            // 如果播放列表中没有歌曲
            if (count == 0) {
                // 设置当前索引值为0
                userPlayQueue.setCurrentIndex(0);
                playQueueMapper.updateById(userPlayQueue);
            }
            // 随机设置一个剩余歌曲的索引值
            Random random = new Random();
            userPlayQueue.setCurrentIndex(random.nextInt(count.intValue()));
            playQueueMapper.updateById(userPlayQueue);
        }
        return true;
    }
}
