package Code;


import lombok.Getter;
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

    @Value("${Spring.rabbitmq.Code.mainExchanger}")
    public void setMainExchanger(String mainExchanger) {
        MAIN_EXCHANGER = mainExchanger;
    }

    @Value("${Spring.rabbitmq.Code.serverId}")
    public void setServerId(String serverId) {
        SERVER_ID = serverId;
    }

    @Value("${Spring.rabbitmq.Code.messageType}")
    public void setMessageType(String messageType) {
        MESSAGE_TYPE = messageType;
    }

    @Value("${Spring.rabbitmq.Code.privateMessage}")
    public void setPrivateMessage(String privateMessage) {
        PRIVATE_MESSAGE = privateMessage;
    }

    @Value("${Spring.rabbitmq.Code.systemMessage}")
    public void setSystemMessage(String systemMessage) {
        SYSTEM_MESSAGE = systemMessage;
    }


    public static String joinRoutingKey(Integer serverId,String messageType)
    {
        return String.join(".",SERVER_ID+":"+serverId.toString(),MESSAGE_TYPE+":"+messageType);
    }

    public static String getMainExchanger() {
        return MAIN_EXCHANGER;
    }

    public static String getServerId() {
        return SERVER_ID;
    }

    public static String getMessageType() {
        return MESSAGE_TYPE;
    }

    public static String getPrivateMessage() {
        return PRIVATE_MESSAGE;
    }

    public static String getSystemMessage() {
        return SYSTEM_MESSAGE;
    }
}
