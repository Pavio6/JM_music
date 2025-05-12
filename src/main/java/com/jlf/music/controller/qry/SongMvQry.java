package com.jlf.music.controller.qry;

import com.jlf.music.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/15 15:46
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class SongMvQry extends PageRequest {
    /**
     * 歌曲名称
     */
    private String songName;
    /**
     * 发行开始时间
     */
    private LocalDate startDate;
    /**
     * 发行结束时间
     */
    private LocalDate endDate;
}
