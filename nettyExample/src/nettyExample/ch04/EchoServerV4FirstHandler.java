package nettyExample.ch04;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handler implementation for the echo server.
 */
public class EchoServerV4FirstHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf readMessage = (ByteBuf) msg;
		System.out.println("FirstHandler channelRead : " + readMessage.toString(Charset.defaultCharset()));
		ctx.write(msg);

		// secondHandler의 channelRead()를 호출하기 위해
		// 채널 파이프라인에 이벤트를 발생시킴
		ctx.fireChannelRead(msg);
	}
}
