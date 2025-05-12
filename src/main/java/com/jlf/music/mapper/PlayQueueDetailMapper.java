package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlf.music.entity.PlayQueueDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PlayQueueDetailMapper extends BaseMapper<PlayQueueDetail> {

    /**
     * 调整删除歌曲后播放队列的顺序
     */
    void updateSortAfterDeleteBatch(@Param("queueId") Long queueId);

    void insertBatchSomeColumn(List<PlayQueueDetail> details);
}
