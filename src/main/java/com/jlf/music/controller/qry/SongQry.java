package com.jlf.music.controller.qry;

import com.jlf.music.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SongQry extends PageRequest {
    private String songName;
    private Long singerId;
    private Long albumId;
}
