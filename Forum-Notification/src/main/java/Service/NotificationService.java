package Service;

import Message.ChatMessage;
import Message.SubscribeMessage;

public interface NotificationService
{
    //关注推送：把某位用户发布新帖子的消息推送给他的所有粉丝
    public void sendPostNotification(SubscribeMessage subscribeMessage);

    //系统聊天消息的广播：把消息发送给所有用户
    public void broadcastSystemMessage(ChatMessage chatMessage);

    //给单个用户发送一条系统消息，落库并推送到该用户在线的终端
    public void sendSystemMessage(Long receiverId, String message, Short type);
}
