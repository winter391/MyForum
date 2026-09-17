package Message;

import lombok.Data;

//系统聊天消息：后台系统向用户发送的消息
//该消息没有指定接收者，Forum-Notification收到后会把消息广播给所有用户
@Data
public class ChatMessage
{
    //消息本体
    private String message;
}
