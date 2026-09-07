package Netty.Handler;

import Code.JwtCode;
import Entity.UserSession;
import Netty.Event.ReceiveToken;
import Netty.SignKey;
import Result.Result;
import Result.ResultUtil;
import Util.JwtUtil;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;



//本类是为了实现，用户在建立链接之后，发送token来登录，仅在刚刚建立连接时使用
public class LoginVerifyTokenHandler extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {

        if(msg instanceof TextWebSocketFrame frame)
        {
            String token = frame.text();
            if(JwtUtil.verifyToken(token, JwtCode.accessTokenSecret))
            {
                String user =JwtUtil.getInfo(token,JwtCode.accessTokenSecret);
                UserSession session = JSON.parseObject(user,UserSession.class);
                ctx.channel().attr(SignKey.session).set(session);
                Result<?> result = ResultUtil.success();
                String json = JSON.toJSONString(result);
                ctx.channel().attr(SignKey.verified).set(true);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(json));
                ctx.fireUserEventTriggered(new ReceiveToken(token));
            }
            else
            {
                Result<?> result = ResultUtil.failed("Token过期或错误，登录失败");
                String json = JSON.toJSONString(result);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(json));
                ctx.channel().close();
            }
        }
    }
}