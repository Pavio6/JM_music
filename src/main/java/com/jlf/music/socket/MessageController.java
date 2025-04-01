package com.jlf.music.socket;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jlf.music.common.Result;
import com.jlf.music.socket.dto.ConversationDTO;
import com.jlf.music.socket.dto.MessageDTO;
import com.jlf.music.socket.dto.MessageRequestDTO;
import com.jlf.music.entity.PrivateMessage;
import com.jlf.music.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final PrivateMessageService privateMessageService;

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    public Result<Page<ConversationDTO>> getConversations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long userId = SecurityUtils.getUserId();
        Page<ConversationDTO> conversations = privateMessageService.getUserConversations(userId, page, size);
        return Result.success(conversations);
    }

    /**
     * 获取与指定用户的会话消息
     */
    @GetMapping("/conversations/{otherUserId}")
    public Result<List<MessageDTO>> getConversationMessages(
            @PathVariable Long otherUserId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtils.getUserId();
        List<MessageDTO> messages = privateMessageService.getConversationMessages(userId, otherUserId, page, size);
        return Result.success(messages);
    }

    /**
     * 标记会话已读
     */
    @PutMapping("/conversations/{otherUserId}/read")
    public Result<Void> markConversationAsRead(
            @PathVariable Long otherUserId) {
        Long userId = SecurityUtils.getUserId();
        privateMessageService.markMessagesAsRead(userId, otherUserId);
        return Result.success();
    }

    /**
     * 撤回消息
     */
    @PutMapping("/{messageId}/recall")
    public Result<Boolean> recallMessage(
            @PathVariable Long messageId) {
        // 获取用户id
        Long userId = SecurityUtils.getUserId();
        boolean recalled = privateMessageService.recallMessage(userId, messageId);

        if (recalled) {
            return Result.success(true);
        } else {
            return Result.fail(500, "消息撤回失败");
        }
    }

    /**
     * 发送消息
     */
    @PostMapping
    public Result<MessageDTO> sendMessage(
            @Valid @RequestBody MessageRequestDTO request) {
        // 获取用户id
        Long userId = SecurityUtils.getUserId();
        // 发送消息
        PrivateMessage message = privateMessageService.sendMessage(userId, request);
        return Result.success(privateMessageService.convertToDTO(message));
    }

    /**
     * 获取未读消息数
     */
    @GetMapping("/unread")
    public Result<Integer> getUnreadMessageCount() {
        Long userId = SecurityUtils.getUserId();
        int unreadCount = privateMessageService.getUnreadMessageCount(userId);
        return Result.success(unreadCount);
    }

    /**
     * 获取用户在线状态
     */
    @GetMapping("/users/{userId}/status")
    public Result<Boolean> getUserOnlineStatus(@PathVariable Long userId) {
        boolean isOnline = privateMessageService.isUserOnline(userId);
        return Result.success(isOnline);
    }


}