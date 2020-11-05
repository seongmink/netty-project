package src.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientMain {

    public static void main(String[] args) {

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            // 서버에 연결된 채널 하나만 존재
            b.group(workerGroup)
                    // 서버에 연결된 클라이언트 소켓 채널이 어떤 방식으로 동작할지
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ClientInitializer());

//      String ip = "172.21.25.48";
            String ip = "127.0.0.1";
            int port = 8888;
            ChannelFuture f = b.connect(ip, port).sync();
            System.out.println("Client Start : " + ip + ":" + port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
