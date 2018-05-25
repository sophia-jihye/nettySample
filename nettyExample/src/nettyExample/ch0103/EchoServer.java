package nettyExample.ch0103;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {
	public static void main(String[] args) throws Exception {

		// 부모 스레드그룹은 단일 스레드로 동작: 최대 1개까지만 생성 가능
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);

		// 서버는 연결된 채널이 여러 개 있을 수 있음
		// 최대 스레드 수 지정 X = 서버 app이 동작하는 하드웨어 CPU 코어 수의 2배를 사용함
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();

			// bossGroup: 클라이언트의 연결을 수락하는 부모 스레드 그룹
			// workerGroup: 연결된 클라이언트의 소켓에 대한 IO 처리를 담당하는 자식 스레드 그룹
			b.group(bossGroup, workerGroup)

					// 서버소켓(부모 스레드) NIO 설정
					.channel(NioServerSocketChannel.class)

					// 로깅 핸들러 추가
					// 서버 소켓에 대해서 동작
					.handler(new LoggingHandler(LogLevel.INFO))

					// 서버 소켓 옵션 추가
					.option(ChannelOption.SO_REUSEADDR, true)

					// 클라이언트 소켓 옵션 추가
					.childOption(ChannelOption.SO_LINGER, 0)

					// 자식 채널의 초기화 방법 설정
					// 서버 소켓 채널로 연결된 클라이언트 채널에 파이프라인을 설정
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) {

							// 채널 파이프라인 객체 생성
							ChannelPipeline p = ch.pipeline();

							// 로깅 핸들러 추가
							// 클라이언트와 통신하는 소켓에 대해서 동작
							p.addLast(new LoggingHandler(LogLevel.INFO));

							// 채널 파이프라인에 에코서버핸들러 등록
							p.addLast(new EchoServerHandler());
						}
					});

			// bind: 해당 포트번호로 서버 bind. 클라이언트의 연결을 기다립니다.
			ChannelFuture f = b.bind(8888).sync();

			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
