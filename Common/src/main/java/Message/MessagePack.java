package Message;

import lombok.Data;

//该类是SystemMessage的message字段的统一内容格式
//Forum-Notification组装SystemMessage时把领域消息包进该类，json序列化后存入message字段
//推送时传输的是整个SystemMessage，客户端根据type解析message
@Data
public class MessagePack<T>
{
    //消息类型，0：关注消息（关注的人发布了帖子），1：系统聊天消息
    private Short type;

    //消息本体，具体类型由type决定，0为SubscribeMessage，1为ChatMessage
    private T message;
}
