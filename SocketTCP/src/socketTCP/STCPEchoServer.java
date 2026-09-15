package socketTCP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class STCPEchoServer {
	public final static int serverPort = 3667;

	public static void main(String[] args) throws IOException {

		try {
			// tao server
			ServerSocket ss = new ServerSocket(serverPort);
			System.out.println("Server created");

			while (true) {
				try {
					Socket s = ss.accept();
					OutputStream os = s.getOutputStream();
					InputStream is = s.getInputStream();
					int ch;
					while (true) {
						ch = is.read();
						if (ch == -1) break;
						System.out.println((char) ch);
						os.write(ch);
					}
					s.close();

				} catch (IOException iel) {
					System.out.println("Connection Error " + iel);
				}
			}
		} catch (IOException ie) {
			System.out.println("Server Error " + ie);
		}
	}
}