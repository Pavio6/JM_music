package com.jlf.music.controller.dto;

import com.jlf.music.common.enumerate.VisibilityType;
import lombok.Data;

@Data
public class UpdateVisibilityDTO {
    private VisibilityType visibilityType; // 资料公开状态
}
