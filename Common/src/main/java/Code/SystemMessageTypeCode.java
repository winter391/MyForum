package Code;

public class SystemMessageTypeCode
{
    //MessagePack的type：关注推送，关注的人发布了帖子
    public static final short suscribe = 0;

    //MessagePack的type：系统聊天消息
    public static final short chat = 1;

    //SystemMessage的type：消息内容类型，0：文字，1：文件
    public static final short text = 0;

    //SystemMessage的type：消息内容类型，1：文件
    public static final short file = 1;
}
