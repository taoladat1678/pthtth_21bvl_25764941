package bai04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RemoteCalculatorClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : RemoteCalculatorServer.DEFAULT_PORT;
        boolean autoTest = args.length > 2 && args[2].equalsIgnoreCase("--test");

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            System.out.println("Đã kết nối tới Máy tính từ xa tại " + host + ":" + port);

            if (autoTest) {
                runStandardTests(out, in);
                return;
            }

            System.out.println("Cú pháp: CALC <toán_tử> <số_1> <số_2> (Ví dụ: CALC + 100 200)");
            System.out.println("Nhập QUIT để thoát chương trình.");

            String line;
            while ((line = console.readLine()) != null) {
                out.println(line);
                String resp = in.readLine();
                if (resp == null) {
                    System.out.println("Server đã đóng kết nối.");
                    break;
                }
                System.out.println("Server trả lời: " + resp);
                if (line.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối tới Server: " + e.getMessage());
        }
    }

    private static void runStandardTests(PrintWriter out, BufferedReader in) throws IOException {
        System.out.println(">>> BẮT ĐẦU KIỂM THỬ CÁC TÌNH HUỐNG TRONG ĐỀ BÀI <<<");
        String[] testCases = {
                "CALC + 100 200",   // Kì vọng: OK 300
                "CALC - 50 15",     // Kì vọng: OK 35
                "CALC * 12 10",     // Kì vọng: OK 120
                "CALC / 10 4",      // Kì vọng: OK 2.5
                "CALC / 10 0",      // Kì vọng: ERR DIVIDE_BY_ZERO
                "CALC % 10 3",      // Kì vọng: ERR UNSUPPORTED_OPERATOR
                "CALC + a 2",       // Kì vọng: ERR INVALID_NUMBER
                "CALC + 10",        // Kì vọng: ERR INVALID_FORMAT (thiếu thành phần)
                "HELLO SERVER",     // Kì vọng: ERR INVALID_FORMAT
                "QUIT"              // Kì vọng: OK BYE
        };

        for (String test : testCases) {
            System.out.println("-> Gửi: [" + test + "]");
            out.println(test);
            String response = in.readLine();
            System.out.println("<- Nhận: " + response);
        }
        System.out.println(">>> HOÀN TẤT KIỂM THỬ MÁY TÍNH TỪ XA <<<");
    }
}
