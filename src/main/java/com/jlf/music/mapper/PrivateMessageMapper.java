package com.jlf.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlf.music.controller.vo.PrivateMessageUserVo;
import com.jlf.music.controller.vo.PrivateMessageVo;
import com.jlf.music.entity.PrivateMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author JLF
 * @Description xxx
 * @Date 2025/3/16 17:13
 */
@Mapper
public interface PrivateMessageMapper extends BaseMapper<PrivateMessage> {

    /**
     * 获取用户的私聊联系人列表
     *
     * @param senderId 当前用户ID（发送者）
     * @return 联系人信息视图对象列表，包含最后一条消息摘要
     * @apiNote 返回结果按最后沟通时间倒序排列
     */
    List<PrivateMessageUserVo> findUsersWithMessageHistory(@Param("senderId") Long senderId);

}