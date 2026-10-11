package bai06;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpUdpBenchmarkServer {
    public static final int TCP_PORT = 5060;
    public static final int UDP_PORT = 5061;

    public static void main(String[] args) {
        System.out.println("=== KHỞI ĐỘNG BENCHMARK SERVER (TCP: " + TCP_PORT + ", UDP: " + UDP_PORT + ") ===");

        // Luồng chạy TCP Server
        Thread tcpThread = new Thread(() -> startTcpServer(TCP_PORT));
        tcpThread.setDaemon(true);
        tcpThread.start();

        // Luồng chạy UDP Server (Chạy ở main thread hoặc thread riêng)
        startUdpServer(UDP_PORT);
    }

    private static void startTcpServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[TCP Server] Đang lắng nghe tại cổng " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> {
                    try (socket;
                         BufferedReader in = new BufferedReader(
                                 new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                         PrintWriter out = new PrintWriter(
                                 new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

                        String line;
                        while ((line = in.readLine()) != null) {
                            if (line.equalsIgnoreCase("QUIT")) {
                                out.println("OK BYE");
                                break;
                            }
                            // Echo lại với tiền tố ACK:
                            out.println("ACK " + line);
                        }
                    } catch (IOException e) {
                        // Phiên kết thúc
                    }
                }).start();
            }
        } catch (IOException e) {
            System.err.println("[TCP Server] Lỗi: " + e.getMessage());
        }
    }

    private static void startUdpServer(int port) {
        byte[] buffer = new byte[4096];
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("[UDP Server] Đang lắng nghe tại cổng " + port);
            while (true) {
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                String msg = new String(requestPacket.getData(), requestPacket.getOffset(), requestPacket.getLength(), StandardCharsets.UTF_8);
                String ack = "ACK " + msg;
                byte[] ackBytes = ack.getBytes(StandardCharsets.UTF_8);

                DatagramPacket responsePacket = new DatagramPacket(ackBytes, ackBytes.length, requestPacket.getAddress(), requestPacket.getPort());
                socket.send(responsePacket);
            }
        } catch (IOException e) {
            System.err.println("[UDP Server] Lỗi: " + e.getMessage());
        }
    }
}
