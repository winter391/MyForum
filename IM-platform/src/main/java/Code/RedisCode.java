package Code;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedisCode
{
    public static Integer EXPIRE_TIME;

    public static Integer WATTING_TIME;

    public static String LOCK;

    public static String ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID;


    //这里key是较小id:较大id作为会话id
    public static String CHAT_KEY_TO_CHAT_ID;

    @Value("${Spring.data.redis.Code.lock}")
    public void setLOCK(String LOCK) {
        RedisCode.LOCK = LOCK;
    }

    @Value("${Spring.data.redis.Code.chatKey_to_chatId}")
    public void setChatKeyToChatId(String chatKeyToChatId) {
        CHAT_KEY_TO_CHAT_ID = chatKeyToChatId;
    }


    @Value("${Spring.data.redis.Code.expireTime}")
    public static void setExpireTime(Integer expireTime) {
        EXPIRE_TIME = expireTime;
    }

    @Value("${Spring.data.redis.Code.waitingTime}")
    public static void setWaitingTime(Integer waitingTime) {
        WATTING_TIME = waitingTime;
    }

    @Value("${Spring.data.redis.Code.onlineUserTerminal}")
    public void setOnlineUserIdWithTerminalToServerId(String onlineUserIdWithTerminalToServerId) {
        ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID = onlineUserIdWithTerminalToServerId;
    }
}
