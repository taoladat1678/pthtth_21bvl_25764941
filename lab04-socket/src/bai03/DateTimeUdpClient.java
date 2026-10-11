package bai03;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class DateTimeUdpClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DateTimeUdpServer.DEFAULT_PORT;
        boolean autoTest = args.length > 2 && args[2].equalsIgnoreCase("--test");

        try (DatagramSocket socket = new DatagramSocket();
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            socket.setSoTimeout(3000); // 3 giây timeout
            InetAddress serverAddress = InetAddress.getByName(host);

            System.out.println("Đang kết nối tới DateTime UDP Server tại " + host + ":" + port);

            if (autoTest) {
                String[] commands = {"DATE", "TIME", "DATETIME", "UNKNOWN_CMD"};
                for (String cmd : commands) {
                    System.out.println("-> Gửi UDP: " + cmd);
                    sendAndReceive(socket, serverAddress, port, cmd);
                }
                return;
            }

            System.out.println("Nhập lệnh (DATE, TIME, DATETIME, hoặc exit để thoát):");
            String line;
            while ((line = console.readLine()) != null) {
                if (line.trim().equalsIgnoreCase("exit")) {
                    break;
                }
                sendAndReceive(socket, serverAddress, port, line.trim());
            }
        } catch (Exception e) {
            System.err.println("Lỗi UDP Client: " + e.getMessage());
        }
    }

    private static void sendAndReceive(DatagramSocket socket, InetAddress serverAddress, int port, String cmd) {
        try {
            byte[] sendData = cmd.getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
            socket.send(sendPacket);

            byte[] buffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(),
                    receivePacket.getOffset(), receivePacket.getLength(),
                    StandardCharsets.UTF_8);
            System.out.println("<- Nhận từ server: " + response);
        } catch (SocketTimeoutException e) {
            System.err.println("<- Hết 3 giây nhưng chưa nhận được phản hồi (Server có thể đã tắt hoặc bị mất gói tin).");
        } catch (Exception e) {
            System.err.println("<- Lỗi truyền nhận: " + e.getMessage());
        }
    }
}
