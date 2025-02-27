package com.jlf.music.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLoginVo {
    private String token;
    private User user;
}
