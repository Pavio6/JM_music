package com.jlf.music.controller.web.common;

import com.jlf.music.common.Result;
import com.jlf.music.controller.vo.TagsVo;
import com.jlf.music.service.TagsInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签信息
 */
@RestController
@RequestMapping("/common/tags")
public class TagsInfoController {
    @Resource
    private TagsInfoService tagsInfoService;

    /**
     * 获取标签列表
     * @return 标签信息
     */
    @GetMapping("/list")
    public Result<List<TagsVo>> getTagsList() {
        return Result.success(tagsInfoService.getTagsList());
    }
}
