package com.jlf.music.controller.qry;

import com.jlf.music.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description
 * @Author JLF
 * @Date 2025/5/9 18:10
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain=true)
public class FollowListQry extends PageRequest {
    private String type;
}
