package Code;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class RabbitMqCode
{
    public static String MAIN_EXCHANGER;

    public static String SERVER_ID;

    public static String MESSAGE_TYPE;


    public static String PRIVATE_MESSAGE;

    public static String SYSTEM_MESSAGE;

    @Value("${spring.rabbitmq.code.main-exchanger}")
    public void setMainExchanger(String mainExchanger) {
        MAIN_EXCHANGER = mainExchanger;
    }

    @Value("${spring.rabbitmq.code.server-id}")
    public void setServerId(String serverId) {
        SERVER_ID = serverId;
    }

    @Value("${spring.rabbitmq.code.message-type}")
    public void setMessageType(String messageType) {
        MESSAGE_TYPE = messageType;
    }

    @Value("${spring.rabbitmq.code.private-message}")
    public void setPrivateMessage(String privateMessage) {
        PRIVATE_MESSAGE = privateMessage;
    }

    @Value("${spring.rabbitmq.code.system-message}")
    public void setSystemMessage(String systemMessage) {
        SYSTEM_MESSAGE = systemMessage;
    }


    public static String joinRoutingKey(Integer serverId,String messageType)
    {
        return String.join(".",SERVER_ID+":"+serverId.toString(),MESSAGE_TYPE+":"+messageType);
    }

}
