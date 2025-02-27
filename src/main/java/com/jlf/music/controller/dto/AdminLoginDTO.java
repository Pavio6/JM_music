package com.jlf.music.controller.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AdminLoginDTO {
    private String adminName;
    private String adminPass;
    /**
     * 验证码key
     */
    private String captchaKey;
    /**
     * 验证码code
     */
    private String captchaCode;
}
