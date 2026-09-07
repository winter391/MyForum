package Netty.Handler;


import Code.RedisCode;
import Code.RedissonCode;
import Code.ServerId;
import Entity.UserSession;
import Netty.ChannelMap;
import Netty.Event.ReceiveToken;
import Netty.SignKey;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;



//这个handler的目的是为了实现不同server之间的，同一类型终端的互踢
@Slf4j
public class ListenerHandler extends ChannelInboundHandlerAdapter
{

    private final RedisTemplate<String,Object> redisTemplate;

    private final RedissonClient redissonClient;



    public ListenerHandler(RedisTemplate<String,Object> redisTemplate, RedissonClient redissonClient)
    {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }



    @Override
    public void userEventTriggered(ChannelHandlerContext ctx,Object evt) throws Exception {
        if (evt instanceof ReceiveToken) {
            UserSession user = ctx.channel().attr(SignKey.session).get();
            RLock lock = redissonClient.getLock(String.join(":",RedissonCode.ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID_LOCK,user.getId().toString(),user.getTerminal().toString()));
            Boolean sign = lock.tryLock(RedissonCode.WAITING_TIME,RedissonCode.EXPIRE_TIME, TimeUnit.SECONDS);
            try {
                if (sign) {
                    Long id = (Long) redisTemplate.opsForValue().get(String.join(":", RedisCode.ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID, user.getId().toString(), user.getTerminal().toString()));
                    if (id != null) {
                        ctx.channel().attr(SignKey.REJECT).set(true);
                        ctx.channel().writeAndFlush(new TextWebSocketFrame("已有同类终端账号登录，请先登出之前的账号"));
                        ctx.channel().close();
                        return;
                    } else {
                        redisTemplate.opsForValue().set(String.join(":", RedisCode.ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID, user.getId().toString(), user.getTerminal().toString()), ServerId.id);
                    }
                    ctx.channel().attr(SignKey.REJECT).set(false);
                    ChannelMap.getUserChannelMap().put(ChannelMap.GetKey(user.getId(), user.getTerminal()), ctx.channel());
                    ctx.channel().attr(SignKey.Logined).set(true);
                } else {
                    log.error("用户登录失败,无法获取锁");
                }
            } finally {
                if(sign)
                {
                    lock.unlock();
                }
            }
        }
        super.userEventTriggered(ctx,evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        UserSession user = ctx.channel().attr(SignKey.session).get();
        if(user!=null&&ctx.channel().attr(SignKey.REJECT).get()!=null&&!ctx.channel().attr(SignKey.REJECT).get()) {
            RLock lock = redissonClient.getLock(String.join(":",RedissonCode.ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID_LOCK,user.getId().toString(),user.getTerminal().toString()));
            Boolean sign = lock.tryLock(RedissonCode.WAITING_TIME,RedissonCode.EXPIRE_TIME,TimeUnit.SECONDS);
            try {

                if(sign) {
                    ChannelMap.getUserChannelMap().remove(ChannelMap.GetKey(user.getId(), user.getTerminal()));
                    redisTemplate.delete(String.join(":", RedisCode.ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID, user.getId().toString(), user.getTerminal().toString()));
                }
            }
            finally
            {
                if(sign)
                {
                    lock.unlock();
                }
            }
        }
    }

}
