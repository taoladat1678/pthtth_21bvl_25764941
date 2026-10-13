package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Đã kết nối tới server.");

            // Bắt đầu luồng nhận tin nhắn từ server
            Thread receiveThread = new Thread(new ReceiveThread(socket, in));
            receiveThread.start();

            // Luồng chính xử lý gửi tin nhắn/lệnh từ người dùng
            String userInput;
            while ((userInput = consoleIn.readLine()) != null) {
                out.println(userInput);
                if (userInput.equalsIgnoreCase("/exit")) {
                    break;
                }
            }

            socket.close();
            System.out.println("Đã thoát ứng dụng.");

        } catch (IOException e) {
            System.out.println("Không thể kết nối tới server: " + e.getMessage());
        }
    }
}