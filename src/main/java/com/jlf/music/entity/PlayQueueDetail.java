package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("play_queue_detail")
public class PlayQueueDetail {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 播放队列id
     */
    private Long queueId;
    /**
     * 歌曲id
     */
    private Long songId;
    /**
     * 播放顺序
     */
    private Integer sort;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
