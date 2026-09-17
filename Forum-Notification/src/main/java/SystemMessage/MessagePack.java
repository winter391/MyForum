package SystemMessage;


import lombok.Data;

@Data
public class MessagePack<T>
{
    //记录传输的消息类型，0：关注消息，1：系统聊天消息
    private Short type;

    private T message;
}
