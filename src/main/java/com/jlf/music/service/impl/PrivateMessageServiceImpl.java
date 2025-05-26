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
import com.jlf.music.entity.SysUser;
import com.jlf.music.mapper.PrivateMessageMapper;
import com.jlf.music.mapper.SysUserMapper;
import com.jlf.music.service.PrivateMessageService;
import com.jlf.music.utils.CopyUtils;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrivateMessageServiceImpl extends ServiceImpl<PrivateMessageMapper, PrivateMessage>
        implements PrivateMessageService {
    @Resource
    private PrivateMessageMapper privateMessageMapper;
    @Resource
    private SysUserMapper sysUserMapper;

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
    public PrivateMessage saveMessage(Long senderId, Long receiverId, String content, MessageType type, boolean isRead) {
        // 使用构建者模式创建对象
        PrivateMessage message = PrivateMessage.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .messageType(type)
                .isRead(isRead)
                .status(isRead ? MessageStatus.READ.getValue() : MessageStatus.SENT.getValue())
                .build();
        privateMessageMapper.insert(message);
        return message;
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
     * 分页获取指定发送者和接收者之间的私聊消息历史记录。
     * 使用 LambdaQueryWrapper 构建查询条件，根据页码和每页数量进行分页查询。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<PrivateMessageVo> getPrivateMessages(Long currentUserId, Long receiverId, int pageNum, int pageSize) {

        // 创建分页对象
        Page<PrivateMessage> page = new Page<>(pageNum, pageSize);
        // 创建查询条件
        LambdaQueryWrapper<PrivateMessage> queryWrapper = new LambdaQueryWrapper<>();
        // 查询当前用户与目标用户之间的所有私信记录 - 排除撤回消息
        queryWrapper
                .and(w -> w.eq(PrivateMessage::getSenderId, currentUserId)
                        .eq(PrivateMessage::getReceiverId, receiverId))
                .or(w -> w.eq(PrivateMessage::getSenderId, receiverId)
                        .eq(PrivateMessage::getReceiverId, currentUserId));
        // 排除已撤回的评论
        queryWrapper.ne(PrivateMessage::getStatus, MessageStatus.RECALLED.getValue());
        // 按照发送时间倒序排列 最新消息排在最前面
        queryWrapper.orderByDesc(PrivateMessage::getCreateTime);
        // 执行分页查询
        Page<PrivateMessage> messagePage = privateMessageMapper.selectPage(page, queryWrapper);
        IPage<PrivateMessageVo> voPage = CopyUtils.covertPage(messagePage, PrivateMessageVo.class);
        // 更新未读消息状态为已读（仅接收方为当前用户的消息）
        updateMessageReadStatus(currentUserId, receiverId);
        return voPage;
    }

    /**
     * 更新消息的已读状态
     */
    private void updateMessageReadStatus(Long currentUserId, Long senderId) {
        // 接收者是当前用户且发送者是指定用户的所有未读消息
        LambdaUpdateWrapper<PrivateMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PrivateMessage::getReceiverId, currentUserId)
                .eq(PrivateMessage::getSenderId, senderId)
                .eq(PrivateMessage::getIsRead, MessageStatus.SENT.getValue()) // 消息状态为 SEND
                .ne(PrivateMessage::getStatus, MessageStatus.RECALLED.getValue()); // 消息状态不为 RECALLED

        // 设置为已读
        PrivateMessage updateEntity = new PrivateMessage();
        updateEntity.setIsRead(true);
        updateEntity.setStatus(MessageStatus.READ.getValue());
        this.update(updateEntity, updateWrapper);
    }

    /**
     * 获取私聊列表
     * 从数据库中查询与指定发送者有私聊消息往来的用户列表。
     *
     * @param senderId 发送者的用户 ID
     * @return 包含私聊用户信息的 PrivateMessageUserVo 对象列表
     */
    @Override
    public List<PrivateMessageUserVo> getUsersWithMessageHistory(Long senderId) {
        // 1. 查询所有与当前用户有私信往来的用户ID
        List<Long> interactedUserIds = getInteractedUserIds(senderId);

        if (interactedUserIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 2. 查询这些用户的基本信息
        List<SysUser> users = sysUserMapper.selectList(
                new LambdaQueryWrapper<SysUser>()
                        .in(SysUser::getUserId, interactedUserIds)
        );

        // 3. 为每个用户查询最后一条消息和未读消息数
        return users.stream()
                .map(user -> buildPrivateMessageUserVo(senderId, user))
                .sorted(Comparator.comparing(PrivateMessageUserVo::getLastMessageTime).reversed())
                .collect(Collectors.toList());
//        return privateMessageMapper.findUsersWithMessageHistory(senderId);
    }

    /**
     * 查询所有与当前用户有私信往来的用户ID
     */
    private List<Long> getInteractedUserIds(Long senderId) {
        // 查询所有涉及当前用户的私信消息
        List<PrivateMessage> messages = privateMessageMapper.selectList(
                new LambdaQueryWrapper<PrivateMessage>()
                        .eq(PrivateMessage::getSenderId, senderId)
                        .or()
                        .eq(PrivateMessage::getReceiverId, senderId)
        );
        // 提取对方用户id并去重
        return messages.stream()
                .map(msg -> msg.getSenderId().equals(senderId) ? msg.getReceiverId() : msg.getSenderId())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 为每个用户查询最后一条消息和未读消息数
     */
    private PrivateMessageUserVo buildPrivateMessageUserVo(Long senderId, SysUser user) {
        PrivateMessageUserVo vo = new PrivateMessageUserVo();
        vo.setOtherUserId(user.getUserId());
        vo.setOtherUserName(user.getUserName());
        vo.setOtherUserAvatar(user.getUserAvatar());

        // 查询最后一条消息
        PrivateMessage lastMessage = privateMessageMapper.selectOne(
                new LambdaQueryWrapper<PrivateMessage>()
                        .and(wrapper -> wrapper
                                .eq(PrivateMessage::getSenderId, senderId)
                                .eq(PrivateMessage::getReceiverId, user.getUserId())
                        )
                        .or(wrapper -> wrapper
                                .eq(PrivateMessage::getSenderId, user.getUserId())
                                .eq(PrivateMessage::getReceiverId, senderId)
                        )
                        .ne(PrivateMessage::getStatus, MessageStatus.RECALLED.getValue()) // 消息状态不是撤回
                        .orderByDesc(PrivateMessage::getMessageId)
                        .last("LIMIT 1")
        );

        if (lastMessage != null) {
            vo.setLastMessageContent(lastMessage.getContent());
            vo.setLastMessageTime(lastMessage.getCreateTime());
            vo.setMessageType(lastMessage.getMessageType());
        }

        // 查询未读消息数
        Long unreadCount = privateMessageMapper.selectCount(
                new LambdaQueryWrapper<PrivateMessage>()
                        .eq(PrivateMessage::getSenderId, user.getUserId())
                        .eq(PrivateMessage::getReceiverId, senderId)
                        .eq(PrivateMessage::getStatus, MessageStatus.SENT.getValue())
        );
        vo.setUnreadCount(unreadCount.intValue());

        return vo;
    }

}