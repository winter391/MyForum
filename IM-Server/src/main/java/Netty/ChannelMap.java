package Netty;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;


import java.util.concurrent.ConcurrentHashMap;




@Component
public class ChannelMap
{
    private static ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    public static String GetKey(Long id,Integer Terminal)
    {
        return String.join(":",id.toString(),Terminal.toString());
    }



    public static ConcurrentHashMap<String, Channel> getUserChannelMap() {
        return userChannelMap;
    }
}
