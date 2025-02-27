package com.jlf.music.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class TagsVo {
    /**
     * 标签类型
     */
    private String tagType;
    /**
     * 标签基本信息list
     */
    private List<TagsBasicInfo> tagsBasicInfoList;

}
