package Code;

import org.springframework.beans.factory.annotation.Value;

public class RedisCode
{
    public static String SERVER_ID;

    @Value("reids.code.serverId")
    public void setServerId(String serverId) {
        SERVER_ID = serverId;
    }
}
