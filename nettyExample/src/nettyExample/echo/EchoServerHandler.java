package nettyExample.echo;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

// ChannelInboundHandlerAdapter 구현
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

	// [3] 이벤트 핸들러의 데이터 수신 이벤트 메서드에서 데이터를 읽어들인다
	// 클라이언트로부터 데이터 수신이 이루어졌을 때 netty가 자동으로 호출하는 이벤트 메서드
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {

		// 1. 클라이언트가 보낸 메세지를 읽음
		// netty 내부에서는 모든 데이터가 ByteBuf로 관리됨
		String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());

		// 2. 읽은 메세지를 서버 콘솔에 출력함
		StringBuilder builder = new StringBuilder();
		builder.append("수신한 문자열 [");
		builder.append(readMessage);
		builder.append("]");
		System.out.println(builder.toString());

		// 채널 파이프라인에 데이터 저장
		ctx.write(msg);
	}

	// channelRead 이벤트 처리 완료 후 자동으로 수행되는 이벤트 메서드
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {

		// 채널 파이프라인에 저장된 버퍼 전송
		ctx.flush();
	}

	// [4] 이벤트 핸들러의 네트워크 끊김 이벤트 메서드에서 에러 처리를 한다
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}

}
