package bai07;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MessageLogClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : MessageLogServer.DEFAULT_PORT;
        boolean autoTest = args.length > 2 && args[2].equalsIgnoreCase("--test");

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            System.out.println("Đã kết nối tới Message Log Server tại " + host + ":" + port);

            if (autoTest) {
                runLogTests(out, in);
                return;
            }

            System.out.println("Hãy gửi: HELLO <clientId> (chỉ chữ, số, - và _)");
            System.out.println("Sau đó gửi các dòng nội dung tin nhắn. Nhập QUIT để thoát.");

            String line;
            while ((line = console.readLine()) != null) {
                out.println(line);
                String resp = in.readLine();
                if (resp == null) {
                    System.out.println("Server đã đóng kết nối.");
                    break;
                }
                System.out.println("Server: " + resp);
                if (line.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối tới Server: " + e.getMessage());
        }
    }

    private static void runLogTests(PrintWriter out, BufferedReader in) throws IOException {
        System.out.println(">>> BẮT ĐẦU KIỂM THỬ BÀI 7: SERVER LƯU NHẬT KÝ TIN NHẮN <<<");

        // 1. Thử gửi nội dung trước khi HELLO
        System.out.println("-> Thử gửi tin nhắn khi chưa HELLO: [Tin nhan dau tien]");
        out.println("Tin nhan dau tien");
        System.out.println("<- Nhận: " + in.readLine());

        // 2. Thử HELLO với clientId sai quy cách (chứa ký tự đặc biệt)
        System.out.println("-> Thử HELLO với clientId không hợp lệ: [HELLO user@123#]");
        out.println("HELLO user@123#");
        System.out.println("<- Nhận: " + in.readLine());

        // 3. HELLO với clientId hợp lệ (chữ, số, gạch ngang, gạch dưới)
        System.out.println("-> Gửi HELLO hợp lệ: [HELLO sinhvien_iuh-01]");
        out.println("HELLO sinhvien_iuh-01");
        System.out.println("<- Nhận: " + in.readLine());

        // 4. Gửi các tin nhắn thường
        System.out.println("-> Gửi tin nhắn 1: [Xin chào server, đây là tin nhắn số 1]");
        out.println("Xin chào server, đây là tin nhắn số 1");
        System.out.println("<- Nhận: " + in.readLine());

        // 5. Gửi tin nhắn chứa chuỗi "HET" (yêu cầu đề bài: chuỗi HET lẫn trong nội dung vẫn ghi nhận, không dừng)
        System.out.println("-> Gửi tin nhắn chứa chữ HET: [Toi da lam HET cac bai tap thuc hanh]");
        out.println("Toi da lam HET cac bai tap thuc hanh");
        System.out.println("<- Nhận: " + in.readLine());

        // 6. Gửi dòng tin nhắn chỉ có chữ HET
        System.out.println("-> Gửi tin nhắn là từ HET đơn lẻ: [HET]");
        out.println("HET");
        System.out.println("<- Nhận: " + in.readLine());

        // 7. Gửi QUIT để kết thúc phiên
        System.out.println("-> Gửi lệnh QUIT để kết thúc: [QUIT]");
        out.println("QUIT");
        System.out.println("<- Nhận: " + in.readLine());

        System.out.println(">>> HOÀN TẤT KIỂM THỬ BÀI 7 <<<");
    }
}
