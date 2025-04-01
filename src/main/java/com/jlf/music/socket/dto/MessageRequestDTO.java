package com.jlf.music.socket.dto;

import com.jlf.music.common.enumerate.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息请求DTO
 */
@Data
@Accessors(chain = true)
public class MessageRequestDTO {
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 500, message = "消息内容不能超过500字")
    private String content;

    private MessageType messageType = MessageType.TEXT;
}
