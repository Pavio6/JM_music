package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 播放队列详情表
 */
@Data
@Accessors(chain = true)
@TableName("play_queue_detail")
public class PlayQueueDetail {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 播放队列ID
     */
    @TableField("queue_id")
    private Long queueId;

    /**
     * 歌曲ID
     */
    @TableField("song_id")
    private Long songId;

    /**
     * 播放顺序
     */
    @TableField("sort")
    private Integer sort;

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
}