package com.jlf.music.common;

import lombok.Data;

@Data
public class PageRequest {
    private Integer pageNum = 1;  // 当前页码，默认为1
    private Integer pageSize = 10; // 每页大小，默认为10
}