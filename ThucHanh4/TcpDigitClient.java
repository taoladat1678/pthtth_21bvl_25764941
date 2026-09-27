import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpDigitClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5001;
        
        try (Socket socket = new Socket(host, port);
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            
            System.out.println("Đã kết nối tới Server. Nhập 1 chữ số (0-9) hoặc QUIT để thoát.");
            String request;
            
            while ((request = console.readLine()) != null) {
                out.println(request);
                String response = in.readLine();
                
                if (response == null) {
                    System.out.println("Server đã đóng kết nối.");
                    break;
                }
                
                System.out.println("Server: " + response);
                
                if (request.equals("QUIT")) {
                    break;
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
        } catch (IOException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        }
    }
}
