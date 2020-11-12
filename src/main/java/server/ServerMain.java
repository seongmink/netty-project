package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;

public class ServerMain {
    public static void main(String[] args) {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        // 클라이언트의 연결을 수락하는 부모 스레드 그룹
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        // 연결된 클라이언트 소켓으로부터 데이터 입출력(I/O) 및 이벤트처리를 담당하는 자식 쓰레드 그룹
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);
        try {
            // ServerBootstrap 클래스를 사용하면 서버에서 Channel을 직접 세팅 할 수 있음
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 새로운 채널을 객체화 하는 클래스 지정
                    .childHandler(new ServerInitializer())
                    .option(ChannelOption.SO_BACKLOG, 50)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            int port = 8888;
            ChannelFuture f = sb.bind(port).sync();
            System.out.println("Server Start : " + port);
            f.channel().closeFuture().sync();
            f.addListener(ChannelFutureListener.CLOSE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
