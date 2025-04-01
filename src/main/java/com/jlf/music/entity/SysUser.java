package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    /**
     * 用户ID，主键，自增
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 密码
     */
    @TableField("user_pass")
    private String userPass;

    /**
     * 邮箱
     */
    @TableField("user_email")
    private String userEmail;

    /**
     * 简介
     */
    @TableField("user_bio")
    private String userBio;

    /**
     * 性别 (0: 女, 1: 男, 2: 未知)
     */
    @TableField("user_sex")
    private Integer userSex;

    /**
     * 出生日期
     */
    @TableField("user_birth")
    private LocalDate userBirth;

    /**
     * 用户状态 (0: 正常, 1: 停用)
     */
    @TableField("user_status")
    private Integer userStatus;

    /**
     * 用户头像路径
     */
    @TableField("user_avatar")
    private String userAvatar;

    /**
     * 用户类型 (0: 普通用户, 1: 管理员)
     */
    @TableField("type")
    private Integer type;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间，插入时自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间，插入和更新时自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志 (0: 未删除, 1: 已删除)，插入时自动填充
     */
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private Integer deleteFlag;

    /**
     * 构造方法
     *
     * @param userId     用户ID
     * @param userName   用户名
     * @param userAvatar 用户头像路径
     */
    public SysUser(Long userId, String userName, String userAvatar) {
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
    }
}