package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaVo {
    /**
     * 验证码图片信息
     */
    private String image;
    /**
     * Redis存储验证码的key
     */
    private String key;
}