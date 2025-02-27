package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlf.music.entity.PlayQueueDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PlayQueueDetailMapper extends BaseMapper<PlayQueueDetail> {
    /**
     * 插入后调整排序
     *
     * @param queueId        队列 ID
     * @param insertPosition 插入位置的排序值
     */
    @Update("UPDATE play_queue_detail " +
            "SET sort = sort + 1 " +
            "WHERE queue_id = #{queueId} " +
            "  AND sort >= #{insertSort} " +
            "  AND delete_flag = 0")
    void updateSortAfterInsert(@Param("queueId") Long queueId, @Param("insertPosition") Integer insertPosition);
    /**
     * 批量物理删除播放队列详情记录
     * @param queueId 队列 ID
     * @param songIds 要删除的歌曲 ID 列表
     */
    void deleteBatch(@Param("queueId") Long queueId, @Param("songIds") List<Long> songIds);

    void updateSortAfterDeleteBatch(@Param("queueId") Long queueId);
}
