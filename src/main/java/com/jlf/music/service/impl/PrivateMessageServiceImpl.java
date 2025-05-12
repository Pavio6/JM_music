package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.common.Result;
import com.jlf.music.common.enumerate.MessageStatus;
import com.jlf.music.common.enumerate.MessageType;
import com.jlf.music.controller.vo.PrivateMessageUserVo;
import com.jlf.music.controller.vo.PrivateMessageVo;
import com.jlf.music.entity.PrivateMessage;
import com.jlf.music.mapper.PrivateMessageMapper;
import com.jlf.music.service.PrivateMessageService;
import com.jlf.music.utils.CopyUtils;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrivateMessageServiceImpl extends ServiceImpl<PrivateMessageMapper, PrivateMessage>
        implements PrivateMessageService {
    @Resource
    private PrivateMessageMapper privateMessageMapper;

    /**
     * 保存私聊消息到数据库。
     * 创建一个新的 PrivateMessage 对象，设置发送者 ID、接收者 ID、消息内容、消息类型和创建时间，
     * 然后将其保存到数据库中。
     *
     * @param senderId   发送者的用户 ID
     * @param receiverId 接收者的用户 ID
     * @param content    消息内容
     * @param type       消息类型
     * @return 保存后的 PrivateMessage 对象
     */
    @Override
    public PrivateMessage saveMessage(Long senderId, Long receiverId, String content, MessageType type) {
        // 使用构建者模式创建对象
        PrivateMessage message = PrivateMessage.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .messageType(type)
                .build();
        this.save(message);
        return message;
    }

    /**
     * 获取指定用户的离线消息列表。
     * 从数据库中查询未读消息，然后将这些消息转换为 PrivateMessageVo 对象列表。
     *
     * @param userId 用户 ID
     * @return 包含离线消息的 PrivateMessageVo 对象列表
     */
    @Override
    public List<PrivateMessageVo> getOfflineMessages(Long userId) {
        List<PrivateMessage> messages = this.baseMapper.getUnreadMessages(userId);
        return messages.stream().map(this::convertToVo).toList();
    }

    /**
     * 将指定消息标记为已读。
     * 使用 UpdateWrapper 更新数据库中指定消息的 is_read 字段为 true。
     *
     * @param messageId 消息 ID
     */
    @Override
    public void markAsRead(Long messageId) {
        privateMessageMapper.update(new LambdaUpdateWrapper<PrivateMessage>()
                .eq(PrivateMessage::getMessageId, messageId)
                .set(PrivateMessage::getIsRead, true));
    }

    /**
     * 将指定消息标记为未读。
     * 使用 UpdateWrapper 更新数据库中指定消息的 is_read 字段为 false。
     *
     * @param messageId 消息 ID
     */
    @Override
    public void markAsUnread(Long messageId) {
        privateMessageMapper.update(new LambdaUpdateWrapper<PrivateMessage>()
                .eq(PrivateMessage::getMessageId, messageId)
                .set(PrivateMessage::getIsRead, false));
    }

    /**
     * 处理用户上线事件。
     * 目前此方法为空，可根据实际需求添加用户上线后的处理逻辑。
     *
     * @param userId 用户 ID
     */
    @Override
    public void userOnline(Long userId) {

    }

    /**
     * 处理用户下线事件。
     * 目前此方法为空，可根据实际需求添加用户下线后的处理逻辑。
     *
     * @param userId 用户 ID
     */
    @Override
    public void userOffline(Long userId) {

    }


    /**
     * 撤回指定消息。
     * 使用 UpdateWrapper 更新数据库中指定消息的状态为已撤回。
     *
     * @param messageId 消息 ID
     * @param senderId  发送者的用户 ID
     */
    @Override
    public Boolean recallMessage(Long messageId, Long senderId) {
        // 更新状态为recalled
        return privateMessageMapper.update(new LambdaUpdateWrapper<PrivateMessage>()
                .eq(PrivateMessage::getMessageId, messageId)
                .eq(PrivateMessage::getSenderId, senderId)
                .ne(PrivateMessage::getStatus, MessageStatus.RECALLED.getValue())
                .gt(PrivateMessage::getCreateTime, LocalDateTime.now().minusMinutes(2))
                .set(PrivateMessage::getStatus, MessageStatus.RECALLED.getValue())) > 0;
    }

    /**
     * 删除消息
     */
    @Override
    public Result<Boolean> deleteAllMessages(Long receiverId) {
        Long userId = SecurityUtils.getUserId();
        return Result.success(privateMessageMapper.delete(new LambdaQueryWrapper<PrivateMessage>()
                .eq(PrivateMessage::getReceiverId, receiverId)
                .eq(PrivateMessage::getSenderId, userId)) > 0);
    }

    /**
     * 将 PrivateMessage 对象转换为 PrivateMessageVo 对象。
     *
     * @param message PrivateMessage 对象
     * @return 转换后的 PrivateMessageVo 对象
     */
    private PrivateMessageVo convertToVo(PrivateMessage message) {
        return PrivateMessageVo.builder()
                .messageId(message.getMessageId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content(message.getContent())
                .createTime(message.getCreateTime())
                .isRead(message.getIsRead())
                .build();
    }

    /**
     * 获得私聊的历史记录，时间正序。
     * 从数据库中查询指定发送者和接收者之间的私聊消息历史记录。
     *
     * @param senderId   发送者的用户 ID
     * @param receiverId 接收者的用户 ID
     * @return 包含私聊消息历史记录的 PrivateMessageVo 对象列表
     */
    @Override
    public List<PrivateMessageVo> getPrivateMessages(Long senderId, Long receiverId) {
        return this.baseMapper.getPrivateMessages(senderId, receiverId);
    }

    /**
     * 分页获取指定发送者和接收者之间的私聊消息历史记录。
     * 使用 LambdaQueryWrapper 构建查询条件，根据页码和每页数量进行分页查询。
     *
     * @param senderId   发送者的用户 ID
     * @param receiverId 接收者的用户 ID
     * @param pageNum    页码
     * @param pageSize   每页显示的记录数量
     * @return 包含私聊消息历史记录的分页对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<PrivateMessageVo> getPrivateMessages(Long senderId, Long receiverId, int pageNum, int pageSize) {
        // 修改未读消息的状态
        LambdaUpdateWrapper<PrivateMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PrivateMessage::getSenderId, senderId)
                .eq(PrivateMessage::getReceiverId, receiverId)
                .set(PrivateMessage::getIsRead, true);
        privateMessageMapper.update(updateWrapper);
        Page<PrivateMessage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PrivateMessage> wrapper = new LambdaQueryWrapper<PrivateMessage>()
                .eq(PrivateMessage::getSenderId, senderId)
                .eq(PrivateMessage::getReceiverId, receiverId)
                .ne(PrivateMessage::getStatus, MessageStatus.RECALLED.getValue()); // 不是撤回状态
        Page<PrivateMessage> pageResult = privateMessageMapper.selectPage(page, wrapper);
        return CopyUtils.covertPage(pageResult, PrivateMessageVo.class);
    }

    /**
     * 获取私聊列表
     * 从数据库中查询与指定发送者有私聊消息往来的用户列表。
     *
     * @param senderId 发送者的用户 ID
     * @return 包含私聊用户信息的 PrivateMessageUserVo 对象列表
     */
    @Override
    public List<PrivateMessageUserVo> getPrivateMessageUsers(Long senderId) {
        return privateMessageMapper.getPrivateMessageUsers(senderId);
    }

    // 标记消息为已读
//    public void markAsRead(Long receiverId, Long senderId) {
//        this.baseMapper.updateIsRead(
//                receiverId, senderId, 1);
//    }

}