package bai02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DigitToWordClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DigitToWordServer.DEFAULT_PORT;

        boolean autoTest = args.length > 2 && args[2].equalsIgnoreCase("--test");

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            System.out.println("Đã kết nối tới DigitToWord Server tại " + host + ":" + port);

            if (autoTest) {
                runBoundaryTests(out, in);
                return;
            }

            System.out.println("Nhập một chữ số (0-9) để xem cách đọc tiếng Việt, hoặc QUIT để thoát:");
            String input;
            while ((input = console.readLine()) != null) {
                out.println(input);
                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server đã đóng kết nối.");
                    break;
                }
                System.out.println("Server trả về: " + response);
                if (input.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối tới server: " + e.getMessage());
        }
    }

    private static void runBoundaryTests(PrintWriter out, BufferedReader in) throws IOException {
        System.out.println(">>> BẮT ĐẦU CHẠY CÁC CA KIỂM THỬ BIÊN BẮT BUỘC <<<");
        String[] testCases = {
                "0",         // Ca biên: số 0 hợp lệ
                "9",         // Ca biên: số 9 hợp lệ
                "",          // Ca biên: chuỗi rỗng
                "10",        // Ca biên: 2 chữ số
                "a",         // Ca biên: ký tự chữ
                " 5 ",       // Ca biên: có khoảng trắng xung quanh
                "   ",       // Ca biên: chỉ toàn khoảng trắng
                "3",         // Hợp lệ: số 3
                "QUIT"       // Thoát
        };

        for (String test : testCases) {
            System.out.println("-> Gửi: [" + test + "]");
            out.println(test);
            String resp = in.readLine();
            System.out.println("<- Nhận: " + resp);
        }
        System.out.println(">>> HOÀN TẤT KIỂM THỬ BIÊN <<<");
    }
}
