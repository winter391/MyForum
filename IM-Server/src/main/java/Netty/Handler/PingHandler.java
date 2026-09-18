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
public class PingHandler extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {

        if(msg instanceof TextWebSocketFrame frame)
        {
            String ping  = frame.text();
            if(ping!=null&&ping.equals("ping"))
            {
                ctx.channel().writeAndFlush(new TextWebSocketFrame("pong"));
                return;
            }
            else
            {
                ctx.fireChannelRead(msg);
            }
        }
    }
}