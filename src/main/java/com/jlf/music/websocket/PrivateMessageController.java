package com.jlf.music.websocket;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.vo.PrivateMessageUserVo;
import com.jlf.music.controller.vo.PrivateMessageVo;
import com.jlf.music.service.PrivateMessageService;
import com.jlf.music.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/private-messages")
public class PrivateMessageController {
    @Resource
    private PrivateMessageService privateMessageService;
    @Resource
    private CustomWebSocketHandler webSocketHandler;

    /**
     * 发送私聊消息接口。
     * 从安全工具中获取当前发送者的用户ID，将其设置到消息实体中，
     * 接着把消息保存到数据库，最后通过WebSocket实时推送给接收者。
     *
     * @param message 包含消息内容、接收者ID等信息的私聊消息实体
     * @return 操作结果，成功时返回包含成功信息的Result对象
     */
    /*@PostMapping
    public Result<String> sendMessage(@RequestBody PrivateMessage message) {
        Long senderId = SecurityUtils.getUserId();

        message.setSenderId(senderId);
        // 保存消息到数据库
        privateMessageService.saveMessage(senderId, message.getReceiverId(), message.getContent(), message.getMessageType());

        // 实时推送消息
        webSocketHandler.sendMessageToUser(
                message.getReceiverId(),
                message.getContent()
        );

        return Result.success();
    }*/


    /**
     * 获取与指定用户的会话消息
     * 根据当前用户ID、接收者ID、页码和每页数量，调用服务层方法获取分页的私聊消息历史记录。
     *
     * @param receiverId 接收者的用户ID
     * @param pageNum    页码
     * @param pageSize   每页显示的记录数量
     * @return 包含私聊消息历史记录的分页对象
     */
    @GetMapping("/history")
    public Result<IPage<PrivateMessageVo>> getHistory(
            @RequestParam Long receiverId,
            @RequestParam int pageNum,
            @RequestParam int pageSize) {
        return Result.success(privateMessageService.getPrivateMessages(SecurityUtils.getUserId(), receiverId, pageNum, pageSize));
    }

    /**
     * 获取有私聊消息往来的用户列表接口
     * 根据当前用户ID，调用服务层方法获取与当前用户有私聊消息往来的用户列表。
     *
     * @return 包含有私聊消息往来的用户信息的列表
     */
    @GetMapping("/users")
    public Result<List<PrivateMessageUserVo>> getUsers() {
        return Result.success(privateMessageService.getPrivateMessageUsers(SecurityUtils.getUserId()));
    }

    /**
     * 删除用户与指定用户的所有消息
     */
    @DeleteMapping("/{receiverId}")
    public Result<Boolean> deleteAllMessages(@PathVariable Long receiverId) {
        return privateMessageService.deleteAllMessages(receiverId);
    }
}