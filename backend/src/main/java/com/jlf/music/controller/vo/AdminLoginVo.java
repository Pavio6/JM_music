package com.jlf.music.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AdminLoginVo {
    private Long adminId;
    private String adminName;
    private String token;
}
