package com.jlf.music.controller.qry;

import com.jlf.music.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AlbumQry extends PageRequest {
    private String albumName;
    private Long singerId;
    /**
     * 专辑类型id
     */
    private Long typeId;
    /**
     * 发行开始时间
     */
    private LocalDate beginDate;
    /**
     * 发行结束时间
     */
    private LocalDate endDate;
}
