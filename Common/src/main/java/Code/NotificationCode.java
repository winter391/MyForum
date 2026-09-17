package Code;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//通知链路的消息码，该链路独立于私信聊天链路
//Forum-Post发布帖子后向notification交换机发消息，Forum-Notification监听notificationPost队列
@Component
public class NotificationCode
{
    //通知交换机的名字
    public static String EXCHANGER;

    //帖子通知队列的名字
    public static String POST_QUEUE;

    //帖子通知的路由键
    public static String POST_ROUTING_KEY;

    //MessagePack的type：关注推送，关注的人发布了帖子
    public static short MESSAGE_TYPE_SUBSCRIBE = 0;

    //MessagePack的type：系统聊天消息
    public static short MESSAGE_TYPE_CHAT = 1;

    @Value("${spring.rabbitmq.code.notification-exchanger}")
    public void setExchanger(String exchanger) {
        EXCHANGER = exchanger;
    }

    @Value("${spring.rabbitmq.code.notification-post-queue}")
    public void setPostQueue(String postQueue) {
        POST_QUEUE = postQueue;
    }

    @Value("${spring.rabbitmq.code.notification-post-routing-key}")
    public void setPostRoutingKey(String postRoutingKey) {
        POST_ROUTING_KEY = postRoutingKey;
    }
}
