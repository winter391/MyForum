package DLL.Impl;

import Code.RabbitMqCode;
import DLL.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SendMessageImpl implements SendMessage
{

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public <T>void SendMessage(List<Integer> serverId, T message,String messageType)
    {
        if(message==null||serverId.isEmpty()) return;
        for(Integer id:serverId) {
            log.debug("RoutingKey为：{}",RabbitMqCode.joinRoutingKey(id,messageType));
            rabbitTemplate.convertAndSend(RabbitMqCode.MAIN_EXCHANGER, RabbitMqCode.joinRoutingKey(id,messageType),message);
        }
    }
}
