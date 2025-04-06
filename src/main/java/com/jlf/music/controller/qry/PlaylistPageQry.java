package com.jlf.music.controller.qry;

import com.jlf.music.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PlaylistPageQry extends PageRequest {
    /**
     * 歌单名称
     */
    private String playlistName;
    /**
     * 歌单标签id
     * 在歌单分类中 用户点击某个选项 查询用于该标签的所有歌单
     */
    private Long tagId;
    /**
     * 歌单状态
     */
    private Integer status;

}
