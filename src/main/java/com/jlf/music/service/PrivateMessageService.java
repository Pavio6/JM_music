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
     * 分页获取私聊消息
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页消息列表
     */
    IPage<PrivateMessageVo> getPrivateMessages(Long senderId, Long receiverId, int pageNum, int pageSize);

    /**
     * 获取两个用户之间的所有私聊消息
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @return 消息列表
     */
    List<PrivateMessageVo> getPrivateMessages(Long senderId, Long receiverId);

    /**
     * 获取用户的所有私聊联系人
     * @param senderId 发送者ID
     * @return 联系人列表
     */
    List<PrivateMessageUserVo> getPrivateMessageUsers(Long senderId);

    /**
     * 保存私聊消息
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @param type 消息类型
     * @return 保存的消息实体
     */
    PrivateMessage saveMessage(Long senderId, Long receiverId, String content, MessageType type);

    /**
     * 获取用户的离线消息
     * @param userId 用户ID
     * @return 离线消息列表
     */
    List<PrivateMessageVo> getOfflineMessages(Long userId);

    /**
     * 将消息标记为已读
     * @param messageId 消息ID
     */
    void markAsRead(Long messageId);

    /**
     * 将消息标记为未读
     * @param messageId 消息ID
     */
    void markAsUnread(Long messageId);

    /**
     * 用户上线
     * @param userId 用户ID
     */
    void userOnline(Long userId);

    /**
     * 用户下线
     * @param userId 用户ID
     */
    void userOffline(Long userId);


    /**
     * 撤回消息
     * @param messageId 消息ID
     * @param senderId 发送者ID
     */
    Boolean recallMessage(Long messageId, Long senderId);

    /**
     * 删除消息
     */
    Result<Boolean> deleteAllMessages(Long receiverId);
}
