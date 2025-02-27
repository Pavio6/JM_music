package com.jlf.music.controller.qry;

import com.jlf.music.common.PageRequest;
import com.jlf.music.common.enumerate.TargetType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain=true)
public class CommentQry extends PageRequest {
    private TargetType targetType;
    private Long targetId;
}
