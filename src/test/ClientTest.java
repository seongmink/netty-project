import client.ClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Assert;
import org.junit.Test;

public class ClientTest {

	@Test
	public void ClientTestSetUp() {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup)
					.channel(NioSocketChannel.class)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.handler(new ClientInitializer());

			String ip = "127.0.0.1";
			int port = 8888;
			ChannelFuture f = b.connect(ip, port).sync();
			System.out.println("Client Start : " + ip + ":" + port);
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			Assert.fail();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}
}
