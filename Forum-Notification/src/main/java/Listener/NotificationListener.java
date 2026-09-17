package Listener;

import Code.SystemMessageTypeCode;
import Entity.SystemMessage;
import Message.ChatMessage;
import Message.MessagePack;
import Message.SubscribeMessage;
import Service.NotificationService;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//通知队列的监听器，负责消费Forum-Post等生产者发来的SystemMessage
//队列里传输的是整个SystemMessage，它的message字段存的是MessagePack序列化后的json
//这里解析出MessagePack的type，还原出领域消息后分发给Service处理
@Slf4j
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
        try
        {
            SystemMessage systemMessage = (SystemMessage) rabbitTemplate.getMessageConverter().fromMessage(message);
            //MessagePack的泛型在序列化时会丢失，message字段反序列化成的是JSONObject，这里按type转回具体类型
            MessagePack<?> pack = JSON.parseObject(systemMessage.getMessage(),MessagePack.class);
            if(pack.getType()==SystemMessageTypeCode.suscribe)
            {
                SubscribeMessage subscribeMessage = JSON.parseObject(JSON.toJSONString(pack.getMessage()),SubscribeMessage.class);
                notificationService.sendPostNotification(subscribeMessage);
            }
            else if(pack.getType()==SystemMessageTypeCode.chat)
            {
                ChatMessage chatMessage = JSON.parseObject(JSON.toJSONString(pack.getMessage()),ChatMessage.class);
                notificationService.broadcastSystemMessage(chatMessage);
            }
        }
        catch (Exception e)
        {
            //解析失败的消息记日志后直接丢弃，避免坏消息在队列里无限重投
            log.error("通知消息解析失败：{}",e.toString());
        }
    }
}
