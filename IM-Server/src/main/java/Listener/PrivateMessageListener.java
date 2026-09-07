package Listener;

import Code.TerminalCode;
import Entity.PrivateMessage;
import Netty.ChannelMap;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;




@Component
public class PrivateMessageListener implements MessageListener
{


    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public void onMessage(Message message)
    {
        PrivateMessage privateMessage = (PrivateMessage) rabbitTemplate.getMessageConverter().fromMessage(message);
        String strMessage = JSON.toJSONString(privateMessage);
        Channel mobileChannel = ChannelMap.getUserChannelMap().get(ChannelMap.GetKey(privateMessage.getReceiverId(), TerminalCode.MOBILE_TERMINAL_CODE));
        Channel PCChannel = ChannelMap.getUserChannelMap().get(ChannelMap.GetKey(privateMessage.getReceiverId(), TerminalCode.PC_TERMINAL_CODE));
        if(mobileChannel!=null&&mobileChannel.isActive())
        {
            mobileChannel.writeAndFlush(new TextWebSocketFrame(strMessage));
        }
        if(PCChannel!=null&&PCChannel.isActive())
        {
            PCChannel.writeAndFlush(new TextWebSocketFrame(strMessage));
        }

    }

}
