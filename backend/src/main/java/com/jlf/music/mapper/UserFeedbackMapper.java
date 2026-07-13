package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jlf.music.controller.qry.UserFeedbackQry;
import com.jlf.music.controller.vo.FeedbackInfoVo;
import com.jlf.music.entity.UserFeedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/4 15:23
 * @Version 1.0
 */
@Mapper
public interface UserFeedbackMapper extends BaseMapper<UserFeedback> {
    /**
     * 获取反馈信息列表
     */
    IPage<FeedbackInfoVo> getFeedbackList(Page<FeedbackInfoVo> page, @Param("qry") UserFeedbackQry userFeedbackQry);
}
