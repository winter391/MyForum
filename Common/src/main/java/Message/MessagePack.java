package Message;

import lombok.Data;

//该类是发往通知队列的消息的统一外壳
//Forum-Post等生产者把它发到notification交换机，Forum-Notification监听队列后根据type分发处理
@Data
public class MessagePack<T>
{
    //消息类型，0：关注消息（关注的人发布了帖子），1：系统聊天消息
    private Short type;

    //消息本体，具体类型由type决定，0为SubscribeMessage，1为ChatMessage
    private T message;
}
