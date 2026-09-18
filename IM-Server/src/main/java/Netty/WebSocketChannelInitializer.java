package Netty;

import Netty.Handler.ListenerHandler;
import Netty.Handler.LoginVerifyTokenHandler;
import Netty.Handler.OnLineHeartBeatHandler;
import Netty.Handler.PingHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;



@Component
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel>
{
    private final RedisTemplate<String,Object> redisTemplate;

    private final RedissonClient redissonClient;

    public WebSocketChannelInitializer(RedisTemplate<String,Object> redisTemplate,RedissonClient redissonClient)
    {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new HttpServerCodec())
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpObjectAggregator(1024*64))
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                .addLast(new IdleStateHandler(60,0,0, TimeUnit.SECONDS))
                .addLast(new OnLineHeartBeatHandler(redisTemplate,redissonClient))
                .addLast(new PingHandler())
                .addLast(new LoginVerifyTokenHandler())
                .addLast(new ListenerHandler(redisTemplate,redissonClient));
    }
}
