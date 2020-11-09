package server;


import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionHandler extends ChannelHandlerAdapter {

	private final AtomicInteger connections = new AtomicInteger();
	private final int max = 100; // 최대 100명까지만 접속 허용
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		int val = connections.incrementAndGet();
		if (val <= max)
			super.channelActive(ctx);
		else
			ctx.writeAndFlush("Access Limit!").addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		connections.decrementAndGet();
	}
}
