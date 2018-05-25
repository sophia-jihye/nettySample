package nettyExample.ch0103;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

// SimpleChannelInboundHandler 구현
public class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 아무것도 하지 않음.
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
