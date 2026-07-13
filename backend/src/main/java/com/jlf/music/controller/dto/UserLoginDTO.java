package com.jlf.music.controller.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLoginDTO {
    private String userName;
    private String userPass;
    /**
     * 验证码key
     */
    private String captchaKey;
    /**
     * 验证码code
     */
    private String captchaCode;
}
