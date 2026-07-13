package com.jlf.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.vo.TagsVo;
import com.jlf.music.entity.TagsInfo;

import java.util.List;


public interface TagsInfoService extends IService<TagsInfo> {
    /**
     * 获取标签列表
     * @return 标签信息
     */
    List<TagsVo> getTagsList();

}
