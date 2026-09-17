package Listener;

import Code.NotificationCode;
import Message.ChatMessage;
import Message.MessagePack;
import Message.SubscribeMessage;
import Service.NotificationService;
import com.alibaba.fastjson2.JSON;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//通知队列的监听器，负责消费Forum-Post等生产者发来的MessagePack消息
//收到消息后根据type分发：0为关注推送，1为系统聊天消息
@Component
public class NotificationListener implements MessageListener
{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private NotificationService notificationService;


    @Override
    public void onMessage(Message message)
    {
        //MessagePack的泛型在序列化时会丢失，message字段反序列化成的是JSONObject，这里按type转回具体类型
        MessagePack<?> pack = (MessagePack<?>) rabbitTemplate.getMessageConverter().fromMessage(message);
        if(pack==null||pack.getType()==null)
        {
            return;
        }
        if(pack.getType()==NotificationCode.MESSAGE_TYPE_SUBSCRIBE)
        {
            SubscribeMessage subscribeMessage = JSON.parseObject(JSON.toJSONString(pack.getMessage()),SubscribeMessage.class);
            notificationService.sendPostNotification(subscribeMessage);
        }
        else if(pack.getType()==NotificationCode.MESSAGE_TYPE_CHAT)
        {
            ChatMessage chatMessage = JSON.parseObject(JSON.toJSONString(pack.getMessage()),ChatMessage.class);
            notificationService.broadcastSystemMessage(chatMessage);
        }
    }
}
