package DLL.Impl;

import Code.RabbitMqCode;
import DLL.SendMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendMessageImpl implements SendMessage
{

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public void SendMessage(List<Integer> serverId,String message,Integer messageType)
    {
        if(message==null||serverId.isEmpty()) return;
        for(Integer id:serverId) {
            rabbitTemplate.convertAndSend(RabbitMqCode.MAIN_EXCHANGER, RabbitMqCode.joinRoutingKey(id,messageType.toString()));
        }
    }
}
