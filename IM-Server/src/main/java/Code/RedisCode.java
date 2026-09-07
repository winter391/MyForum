package Code;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class RedisCode
{
    public static String SERVER_ID;

    public static String ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID;

    @Value("${redis.code.serverId}")
    public void setServerId(String serverId) {
        SERVER_ID = serverId;
    }


    @Value("${spring.data.redis.code.online-user-terminal-key}")
    public void setOnlineUserIdWithTerminalToServerId(String onlineUserIdWithTerminalToServerId) {
        ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID = onlineUserIdWithTerminalToServerId;
    }


}
