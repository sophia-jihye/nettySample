package nettyExample.echo;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handler implementation for the echo client. It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

	// 소켓 채널이 최초 활성화 되었을 때 실행되는 메서드
	@Override
	public void channelActive(ChannelHandlerContext ctx) {

		// 1. 소켓 최초 연결 시 "Hello netty"라는 문자열을 서버로 보냄
		String sendMessage = "Hello netty";

		// netty 내부에서는 모든 데이터가 ByteBuf로 관리됨
		ByteBuf messageBuffer = Unpooled.buffer();
		messageBuffer.writeBytes(sendMessage.getBytes());

		// 2. 서버로 전송한 메세지를 클라이언트 콘솔에 출력함
		StringBuilder builder = new StringBuilder();
		builder.append("전송한 문자열 [");
		builder.append(sendMessage);
		builder.append("]");
		System.out.println(builder.toString());

		// 채널 파이프라인에 데이터 저장 및 전송
		ctx.writeAndFlush(messageBuffer);
	}

	// 서버로부터 데이터를 수신받을 때 호출되는 이벤트 메서드
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {

		// 1. 서버로부터 수신받은 메세지를 읽음
		String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());

		// 2. 읽은 메세지를 클라이언트 콘솔에 출력함
		StringBuilder builder = new StringBuilder();
		builder.append("수신한 문자열 [");
		builder.append(readMessage);
		builder.append("]");
		System.out.println(builder.toString());
	}

	// 서버로부터 수신받은 데이터를 모두 읽은 후 (=channelRead 메서드 수행 완료 후)
	// 호출되는 이벤트 메서드
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {

		// 해당 클라이언트와 서버 간 연결된 채널을 닫음 - 클라이언트 종료
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
