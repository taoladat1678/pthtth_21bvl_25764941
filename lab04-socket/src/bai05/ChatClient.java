package bai05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : ChatServer.DEFAULT_PORT;
        String autoNick = args.length > 2 ? args[2] : null;

        try {
            Socket socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader console = new BufferedReader(
                    new InputStreamReader(System.in, StandardCharsets.UTF_8));

            System.out.println("Kết nối thành công tới Chat Server " + host + ":" + port);

            // Luồng phụ nhận tin nhắn thời gian thực từ server
            Thread readerThread = new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println("<- " + serverMsg);
                    }
                } catch (IOException e) {
                    System.out.println("Đã ngắt luồng nhận (Server đóng kết nối hoặc socket đóng).");
                }
            });
            readerThread.setDaemon(true);
            readerThread.start();

            // Nếu truyền tham số tự động đặt nick
            if (autoNick != null && !autoNick.isEmpty()) {
                System.out.println("Tự động gửi: NICK " + autoNick);
                out.println("NICK " + autoNick);
            }

            System.out.println("Nhập các lệnh chat: NICK <tên>, MSG <tin nhắn>, USERS, QUIT");
            String line;
            while ((line = console.readLine()) != null) {
                out.println(line);
                if (line.trim().equalsIgnoreCase("QUIT")) {
                    Thread.sleep(300); // Đợi server xử lý và đóng socket
                    break;
                }
            }

            socket.close();
            System.out.println("Đã kết thúc phiên chat.");
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
        }
    }
}
