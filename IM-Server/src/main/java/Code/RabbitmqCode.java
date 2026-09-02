package Code;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitmqCode {

    public static String SERVER_NAME;

    public static String privateMessageQueueName;

    public static String systemMessageQueueName;

    public static String privateMessageRoutingKey;

    public static String systemMessageRoutingKey;

    private static String mainExchangerRoutingKey;

    public static String mainExchanger;

    @Value("${spring.rabbitmq.code.server-name}")
    public void setServerName(String serverName) {
        SERVER_NAME = serverName;
    }

    @Value("${spring.rabbitmq.code.private-message-queue-name}")
    public void setPrivateMessageQueueName(String privateMessageQueueName) {
        RabbitmqCode.privateMessageQueueName = privateMessageQueueName;
    }

    @Value("${spring.rabbitmq.code.system-message-queue-name}")
    public void setSystemMessageQueueName(String systemMessageQueueName) {
        RabbitmqCode.systemMessageQueueName = systemMessageQueueName;
    }

    @Value("${spring.rabbitmq.code.private-message-routing-key}")
    public void setPrivateMessageRoutingKey(String privateMessageRoutingKey) {
        RabbitmqCode.privateMessageRoutingKey = privateMessageRoutingKey;
    }

    @Value("${spring.rabbitmq.code.system-message-routing-key}")
    public void setSystemMessageRoutingKey(String systemMessageRoutingKey) {
        RabbitmqCode.systemMessageRoutingKey = systemMessageRoutingKey;
    }

    @Value("${spring.rabbitmq.code.main-exchanger}")
    public void setMainExchanger(String mainExchanger) {
        RabbitmqCode.mainExchanger = mainExchanger;
    }

    @Value("${spring.rabbitmq.code.main-exchanger-routing-key}")
    public void setMainExchangerRoutingKey(String mainExchangerRoutingKey) {
        RabbitmqCode.mainExchangerRoutingKey = mainExchangerRoutingKey;
    }

    public static String getMainExchangerRoutingKey(Long id)
    {
        return mainExchangerRoutingKey+id+".*";
    }
}
