package org.example.clientpc.model;

import lombok.Data;

/**
 * 系统消息（关注的人发帖推送等）。message 字段是 MessagePack 的 JSON 字符串。
 */
@Data
public class SystemMessage {
    private Long id;
    private Long receiverId;
    private String message;
    private Short type;
    private Integer chatId;
}
