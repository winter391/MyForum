package Code;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedisCode
{
    public static Integer EXPIRE_TIME;

    public static Integer WAITING_TIME;

    public static String LOCK;

    public static String ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID;


    //这里key是较小id:较大id作为会话id
    public static String CHAT_KEY_TO_CHAT_ID;

    @Value("${spring.data.redis.code.lock}")
    public void setLOCK(String LOCK) {
        RedisCode.LOCK = LOCK;
    }

    @Value("${spring.data.redis.code.chat-key_to-chat-id}")
    public void setChatKeyToChatId(String chatKeyToChatId) {
        CHAT_KEY_TO_CHAT_ID = chatKeyToChatId;
    }


    @Value("${spring.data.redis.code.expire-time}")
    public void setExpireTime(Integer expireTime) {
        EXPIRE_TIME = expireTime;
    }

    @Value("${spring.data.redis.code.waiting-time}")
    public void setWaitingTime(Integer waitingTime) {
        WAITING_TIME = waitingTime;
    }

    @Value("${spring.data.redis.code.online-user-terminal-key}")
    public void setOnlineUserIdWithTerminalToServerId(String onlineUserIdWithTerminalToServerId) {
        ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID = onlineUserIdWithTerminalToServerId;
    }
}
