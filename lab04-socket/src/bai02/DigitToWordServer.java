package bai02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DigitToWordServer {
    public static final int DEFAULT_PORT = 5002;
    private static final String[] DIGIT_WORDS = {
            "không", "một", "hai", "ba", "bốn",
            "năm", "sáu", "bảy", "tám", "chín"
    };

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        ExecutorService pool = Executors.newFixedThreadPool(20);

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("DigitToWord TCP Server listening on port " + port);

            while (true) {
                Socket socket = server.accept();
                pool.submit(() -> handleClient(socket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handleClient(Socket socket) {
        String clientAddress = String.valueOf(socket.getRemoteSocketAddress());
        System.out.println("Client connected: " + clientAddress);

        try (socket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line;
            while ((line = in.readLine()) != null) {
                // Kiểm tra lệnh QUIT
                if (line.trim().equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }

                String response = processDigit(line);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Lỗi phiên làm việc với client " + clientAddress + ": " + e.getMessage());
        } finally {
            System.out.println("Client disconnected: " + clientAddress);
        }
    }

    public static String processDigit(String input) {
        // Yêu cầu: đúng một chữ số từ 0 đến 9.
        // Không trim để bắt chính xác ca biên: dữ liệu có khoảng trắng ví dụ " 5 " hoặc "5 "
        if (input == null || input.length() != 1) {
            return "ERR INVALID_DIGIT";
        }

        char ch = input.charAt(0);
        if (ch >= '0' && ch <= '9') {
            int digit = ch - '0';
            return "OK " + DIGIT_WORDS[digit];
        } else {
            return "ERR INVALID_DIGIT";
        }
    }
}
