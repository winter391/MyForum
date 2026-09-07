package Code;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class RedissonCode
{
    public static String LOCK_SERVER_ID;

    public static Integer WAITING_TIME;

    public static Integer EXPIRE_TIME;

    public static String ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID_LOCK;

    @Value("${redis.lock.code.lock-serverId}")
    public void setLockServerId(String serverId) {
        LOCK_SERVER_ID = serverId;
    }

    @Value("${redis.lock.code.waiting-time}")
    public void setWaitingTime(Integer waitingTime) {
        WAITING_TIME = waitingTime;
    }

    @Value("${redis.lock.code.expire-time}")
    public void setExpireTime(Integer expireTime) {
        EXPIRE_TIME = expireTime;
    }

    @Value("${redis.lock.code.online-user-terminal-lock}")
    public void setOnlineUserIdWithTerminalToServerIdLock(String onlineUserIdWithTerminalToServerIdLock) {
        ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID_LOCK = onlineUserIdWithTerminalToServerIdLock;
    }
}
