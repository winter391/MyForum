package Netty.Handler;

import Code.RedisCode;
import Code.RedissonCode;
import Entity.UserSession;
import Netty.ChannelMap;
import Netty.SignKey;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class OnLineHeartBeatHandler extends ChannelInboundHandlerAdapter
{

    private RedisTemplate<String,Object> redisTemplate;

    private RedissonClient redissonClient;

    public OnLineHeartBeatHandler(RedisTemplate<String, Object> redisTemplate,RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent event)
        {
            UserSession session = ctx.channel().attr(SignKey.session).get();
            if(session==null)
            {
                ctx.channel().writeAndFlush(new TextWebSocketFrame("长时间未传递Token，连接已断开"));
                ctx.channel().close();
                return ;
            }
            RLock lock = redissonClient.getLock(String.join(":",RedissonCode.ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID_LOCK,session.getId().toString(),session.getTerminal().toString()));
            Boolean sign = lock.tryLock(RedissonCode.WAITING_TIME,RedissonCode.EXPIRE_TIME, TimeUnit.SECONDS);
            try {
                if(sign) {
                    String key = ChannelMap.GetKey(session.getId(), session.getTerminal());
                    ChannelMap.getUserChannelMap().remove(key);
                    redisTemplate.delete(String.join(":", RedisCode.ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID,session.getId().toString(),session.getTerminal().toString()));
                    ctx.channel().close();
                }
            }
            finally {
                if(sign) lock.unlock();
            }

        }
        super.userEventTriggered(ctx,evt);
    }
}
