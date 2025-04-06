package com.jlf.music.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.enumerate.FeedbackProcessingStatus;
import com.jlf.music.common.enumerate.FeedbackType;
import com.jlf.music.controller.qry.UserFeedbackQry;
import com.jlf.music.controller.vo.FeedbackInfoVo;
import com.jlf.music.controller.vo.FeedbackStatisticsVo;
import com.jlf.music.entity.SysUser;
import com.jlf.music.entity.UserFeedback;
import com.jlf.music.mapper.SysUserMapper;
import com.jlf.music.mapper.UserFeedbackMapper;
import com.jlf.music.service.UserFeedbackService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @Description 用户反馈service
 * @Author JLF
 * @Date 2025/4/4 15:24
 * @Version 1.0
 */
@Service
public class UserFeedbackServiceImpl extends ServiceImpl<UserFeedbackMapper, UserFeedback>
        implements UserFeedbackService {
    @Resource
    private UserFeedbackMapper userFeedbackMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 获取反馈信息列表
     */
    @Override
    public IPage<FeedbackInfoVo> getFeedbackList(UserFeedbackQry userFeedbackQry) {
        // 初始化分页对象
        Page<FeedbackInfoVo> page = new Page<>(userFeedbackQry.getPageNum(), userFeedbackQry.getPageSize());
        return userFeedbackMapper.getFeedbackList(page, userFeedbackQry);

    }

    /**
     * 获取反馈信息统计数据
     */
    @Override
    public FeedbackStatisticsVo getFeedbackStatistics() {
        Long pendingCount = userFeedbackMapper.selectCount(new LambdaQueryWrapper<UserFeedback>()
                .eq(UserFeedback::getStatus, FeedbackProcessingStatus.PENDING));
        Long processingCount = userFeedbackMapper.selectCount(new LambdaQueryWrapper<UserFeedback>()
                .eq(UserFeedback::getStatus, FeedbackProcessingStatus.PROCESSING));
        // 今天日期
        Date todayStart = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        // 明天日期
        Date todayEnd = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Long todayCount = userFeedbackMapper.selectCount(new LambdaQueryWrapper<UserFeedback>()
                .between(UserFeedback::getCreateTime, todayStart, todayEnd));
        return new FeedbackStatisticsVo(pendingCount, processingCount, todayCount);
    }
}
