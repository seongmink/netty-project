package client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		p.addLast(new ByteArrayDecoder());
		p.addLast(new ClientHandler());
		p.addLast(new ByteArrayEncoder());
	}

}
