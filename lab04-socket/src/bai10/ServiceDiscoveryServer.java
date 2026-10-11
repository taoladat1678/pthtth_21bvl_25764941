package bai10;

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
import java.time.LocalDateTime;

public class ServiceDiscoveryServer {
    public static final int UDP_DISCOVERY_PORT = 5020;
    public static final int DEFAULT_TCP_PORT = 5010;
    public static final String DEFAULT_SERVICE_NAME = "StudentComputeService";
    public static final String DEFAULT_VERSION = "1.0.0";

    private final String serviceName;
    private final int tcpPort;
    private final String version;

    public ServiceDiscoveryServer(String serviceName, int tcpPort, String version) {
        this.serviceName = serviceName;
        this.tcpPort = tcpPort;
        this.version = version;
    }

    public static void main(String[] args) {
        String sName = args.length > 0 ? args[0] : DEFAULT_SERVICE_NAME;
        int tPort = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_TCP_PORT;
        String ver = args.length > 2 ? args[2] : DEFAULT_VERSION;

        ServiceDiscoveryServer app = new ServiceDiscoveryServer(sName, tPort, ver);
        app.start();
    }

    public void start() {
        System.out.println("=================================================================");
        System.out.printf("Khởi động dịch vụ: %s | TCP Port: %d | Version: %s\n", serviceName, tcpPort, version);
        System.out.println("UDP Discovery lắng nghe tại cổng: " + UDP_DISCOVERY_PORT);
        System.out.println("=================================================================");

        // 1. Chạy TCP Service
        Thread tcpThread = new Thread(this::runTcpService);
        tcpThread.setDaemon(true);
        tcpThread.start();

        // 2. Chạy UDP Discovery Responder
        runUdpDiscoveryResponder();
    }

    private void runTcpService() {
        try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {
            System.out.printf("[TCP Server] Đang phục vụ các yêu cầu kết nối tại cổng %d\n", tcpPort);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleTcpClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("[TCP Server] Lỗi: " + e.getMessage());
        }
    }

    private void handleTcpClient(Socket socket) {
        String client = String.valueOf(socket.getRemoteSocketAddress());
        System.out.printf("[TCP Server] Client đã kết nối từ: %s\n", client);

        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            out.printf("OK CONNECTED Service: %s (v%s)\n", serviceName, version);

            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                } else if (line.equalsIgnoreCase("INFO")) {
                    out.printf("OK SERVICE %s %d %s (Time: %s)\n", serviceName, tcpPort, version, LocalDateTime.now());
                } else if (line.regionMatches(true, 0, "ECHO ", 0, 5)) {
                    out.println("OK " + line.substring(5));
                } else {
                    out.println("ERR UNKNOWN_COMMAND (Hỗ trợ INFO, ECHO <text>, QUIT)");
                }
            }
        } catch (IOException e) {
            System.err.println("[TCP Server] Lỗi client " + client + ": " + e.getMessage());
        } finally {
            System.out.printf("[TCP Server] Client đã ngắt kết nối: %s\n", client);
        }
    }

    private void runUdpDiscoveryResponder() {
        byte[] buffer = new byte[2048];
        try (DatagramSocket udpSocket = new DatagramSocket(UDP_DISCOVERY_PORT)) {
            System.out.println("[UDP Discovery] Sẵn sàng phản hồi gói tin DISCOVER_SERVICE...");

            while (true) {
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(requestPacket);

                String msg = new String(requestPacket.getData(), requestPacket.getOffset(), requestPacket.getLength(), StandardCharsets.UTF_8).trim();
                System.out.printf("[UDP Discovery] Nhận được từ %s: [%s]\n", requestPacket.getSocketAddress(), msg);

                if (msg.equalsIgnoreCase("DISCOVER_SERVICE")) {
                    // Cú pháp phản hồi theo yêu cầu: SERVICE tên_dịch_vụ tcp_port phiên_bản
                    String responseText = String.format("SERVICE %s %d %s", serviceName, tcpPort, version);
                    byte[] respData = responseText.getBytes(StandardCharsets.UTF_8);

                    DatagramPacket responsePacket = new DatagramPacket(
                            respData, respData.length,
                            requestPacket.getAddress(), requestPacket.getPort());

                    udpSocket.send(responsePacket);
                    System.out.printf("[UDP Discovery] Đã gửi phản hồi dịch vụ tới %s\n", requestPacket.getSocketAddress());
                }
            }
        } catch (IOException e) {
            System.err.println("[UDP Discovery] Lỗi: " + e.getMessage());
        }
    }
}
