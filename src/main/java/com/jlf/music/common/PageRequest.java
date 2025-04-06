package com.jlf.music.common;

import lombok.Data;

/**
 * @Description 分页请求参数
 * @Author JLF
 * @Date 2025/4/4 15:28
 * @Version 1.0
 */
@Data
public class PageRequest {
    /**
     * 当前页
     */
    private Integer pageNum = 1;
    /**
     * 每页显示条数
     */
    private Integer pageSize = 10;
}
