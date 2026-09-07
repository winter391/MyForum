package Code;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NettyCode
{
    public static Integer port;

    @Value("${netty.code.port}")
    public void setPort(Integer port) {
        NettyCode.port = port;
    }
}
