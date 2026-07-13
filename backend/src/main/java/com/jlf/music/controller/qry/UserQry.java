package com.jlf.music.controller.qry;

import com.jlf.music.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Description 用户qry
 * @Author JLF
 * @Date 2025/4/4 15:28
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserQry extends PageRequest {
    /**
     * 用户名（模糊查询）
     */
    private String userName;

    /**
     * 用户邮箱（模糊查询）
     */
    private String userEmail;

    /**
     * 用户状态
     */
    private Integer userStatus;

    /**
     * 开始时间（查询范围）
     */
    private Date beginTime;

    /**
     * 结束时间（查询范围）
     */
    private Date endTime;
}
