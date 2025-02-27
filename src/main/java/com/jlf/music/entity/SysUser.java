package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * 用户信息表
 */
@Data
@Accessors(chain = true)
@TableName("sys_user")
@NoArgsConstructor
public class SysUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String userPass;
    /**
     * 邮箱
     */
    private String userEmail;
    /**
     * 简介
     */
    private String userBio;
    /**
     * 性别 0-女 1-男 2-未知
     */
    private Integer userSex;
    /**
     * 出生日期
     */
    private LocalDate userBirth;
    /**
     * 用户状态 0-正常 1-停用
     */
    private Integer userStatus;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户类型 0-普通用户 1-管理员
     */
    private Integer type;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;

    public SysUser(Long userId, String userName, String userAvatar) {
        this.userAvatar = userAvatar;
        this.userId = userId;
        this.userName = userName;
    }
}
