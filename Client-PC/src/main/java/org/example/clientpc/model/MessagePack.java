package org.example.clientpc.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * SystemMessage.message 的统一内容格式。
 * type：0 关注的人发帖推送（message 为 SubscribeMessage），1 系统聊天消息（message 为 ChatMessage）。
 */
@Data
public class MessagePack {
    private Short type;
    private JsonNode message;

    public static final short TYPE_SUBSCRIBE = 0;
    public static final short TYPE_CHAT = 1;

    /** type=0 时解析出的关注推送内容 */
    @Data
    public static class SubscribeMessage {
        private Long publisherId;
        private String publisherNickname;
        private Long postId;
        private String postTitle;
        private String barName;
    }
}
