package Listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;


@Component
public class SystemMessageListener implements MessageListener
{
    @Override
    public void onMessage(Message message)
    {

    }
}
