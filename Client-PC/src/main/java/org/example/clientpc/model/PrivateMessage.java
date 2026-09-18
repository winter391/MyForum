package org.example.clientpc.model;

import lombok.Data;

/** 私聊消息（WebSocket 下发与历史消息共用此格式） */
@Data
public class PrivateMessage {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String message;
    /** 0 文字 1 文件 */
    private Short type;
    private Integer chatId;
    private String chatKey;
}
