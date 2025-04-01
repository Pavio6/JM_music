package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 用户听歌记录表
 * 用于记录用户听歌的相关信息，包括用户ID、歌曲ID、播放时长、是否完整播放等。
 */
@Data
@Accessors(chain = true)
@TableName("user_listening_record")
public class UserListeningRecord {

    /**
     * 主键ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 歌曲ID
     */
    @TableField("song_id")
    private Long songId;

    /**
     * 播放时长 (秒)
     */
    @TableField("play_duration")
    private Integer playDuration;

    /**
     * 是否完整播放 (0: 未播放完整, 1: 播放完整)
     */
    @TableField("is_complete")
    private Integer isComplete;

    /**
     * 创建时间，插入时自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间，插入和更新时自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志 (0: 未删除, 1: 已删除)，插入时自动填充
     */
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private Integer deleteFlag;
}