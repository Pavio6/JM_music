package com.jlf.music.controller.vo;


import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 用户个人信息
 */
@Data
@Accessors(chain = true)
public class UserPersonalInfoVo {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户简介
     */
    private String userBio;
    /**
     * 用户性别 0:女    1:男
     */
    private Integer userSex;
    /**
     * 用户生日
     */
    private LocalDate userBirth;
    /**
     * 邮箱
     */
    private String userEmail;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户隐私设置
     */
    private UserPrivacyBasicInfoVo userPrivacy;

}
