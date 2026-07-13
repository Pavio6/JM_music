package com.jlf.music.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.jlf.music.common.enumerate.FeedbackProcessingStatus;
import com.jlf.music.controller.dto.UserFeedbackDTO;
import com.jlf.music.controller.dto.UserFeedbackReplyDTO;
import com.jlf.music.controller.qry.UserFeedbackQry;
import com.jlf.music.controller.vo.FeedbackInfoVo;
import com.jlf.music.controller.vo.FeedbackStatisticsVo;
import com.jlf.music.controller.vo.UserFeedbackDetailVo;
import com.jlf.music.controller.vo.UserFeedbackListVo;
import com.jlf.music.entity.UserFeedback;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.SysUserMapper;
import com.jlf.music.mapper.UserFeedbackMapper;
import com.jlf.music.security.LoginUser;
import com.jlf.music.service.UserFeedbackService;
import com.jlf.music.utils.CopyUtils;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    /**
     * 添加反馈信息
     */
    @Override
    public Boolean addFeedback(UserFeedbackDTO userFeedbackDTO) {
        if (userFeedbackDTO.getFeedbackTitle() == null || userFeedbackDTO.getFeedbackContent() == null) {
            throw new ServiceException("反馈标题或内容不能为空");
        }
        if (SensitiveWordHelper.contains(userFeedbackDTO.getFeedbackTitle()) || SensitiveWordHelper.contains(userFeedbackDTO.getFeedbackContent())) {
            throw new ServiceException("反馈标题或内容中包含敏感词");
        }
        // 获取登录用户上下文信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        UserFeedback userFeedback = CopyUtils.classCopy(userFeedbackDTO, UserFeedback.class);
        userFeedback.setStatus(FeedbackProcessingStatus.PENDING)
                .setUserId(loginUser.getUser().getUserId())
                .setUserName(loginUser.getUsername());
        // 保存反馈信息
        return this.save(userFeedback);
    }

    /**
     * 编辑反馈信息
     */
    @Override
    public Boolean editFeedback(UserFeedbackDTO userFeedbackDTO, Long feedbackId) {
        // 获取用户id
        Long userId = SecurityUtils.getUserId();
        UserFeedback userFeedback = userFeedbackMapper.selectById(feedbackId);
        if (userFeedback == null) {
            throw new ServiceException("该反馈信息不存在");
        }
        if (!Objects.equals(userFeedback.getUserId(), userId)) {
            throw new ServiceException("您无权编辑该反馈信息，只能编辑自己的反馈记录");
        }
        LambdaUpdateWrapper<UserFeedback> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserFeedback::getFeedbackId, feedbackId);
        wrapper.set(userFeedbackDTO.getFeedbackType() != null, UserFeedback::getFeedbackType, userFeedbackDTO.getFeedbackType());
        wrapper.set(userFeedbackDTO.getFeedbackTitle() != null, UserFeedback::getFeedbackTitle, userFeedbackDTO.getFeedbackTitle());
        wrapper.set(userFeedbackDTO.getFeedbackContent() != null, UserFeedback::getFeedbackContent, userFeedbackDTO.getFeedbackContent());
        // 执行更新语句
        return this.update(wrapper);
    }

    /**
     * 获取用户个人反馈信息列表
     */
    @Override
    public List<UserFeedbackListVo> getPersonalFeedbackList() {
        Long userId = SecurityUtils.getUserId();
        List<UserFeedback> list = this.list(new LambdaQueryWrapper<UserFeedback>()
                .eq(UserFeedback::getUserId, userId)
                .orderByDesc(UserFeedback::getCreateTime));// 按创建时间降序
        return CopyUtils.classCopyList(list, UserFeedbackListVo.class);
    }

    /**
     * 获取反馈信息详情
     */
    @Override
    public UserFeedbackDetailVo getFeedbackDetailById(Long feedbackId) {
        UserFeedback userFeedback = this.getById(feedbackId);
        if (userFeedback == null) {
            throw new ServiceException("反馈信息不存在");
        }
        UserFeedbackDetailVo vo = CopyUtils.classCopy(userFeedback, UserFeedbackDetailVo.class);
        if (userFeedback.getAdminId() == null) {
            return vo;
        } else {
            String adminName = sysUserMapper.selectById(userFeedback.getAdminId()).getUserName();
            vo.setAdminName(adminName);
        }
        return vo;
    }

    /**
     * 删除用户反馈信息
     */
    @Override
    public Boolean deleteByFeedbackId(Long feedbackId) {
        Long userId = SecurityUtils.getUserId();
        UserFeedback userFeedback = userFeedbackMapper.selectOne(new LambdaQueryWrapper<UserFeedback>()
                .eq(UserFeedback::getUserId, userId)
                .eq(UserFeedback::getFeedbackId, feedbackId));
        if (userFeedback == null) {
            throw new ServiceException("您无权删除该反馈信息，只能删除自己的反馈记录");
        }
        return this.removeById(feedbackId);
    }

    /**
     * 获取反馈信息详情 - 管理员端
     */
    @Override
    public FeedbackInfoVo getAdminFeedbackDetailById(Long feedbackId) {
        UserFeedback userFeedback = this.getById(feedbackId);
        if (userFeedback == null) {
            throw new ServiceException("该反馈记录不存在");
        }
        String adminName = null;
        if (userFeedback.getAdminId() != null) {
            adminName = sysUserMapper.selectById(userFeedback.getAdminId()).getUserName();
        }
        FeedbackInfoVo feedbackInfoVo = CopyUtils.classCopy(userFeedback, FeedbackInfoVo.class);
        feedbackInfoVo.setCreateTime(dateToLocalDateTime(userFeedback.getCreateTime()))
                .setUpdateTime(dateToLocalDateTime(userFeedback.getUpdateTime()));
        if (userFeedback.getReplyTime() != null) {
            feedbackInfoVo.setReplyTime(dateToLocalDateTime(userFeedback.getReplyTime()));
        }
        feedbackInfoVo.setAdminName(adminName);
        return feedbackInfoVo;
    }

    /**
     * 更改反馈状态和回复信息
     */
    @Override
    public Boolean updateFeedbackStatusAndReply(Long feedbackId, UserFeedbackReplyDTO dto) {
        UserFeedback userFeedback = this.getById(feedbackId);
        if (userFeedback == null) {
            throw new ServiceException("该反馈记录不存在");
        }
        LambdaUpdateWrapper<UserFeedback> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserFeedback::getFeedbackId, feedbackId);
        wrapper.set(dto.getStatus() != null, UserFeedback::getStatus, dto.getStatus());
        // 如果有反馈内容
        if (dto.getAdminReply() != null) {
            Long adminId = SecurityUtils.getUserId();
            wrapper.set(UserFeedback::getAdminReply, dto.getAdminReply());
            wrapper.set(UserFeedback::getAdminId, adminId);
            wrapper.set(UserFeedback::getReplyTime, dateToLocalDateTime(new Date()));
        }
        return this.update(null, wrapper);
    }

    /**
     * Date类型转为LocalDateTime类型
     */
    private LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
