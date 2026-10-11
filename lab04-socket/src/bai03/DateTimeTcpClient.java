package bai03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DateTimeTcpClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DateTimeTcpServer.DEFAULT_PORT;
        boolean autoTest = args.length > 2 && args[2].equalsIgnoreCase("--test");

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            System.out.println("Đã kết nối TCP tới DateTime Server tại " + host + ":" + port);

            if (autoTest) {
                String[] commands = {"DATE", "TIME", "DATETIME", "UNKNOWN_CMD", "QUIT"};
                for (String cmd : commands) {
                    System.out.println("-> Gửi: " + cmd);
                    out.println(cmd);
                    String resp = in.readLine();
                    System.out.println("<- Nhận: " + resp);
                }
                return;
            }

            System.out.println("Nhập lệnh (DATE, TIME, DATETIME, QUIT):");
            String line;
            while ((line = console.readLine()) != null) {
                out.println(line);
                String resp = in.readLine();
                if (resp == null) {
                    System.out.println("Server đã đóng kết nối (phát hiện kết thúc luồng byte).");
                    break;
                }
                System.out.println("Server: " + resp);
                if (line.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối TCP: " + e.getMessage());
        }
    }
}
