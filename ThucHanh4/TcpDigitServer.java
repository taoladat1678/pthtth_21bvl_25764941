import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpDigitServer {
    private static final int PORT = 5001;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("TCP Digit Server đang chạy trên port " + PORT + "...");
            while (true) {
                try {
                    Socket socket = server.accept();
                    // Tạo thread mới để xử lý nhiều client đồng thời (tuỳ chọn nhưng tốt cho thực tế)
                    new Thread(() -> serve(socket)).start();
                } catch (IOException e) {
                    System.err.println("Lỗi phiên client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Không mở được server: " + e.getMessage());
        }
    }

    static void serve(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String request;
            while ((request = in.readLine()) != null) {
                if (request.equals("QUIT")) {
                    out.println("OK BYE");
                    break;
                }
                
                String response = process(request);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Bỏ qua lỗi khi đóng socket
            }
        }
    }

    static String process(String request) {
        // Kiểm tra nếu dữ liệu không phải đúng MỘT ký tự
        if (request == null || request.length() != 1) {
            return "ERR INVALID_DIGIT";
        }
        
        char c = request.charAt(0);
        switch (c) {
            case '0': return "Không";
            case '1': return "Một";
            case '2': return "Hai";
            case '3': return "Ba";
            case '4': return "Bốn";
            case '5': return "Năm";
            case '6': return "Sáu";
            case '7': return "Bảy";
            case '8': return "Tám";
            case '9': return "Chín";
            default: return "ERR INVALID_DIGIT";
        }
    }
}
