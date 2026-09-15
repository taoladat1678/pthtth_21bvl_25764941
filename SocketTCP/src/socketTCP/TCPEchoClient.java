package socketTCP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPEchoClient {
	public final static String serverIP = "127.0.0.1";
	public final static int serverPort = 3667;

	public static void main(String[] args) throws IOException, InterruptedException {
		Socket s = null;
		try {
			s = new Socket(serverIP, serverPort);
			System.out.println("CLient created");
			InputStream is = s.getInputStream();
			OutputStream os = s.getOutputStream();
			for (int i = '0'; i < '9'; i++) { // goi 0-9 den server
				// tao output stream gui cho server
				os.write(i);
				// nhan du lieu
				int ch = is.read();
				System.out.println((char) ch);
				Thread.sleep(2000);
			}
		} catch (IOException ie) {
			System.out.println("Can't create socket");
		} finally {
			if (s != null) {
				s.close();
			}
		}

	}

}
