package Code;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//通知模块用到的redis key，取值与IM-platform保持一致，共同组成原有的消息链路
@Component
public class RedisCode
{
    //Redisson分布式锁key的前缀
    public static String LOCK = "lock";

    //系统与用户会话的chatId计数器的key前缀
    public static String CHAT_KEY_TO_CHAT_ID = "chatKey:ChatId";

    //获取Redisson锁的最长等待时间，单位：秒
    public static Integer WAITING_TIME = 30;

    //用户终端到IM-Server的在线映射key的前缀，该值在application-common中统一配置
    public static String ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID;

    @Value("${spring.data.redis.code.online-user-terminal-key}")
    public void setOnlineUserIdWithTerminalToServerId(String onlineUserIdWithTerminalToServerId) {
        ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID = onlineUserIdWithTerminalToServerId;
    }
}
