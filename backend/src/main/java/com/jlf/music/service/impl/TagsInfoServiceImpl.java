package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.controller.vo.TagsBasicInfo;
import com.jlf.music.controller.vo.TagsVo;
import com.jlf.music.entity.TagsInfo;
import com.jlf.music.mapper.TagsInfoMapper;
import com.jlf.music.service.TagsInfoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TagsInfoServiceImpl extends ServiceImpl<TagsInfoMapper, TagsInfo>
        implements TagsInfoService {

    /**
     * 获取标签列表
     *
     * @return 标签信息
     */
    @Override
    public List<TagsVo> getTagsList() {
        List<TagsInfo> tagsInfoList = this.list();
        // 用于存储每个 tagType 对应的 TagsBasicInfo 列表
        Map<String, List<TagsBasicInfo>> tagTypeMap = new HashMap<>();
        // 遍历查询结果，将每个 tagType 对应的 TagsBasicInfo 信息分组
        for (TagsInfo tagsInfo : tagsInfoList) {
            String tagType = tagsInfo.getTagType();
            TagsBasicInfo basicInfo = new TagsBasicInfo();
            basicInfo.setTagId(tagsInfo.getTagId());
            basicInfo.setTagName(tagsInfo.getTagName());

            tagTypeMap.computeIfAbsent(tagType, k -> new ArrayList<>()).add(basicInfo);
        }

        // 将分组结果封装成 TagsVo 对象列表

        List<TagsVo> tagsVoList = new ArrayList<>();
        for (Map.Entry<String, List<TagsBasicInfo>> entry : tagTypeMap.entrySet()) {
            TagsVo tagsVo = new TagsVo();
            tagsVo.setTagType(entry.getKey());
            tagsVo.setTagsBasicInfoList(entry.getValue());
            tagsVoList.add(tagsVo);
        }

        return tagsVoList;
    }
}
