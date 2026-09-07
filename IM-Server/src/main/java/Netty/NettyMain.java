package Netty;



import Code.NettyCode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NettyMain
{
    private final WebSocketChannelInitializer initializer;


    public NettyMain(WebSocketChannelInitializer initializer)
    {
        this.initializer = initializer;
    }

    public void run()
    {
        EventLoopGroup boss = new MultiThreadIoEventLoopGroup(1,NioIoHandler.newFactory());
        EventLoopGroup worker = new MultiThreadIoEventLoopGroup(2,NioIoHandler.newFactory());

        try
        {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(initializer);
            bootstrap.bind(NettyCode.port).sync().channel().closeFuture().sync();
        }
        catch (Exception e)
        {
            log.error("netty启动出错:{}",e.toString());
        }
        finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}
