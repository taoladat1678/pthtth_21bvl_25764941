import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class DateTimeUdpClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5002;
        try (DatagramSocket socket = new DatagramSocket();
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            
            // Thiết lập timeout 3 giây. Nếu server chết, socket.receive() sẽ văng lỗi thay vì bị treo vĩnh viễn.
            socket.setSoTimeout(3000); 
            
            InetAddress address = InetAddress.getByName(host);
            System.out.println("Client UDP sẵn sàng. Nhập DATE, TIME, DATETIME (Gõ EXIT để tắt client):");
            String request;
            while ((request = console.readLine()) != null) {
                if (request.equalsIgnoreCase("EXIT")) break;
                
                byte[] reqBytes = request.getBytes(StandardCharsets.UTF_8);
                DatagramPacket reqPacket = new DatagramPacket(reqBytes, reqBytes.length, address, port);
                socket.send(reqPacket);
                
                byte[] buf = new byte[1024];
                DatagramPacket resPacket = new DatagramPacket(buf, buf.length);
                
                try {
                    socket.receive(resPacket);
                    String response = new String(resPacket.getData(), 0, resPacket.getLength(), StandardCharsets.UTF_8);
                    System.out.println("Server: " + response);
                } catch (SocketTimeoutException e) {
                    System.out.println("Lỗi: Không nhận được phản hồi từ server (Request Timeout). Có thể Server đã dừng.");
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi UDP Client: " + e.getMessage());
        }
    }
}
