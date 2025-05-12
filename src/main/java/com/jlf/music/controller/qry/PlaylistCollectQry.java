package com.jlf.music.controller.qry;

import com.jlf.music.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/26 11:16
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain=true)
public class PlaylistCollectQry extends PageRequest {
    /**
     * 歌单类型
     * PLAYLIST / ALBUM
     */
    private String type;
}
