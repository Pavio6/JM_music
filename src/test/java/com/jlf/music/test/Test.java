package com.jlf.music.test;

public class Test {
    /*@Service
    @RequiredArgsConstructor
    public class PlayQueueService {
        private final PlayQueueMapper playQueueMapper;
        private final PlayQueueDetailMapper detailMapper;
        private final StringRedisTemplate redisTemplate;

        // Redis缓存键格式：queue:{userId}
        private static final String REDIS_KEY_PREFIX = "queue:";

        *//**
         * 初始化播放队列（专辑/歌单/喜欢列表）
         * @param userId 用户ID
         * @param queueType 队列类型
         * @param sourceId 数据源ID（专辑ID/歌单ID）
         *//*
        @Transactional
        public void initQueue(Long userId, QueueType queueType, Long sourceId) {
            // 1. 清理旧队列（逻辑删除）
            LambdaQueryWrapper<PlayQueue> query = new LambdaQueryWrapper<>();
            query.eq(PlayQueue::getUserId, userId)
                    .eq(PlayQueue::getDeleted, 0);
            PlayQueue existQueue = playQueueMapper.selectOne(query);

            if(existQueue != null) {
                logicalDeleteQueue(existQueue.getId());
            }

            // 2. 创建新队列
            PlayQueue newQueue = new PlayQueue()
                    .setUserId(userId)
                    .setQueueType(queueType.name())
                    .setSourceId(sourceId)
                    .setCurrentIndex(0)
                    .setPlayMode(PlayMode.SEQUENCE.name());
            playQueueMapper.insert(newQueue);

            // 3. 获取歌曲列表（需要实现不同类型的数据获取）
            List<Long> songIds = fetchSongIdsByType(queueType, sourceId);

            // 4. 插入队列明细
            List<PlayQueueDetail> details = new ArrayList<>();
            for(int i=0; i<songIds.size(); i++) {
                details.add(new PlayQueueDetail()
                        .setQueueId(newQueue.getId())
                        .setSongId(songIds.get(i))
                        .setSort(i));
            }
            // 批量插入
            if(!details.isEmpty()) {
                detailMapper.insertBatchSomeColumn(details);
            }

            // 5. 缓存到Redis
            cacheQueue(newQueue);
        }

        *//**
         * 添加歌曲到队列开头
         *//*
        @Transactional
        public void addSongToFront(Long userId, Long songId) {
            PlayQueue queue = getCurrentQueue(userId);

            // 如果当前不是自定义队列，转换为自定义队列
            if(!queue.getQueueType().equals(QueueType.CUSTOM.name())) {
                convertToCustomQueue(queue);
            }

            // 获取当前最大sort值
            Integer maxSort = detailMapper.selectMaxSort(queue.getId());
            int newSort = (maxSort != null) ? maxSort + 1 : 0;

            // 更新所有歌曲的排序（腾出首位）
            if(maxSort != null) {
                playQueueMapper.incrementSortAfter(queue.getId(), 0);
            }

            // 插入新歌曲
            PlayQueueDetail detail = new PlayQueueDetail()
                    .setQueueId(queue.getId())
                    .setSongId(songId)
                    .setSort(0);
            detailMapper.insert(detail);

            // 更新当前播放位置
            queue.setCurrentIndex(0);
            updateQueue(queue);
        }

        *//**
         * 移除指定歌曲
         *//*
        @Transactional
        public void removeSong(Long userId, Long songId) {
            PlayQueue queue = getCurrentQueue(userId);

            // 标记为删除
            LambdaUpdateWrapper<PlayQueueDetail> update = new LambdaUpdateWrapper<>();
            update.eq(PlayQueueDetail::getQueueId, queue.getId())
                    .eq(PlayQueueDetail::getSongId, songId)
                    .set(PlayQueueDetail::getDeleted, 1);
            detailMapper.update(null, update);

            // 如果移除的是当前播放歌曲
            PlayQueueDetail current = detailMapper.selectOne(
                    new LambdaQueryWrapper<PlayQueueDetail>()
                            .eq(PlayQueueDetail::getQueueId, queue.getId())
                            .eq(PlayQueueDetail::getSort, queue.getCurrentIndex())
                            .eq(PlayQueueDetail::getDeleted, 0));

            if(current != null && current.getSongId().equals(songId)) {
                findNextSong(queue); // 自动跳转下一首
            }

            // 转换为自定义队列
            if(!queue.getQueueType().equals(QueueType.CUSTOM.name())) {
                convertToCustomQueue(queue);
            }

            updateQueue(queue);
        }

        *//**
         * 获取当前播放队列
         *//*
        public PlayQueue getCurrentQueue(Long userId) {
            // 优先从Redis获取
            String redisKey = REDIS_KEY_PREFIX + userId;
            String cached = redisTemplate.opsForValue().get(redisKey);

            if(StringUtils.isNotBlank(cached)) {
                return JSON.parseObject(cached, PlayQueue.class);
            }

            // 从数据库加载
            LambdaQueryWrapper<PlayQueue> query = new LambdaQueryWrapper<>();
            query.eq(PlayQueue::getUserId, userId)
                    .eq(PlayQueue::getDeleted, 0)
                    .last("LIMIT 1");
            PlayQueue queue = playQueueMapper.selectOne(query);

            if(queue == null) {
                throw new BusinessException("用户没有播放队列");
            }

            cacheQueue(queue);
            return queue;
        }

        // ---------- 私有方法 ----------

        *//**
         * 转换队列为自定义类型
         *//*
        private void convertToCustomQueue(PlayQueue queue) {
            queue.setQueueType(QueueType.CUSTOM.name());
            queue.setSourceId(null);
            playQueueMapper.updateById(queue);
            cacheQueue(queue);
        }

        *//**
         * 逻辑删除队列（主表+明细表）
         *//*
        private void logicalDeleteQueue(Long queueId) {
            // 主表标记删除
            PlayQueue update = new PlayQueue();
            update.setId(queueId);
            update.setDeleted(1);
            playQueueMapper.updateById(update);

            // 明细表标记删除
            LambdaUpdateWrapper<PlayQueueDetail> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(PlayQueueDetail::getQueueId, queueId)
                    .set(PlayQueueDetail::getDeleted, 1);
            detailMapper.update(null, updateWrapper);
        }

        *//**
         * 更新队列到数据库和缓存
         *//*
        private void updateQueue(PlayQueue queue) {
            int version = queue.getVersion();
            queue.setVersion(version + 1);

            LambdaUpdateWrapper<PlayQueue> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(PlayQueue::getId, queue.getId())
                    .eq(PlayQueue::getVersion, version);

            int affected = playQueueMapper.update(queue, updateWrapper);
            if(affected == 0) {
                throw new OptimisticLockException("队列更新冲突");
            }
            cacheQueue(queue);
        }

        *//**
         * 缓存队列到Redis（过期时间30分钟）
         *//*
        private void cacheQueue(PlayQueue queue) {
            String redisKey = REDIS_KEY_PREFIX + queue.getUserId();
            redisTemplate.opsForValue().set(
                    redisKey,
                    JSON.toJSONString(queue),
                    30, TimeUnit.MINUTES
            );
        }

        *//**
         * 根据类型获取歌曲列表（需实现具体逻辑）
         *//*
        private List<Long> fetchSongIdsByType(QueueType type, Long sourceId) {
            // 示例实现：调用其他服务接口获取
            return Collections.emptyList();
        }
    }*/
}
