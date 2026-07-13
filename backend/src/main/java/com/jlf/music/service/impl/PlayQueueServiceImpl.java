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

import java.lang.reflect.Array;
import java.util.*;

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
            throw new ServiceException("用户已存在队列");
        }
        PlayQueue playQueue = new PlayQueue();
        playQueue.setUserId(userId)
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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean addSongToFront(Long songId) {
        Long userId = SecurityUtils.getUserId();
        // 获取用户的播放队列
        PlayQueue userPlayQueue = this.getOne(new LambdaQueryWrapper<PlayQueue>()
                .eq(PlayQueue::getUserId, userId));
        if (userPlayQueue == null) {
            throw new ServiceException("用户没有播放队列");
        }
        // 初始化更新构造器
        LambdaUpdateWrapper<PlayQueue> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PlayQueue::getUserId, userId);
        // 播放队列为空
        // 直接插入记录 并返回
        List<PlayQueueDetail> playQueueDetails = playQueueDetailMapper.selectList(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, userPlayQueue.getId()));
        if (playQueueDetails.isEmpty()) {
            PlayQueueDetail playQueueDetail = new PlayQueueDetail()
                    .setQueueId(userPlayQueue.getId())
                    .setSongId(songId)
                    .setSort(0);
            playQueueDetailMapper.insert(playQueueDetail);
            return true;
        }
        // 不为空 - 找出新歌所处的位置
        // 判断队列中是否已有该歌曲
        // anyMatch - 如果有任意一个元素匹配条件 就返回true
        if (playQueueDetails.stream()
                .anyMatch(detail -> songId.equals(detail.getSongId()))) {
            throw new ServiceException("该用户的队列中已存在该歌曲，不能重复添加。");
        }
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
        return playQueueDetailMapper.insert(detail) > 0;
        // 更新play_queue的currentIndex值
//        updateWrapper.set(PlayQueue::getCurrentIndex, insertPosition);
//        return this.update(updateWrapper);
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
        // 播放队列为空
        if (playQueueDetailMapper.selectCount(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, playQueue.getId())) == 0) {
            return new QueueStateVo()
                    .setCurrentIndex(playQueue.getCurrentIndex())
                    .setCurrentSongId(null)
                    .setSongsInfoList(null);
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
                .setSongsInfoList(songSimpleInfoVoList)
                .setCurrentSongId(currentSongId);
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
    @Transactional(rollbackFor = Exception.class) // 所有异常都会触发回滚
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
        PlayQueueDetail detail = playQueueDetailMapper.selectOne(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, queueId)
                .eq(PlayQueueDetail::getSort, currentIndex));
        // 获取当前播放索引对应的歌曲id
        Long songId = detail.getSongId();
        // 删除指定歌曲列表
        playQueueDetailMapper.delete(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, queueId)
                .in(PlayQueueDetail::getSongId, songIds));
        // 调整播放列表中歌曲sort值
        playQueueDetailMapper.updateSortAfterDeleteBatch(queueId);
        // 判断删除的歌曲列表中是否包含当前播放的歌曲
        if (songIds.contains(songId)) {
            // 获取用户歌单列表剩余的歌曲数量
            Long count = playQueueDetailMapper.selectCount(new LambdaQueryWrapper<PlayQueueDetail>()
                    .eq(PlayQueueDetail::getQueueId, queueId));
            // 如果播放列表中没有歌曲
            if (count == 0) {
                // 设置当前索引值为0
                userPlayQueue.setCurrentIndex(0);
            } else {
                // 随机设置一个剩余歌曲的索引值
                Random random = new Random();
                int position = random.nextInt(count.intValue());
                userPlayQueue.setCurrentIndex(position);
            }
        } else {
            // 获取之前歌曲对应的新的sort值
            Integer sort = playQueueDetailMapper.selectOne(new LambdaQueryWrapper<PlayQueueDetail>()
                    .eq(PlayQueueDetail::getQueueId, queueId)
                    .eq(PlayQueueDetail::getSongId, songId)).getSort();
            // 当前播放歌曲的索引有可能修改
            userPlayQueue.setCurrentIndex(sort);

        }
        // 执行更新操作
        return playQueueMapper.updateById(userPlayQueue) > 0;

    }

    /**
     * 清空当前用户队列信息
     *
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 所有异常都会触发回滚
    public Boolean clearAll() {
        // 获取当前用户id
        Long userId = SecurityUtils.getUserId();
        PlayQueue playQueue = playQueueMapper.selectOne(new LambdaQueryWrapper<PlayQueue>().eq(PlayQueue::getUserId, userId));
        // 获取当前用户的队列id
        Long queueId = playQueue.getId();
        // 删除用户播放队列信息
        playQueueDetailMapper.delete(
                new LambdaQueryWrapper<PlayQueueDetail>().eq(PlayQueueDetail::getQueueId, queueId)
        );
        // 更新主队列的信息
        playQueueMapper.updateById(playQueue.setCurrentIndex(0));
        return true;
    }

    /**
     * 切换整个播放队列中的歌曲
     * 完整队列切换操作 清空当前队列并创建新的队列
     *
     * @return Boolean
     */
    @Override
    public Boolean switchSongsInPlayQueue(List<Long> songIds) {
        // 获取用户的播放队列
        Long userId = SecurityUtils.getUserId();
        PlayQueue userPlayQueue = this.getOne(new LambdaQueryWrapper<PlayQueue>()
                .eq(PlayQueue::getUserId, userId));
        if (userPlayQueue == null) {
            throw new ServiceException("该用户没有播放队列");
        }
        List<PlayQueueDetail> playQueueDetails = playQueueDetailMapper.selectList(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, userPlayQueue.getId()));
        // 获取用户播放队列中所有歌曲id列表
        List<Long> originalSongIds = playQueueDetails.stream()
                .map(PlayQueueDetail::getSongId)
                .toList();
        // 清空当前播放队列的歌曲信息（如果存在） - 有可能该用户没有播放过歌曲
        if (!playQueueDetails.isEmpty()) {
            playQueueDetailMapper.delete(new LambdaQueryWrapper<PlayQueueDetail>()
                    .eq(PlayQueueDetail::getQueueId, userPlayQueue.getId())
                    .in(PlayQueueDetail::getSongId, originalSongIds));
        }
        // 插入新队列明细
        insertQueueDetails(userPlayQueue.getId(), songIds);

        // 更新主记录信息 - 默认将当前播放歌曲 设置为0
        userPlayQueue.setCurrentIndex(0);
        return this.updateById(userPlayQueue);
    }

    /**
     * 修改用户正在播放的队列的歌曲
     *
     * @param songId 歌曲id
     * @return boolean
     */
    @Override
    @Transactional
    public Boolean updateIsPlayingSong(Long songId) {
        Long userId = SecurityUtils.getUserId();
        PlayQueue playQueue = playQueueMapper.selectOne(new LambdaQueryWrapper<PlayQueue>()
                .eq(PlayQueue::getUserId, userId));
        if (playQueue == null) {
            throw new ServiceException("用户不存在播放队列");
        }
        PlayQueueDetail detail = playQueueDetailMapper.selectOne(new LambdaQueryWrapper<PlayQueueDetail>()
                .eq(PlayQueueDetail::getQueueId, playQueue.getId())
                .eq(PlayQueueDetail::getSongId, songId));
        if (detail == null) {
            log.warn("用户队列不存在这首歌: songId: {}", songId);
            throw new ServiceException("用户队列中不存在这首歌");
        }
        playQueue.setCurrentIndex(detail.getSort());
        return playQueueMapper.updateById(playQueue) > 0;

    }
}
