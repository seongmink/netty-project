
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.junit.Assert;
import org.junit.Test;
import server.ServerInitializer;

public class ServerTest {

	@Test
	public void ServerTestSetUp() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ServerInitializer()).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			int port = 8888;
			ChannelFuture f = sb.bind(port).sync();
			System.out.println("Server Start : " + port);
			f.channel().closeFuture().sync();
			f.addListener(ChannelFutureListener.CLOSE);
		} catch (InterruptedException e) {
			Assert.fail();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
