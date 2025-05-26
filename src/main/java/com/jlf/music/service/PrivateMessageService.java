package com.jlf.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.common.Result;
import com.jlf.music.common.enumerate.MessageType;
import com.jlf.music.controller.vo.PrivateMessageUserVo;
import com.jlf.music.controller.vo.PrivateMessageVo;
import com.jlf.music.entity.PrivateMessage;

import java.util.List;

public interface PrivateMessageService extends IService<PrivateMessage> {

    /**
     * 分页获取用户与对方用户的私聊消息列表
     *
     * @param senderId   发送者ID
     * @param receiverId 接收者ID
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @return 分页消息列表
     */
    IPage<PrivateMessageVo> getPrivateMessages(Long senderId, Long receiverId, int pageNum, int pageSize);

    /**
     * 获取用户的所有私聊联系人
     *
     * @param senderId 发送者ID
     * @return 联系人列表
     */
    List<PrivateMessageUserVo> getUsersWithMessageHistory(Long senderId);

    /**
     * 保存私聊消息
     *
     * @param senderId   发送者ID
     * @param receiverId 接收者ID
     * @param content    消息内容
     * @param type       消息类型
     * @return 保存的消息实体
     */
    PrivateMessage saveMessage(Long senderId, Long receiverId, String content, MessageType type, boolean isRead);

    /**
     * 撤回消息
     *
     * @param messageId 消息ID
     * @param senderId  发送者ID
     */
    Boolean recallMessage(Long messageId, Long senderId);

    /**
     * 删除消息
     */
    Result<Boolean> deleteAllMessages(Long receiverId);
}
