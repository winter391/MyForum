package Code;

import org.springframework.beans.factory.annotation.Value;

public class RedissonCode
{
    public static String LOCK_SERVER_ID;

    public static Integer WAITING_TIME;

    public static Integer EXPIRE_TIME;

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
}
