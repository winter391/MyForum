package Message;

import lombok.Data;

//关注推送消息：某个用户发布了帖子之后，Forum-Post向通知队列发送该消息
//Forum-Notification收到后，会把推送发送给发布者的所有粉丝
@Data
public class SubscribeMessage
{
    //发帖用户的id
    private Long publisherId;

    //发帖用户的昵称
    private String publisherNickname;

    //新帖子的id
    private Long postId;

    //新帖子的标题
    private String postTitle;

    //帖子所属贴吧的名字
    private String barName;
}
