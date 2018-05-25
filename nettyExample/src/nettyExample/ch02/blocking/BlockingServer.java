package nettyExample.ch02.blocking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingServer {
	public static void main(String[] args) throws Exception {
		BlockingServer server = new BlockingServer();
		server.run();
	}

	private void run() throws IOException {

		// 서버 소켓 생성
		ServerSocket server = new ServerSocket(8888);
		System.out.println("접속 대기중");

		while (true) {

			// accept: 클라이언트가 서버에 접속하면 서버 소켓으로부터 클라이언트 소켓을 얻어옴
			Socket sock = server.accept();
			System.out.println("클라이언트 연결됨");

			OutputStream out = sock.getOutputStream();
			InputStream in = sock.getInputStream();

			while (true) {
				try {
					int request = in.read();
					out.write(request);
				} catch (IOException e) {
					break;
				}
			}
		}
	}

}
