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
     * 获取指定用户的未读消息列表
     *
     * @param userId 目标用户ID（接收者）
     * @return 未读消息列表（按时间倒序）
     */
    List<PrivateMessage> getUnreadMessages(@Param("userId") Long userId);

    /**
     * 获取用户的私聊联系人列表（最近交流过的用户）
     *
     * @param senderId 当前用户ID（发送者）
     * @return 联系人信息视图对象列表，包含最后一条消息摘要
     * @apiNote 返回结果按最后沟通时间倒序排列
     */
    List<PrivateMessageUserVo> getPrivateMessageUsers(@Param("senderId") Long senderId);

    /**
     * 获取两个用户之间的私聊消息记录
     *
     * @param senderId 发送者用户ID
     * @param receiverId 接收者用户ID
     * @return 私聊消息视图对象列表（按时间正序排列）
     * @implNote 包含消息内容、发送时间、已读状态等字段
     */
    List<PrivateMessageVo> getPrivateMessages(@Param("senderId") Long senderId,
                                              @Param("receiverId") Long receiverId);
}