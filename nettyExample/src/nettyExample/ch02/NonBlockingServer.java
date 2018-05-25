package nettyExample.ch02;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NonBlockingServer {

	public static void main(String[] args) {
		NonBlockingServer main = new NonBlockingServer();
		main.startEchoServer();
	}

	private Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
	private ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);

	private void startEchoServer() {

		// java 1.7 기능
		// try 블럭이 끝날 때 소괄호 안에서 선언된 자원을 자동으로 해제해줌
		try (
				// selector: 자신에게 등록된 채널에 변경사항이 발생했는지 검사하고,
				// 변경 사항이 발생한 채널에 접근 가능하게 해줌
				Selector selector = Selector.open();

				// 서버 소켓 생성
				ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

			// 위 코드에서 생성한 selector와 서버소켓이 정상적으로 생성되었는지 확인
			if ((serverSocketChannel.isOpen()) && (selector.isOpen())) {

				// non-blocking 설정
				serverSocketChannel.configureBlocking(false);

				// bind: 해당 포트번호로 서버 bind. 클라이언트의 연결을 기다립니다.
				serverSocketChannel.bind(new InetSocketAddress(8888));

				// 서버소켓을 selector 객체에 등록
				// 이 때 selector가 해당 서버소켓에 대하여 감지할 이벤트는 SelectionKey.OP_ACCEPT
				// (연결 요청).
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
				System.out.println("접속 대기중");

				while (true) {

					// selector에 등록된 채널에서 변경사항이 발생했는지 검사
					selector.select();

					// IO이벤트가 발생한 채널 목록 조회
					Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

					while (keys.hasNext()) {

						// key 식별
						SelectionKey key = (SelectionKey) keys.next();

						// 조회 목록에서 제거
						keys.remove();

						if (!key.isValid()) {
							continue;
						}

						if (key.isAcceptable()) {
							// 연결 요청
							this.acceptOP(key, selector);

						} else if (key.isReadable()) {
							// 데이터 수신
							this.readOP(key);

						} else if (key.isWritable()) {
							// 데이터 송신
							this.writeOP(key);
						}
					}
				}
			} else {
				System.out.println("서버 소캣을 생성하지 못했습니다.");
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	// 연결 요청
	private void acceptOP(SelectionKey key, Selector selector) throws IOException {

		// 서버 소켓 채널
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();

		// 클라이언트 소켓 채널
		// accept: 클라이언트가 서버에 접속하면 서버 소켓으로부터 클라이언트 소켓을 얻어옴
		SocketChannel socketChannel = serverChannel.accept();

		// 클라이언트 소켓 non-blocking 설정
		socketChannel.configureBlocking(false);

		System.out.println("클라이언트 연결됨 : " + socketChannel.getRemoteAddress());

		keepDataTrack.put(socketChannel, new ArrayList<byte[]>());

		// 클라이언트 소켓 채널을 selector에 등록
		// 이 때 selector가 해당 클라이언트 소켓에 대하여 감지할 이벤트는 SelectionKey.OP_READ
		// (데이터 수신).
		socketChannel.register(selector, SelectionKey.OP_READ);
	}

	// 데이터 수신
	private void readOP(SelectionKey key) {
		try {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			buffer.clear();
			int numRead = -1;
			try {
				numRead = socketChannel.read(buffer);
			} catch (IOException e) {
				System.err.println("데이터 읽기 에러!");
			}

			if (numRead == -1) {
				this.keepDataTrack.remove(socketChannel);
				System.out.println("클라이언트 연결 종료 : " + socketChannel.getRemoteAddress());
				socketChannel.close();
				key.cancel();
				return;
			}

			byte[] data = new byte[numRead];
			System.arraycopy(buffer.array(), 0, data, 0, numRead);
			System.out.println(new String(data, "UTF-8") + " from " + socketChannel.getRemoteAddress());

			doEchoJob(key, data);
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	// 데이터 송신
	private void writeOP(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		List<byte[]> channelData = keepDataTrack.get(socketChannel);
		Iterator<byte[]> its = channelData.iterator();

		while (its.hasNext()) {
			byte[] it = its.next();
			its.remove();
			socketChannel.write(ByteBuffer.wrap(it));
		}

		key.interestOps(SelectionKey.OP_READ);
	}

	// 데이터 수신 후 전송
	private void doEchoJob(SelectionKey key, byte[] data) {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		List<byte[]> channelData = keepDataTrack.get(socketChannel);
		channelData.add(data);

		key.interestOps(SelectionKey.OP_WRITE);
	}

}
