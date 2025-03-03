package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.jlf.music.common.enumerate.PlayModeType;
import com.jlf.music.common.enumerate.QueueType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("play_queue")
public class PlayQueue {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    /**
     * 播放队列类型
     */
    @TableField("queue_type")
    private QueueType queueType;
    /**
     * 专辑/歌单id
     */
    // FieldStrategy.IGNORED  忽略字段是否为 null, 始终更新
    @TableField(value = "source_id", updateStrategy = FieldStrategy.IGNORED)
    private Long sourceId;
    /**
     * 当前播放索引
     */
    @TableField("current_index")
    private Integer currentIndex;
    /**
     * 播放模式
     */
    @TableField("play_mode")
    private PlayModeType playMode;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;
}
