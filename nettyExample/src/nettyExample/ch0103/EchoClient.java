package nettyExample.ch0103;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Sends one message when a connection is open and echoes back any received data
 * to the server. Simply put, the echo client initiates the ping-pong traffic
 * between the echo client and server by sending the first message to the
 * server.
 */
public final class EchoClient {
	public static void main(String[] args) throws Exception {

		// 클라이언트는 연결된 채널이 하나만 존재
		EventLoopGroup group = new NioEventLoopGroup();

		try {

			// 클라이언트 app을 위한 bootstrap 객체 생성
			Bootstrap b = new Bootstrap();
			b.group(group)

					// 클라이언트 소켓 채널 NIO 설정
					.channel(NioSocketChannel.class)

					// 클라이언트 소켓 채널 이벤트 핸들러 설정을 위해 객체 생성
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();

							// 클라이언트 소켓 채널 이벤트 핸들러 등록
							p.addLast(new EchoClientHandler());
						}
					});

			ChannelFuture f = b.connect("localhost", 8888).sync();

			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}
}
