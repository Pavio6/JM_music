package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 管理员信息表
 */
@Data
@Accessors(chain = true) // 开启链式调用
@TableName("sys_admin")
public class SysAdmin implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "admin_id", type = IdType.AUTO)
    private Long adminId;
    private String adminName;
    private String adminPass;
    private String adminSalt;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;
}
