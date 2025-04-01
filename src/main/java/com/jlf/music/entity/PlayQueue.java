package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.jlf.music.common.enumerate.PlayModeType;
import com.jlf.music.common.enumerate.QueueType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 播放队列实体类
 */
@Data
@Accessors(chain = true)
@TableName("play_queue")
public class PlayQueue {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 播放队列类型
     */
    @TableField("queue_type")
    private QueueType queueType;

    /**
     * 专辑/歌单ID
     * 忽略字段是否为 null，始终更新
     */
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

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志（0-未删除，1-已删除）
     */
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private Integer deleteFlag;
}