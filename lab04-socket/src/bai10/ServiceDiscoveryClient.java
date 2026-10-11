package bai10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServiceDiscoveryClient {
    public static final int DISCOVERY_PORT = ServiceDiscoveryServer.UDP_DISCOVERY_PORT;
    public static final int TIMEOUT_MS = 2000;

    public static class DiscoveredService {
        public final String serviceName;
        public final String hostIp;
        public final int tcpPort;
        public final String version;

        public DiscoveredService(String serviceName, String hostIp, int tcpPort, String version) {
            this.serviceName = serviceName;
            this.hostIp = hostIp;
            this.tcpPort = tcpPort;
            this.version = version;
        }

        @Override
        public String toString() {
            return String.format("%s (Phiên bản: %s) tại %s:%d", serviceName, version, hostIp, tcpPort);
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("BÀI 10: KHÁM PHÁ DỊCH VỤ (UDP DISCOVERY KẾT HỢP DỊCH VỤ TCP)");
        System.out.println("========================================================================");
        System.out.println("Giai đoạn 1: Gửi gói tin broadcast DISCOVER_SERVICE qua UDP port " + DISCOVERY_PORT);
        System.out.println("Thời gian chờ phản hồi (timeout): " + TIMEOUT_MS + " ms...");

        List<DiscoveredService> services = discoverServices();

        if (services.isEmpty()) {
            System.out.println("Không tìm thấy dịch vụ nào đang hoạt động trên mạng.");
            return;
        }

        System.out.println("\n>>> DANH SÁCH DỊCH VỤ ĐÃ KHÁM PHÁ (ĐÃ LỌC BỎ TRÙNG LẶP) <<<");
        for (int i = 0; i < services.size(); i++) {
            System.out.printf("[%d] %s\n", (i + 1), services.get(i));
        }

        // Giai đoạn 2: Kết nối tới dịch vụ đầu tiên tìm thấy qua TCP
        DiscoveredService target = services.get(0);
        System.out.printf("\n>>> Giai đoạn 2: Kết nối TCP tới %s tại %s:%d <<<\n",
                target.serviceName, target.hostIp, target.tcpPort);

        testConnectTcp(target);
    }

    public static List<DiscoveredService> discoverServices() {
        Map<String, DiscoveredService> uniqueServices = new LinkedHashMap<>();
        byte[] requestData = "DISCOVER_SERVICE".getBytes(StandardCharsets.UTF_8);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.setSoTimeout(TIMEOUT_MS);

            // Gửi tới cả địa chỉ broadcast mạng cục bộ lẫn localhost
            try {
                DatagramPacket broadcastPacket = new DatagramPacket(
                        requestData, requestData.length,
                        InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT);
                socket.send(broadcastPacket);
            } catch (Exception ignored) {}

            try {
                DatagramPacket loopbackPacket = new DatagramPacket(
                        requestData, requestData.length,
                        InetAddress.getByName("127.0.0.1"), DISCOVERY_PORT);
                socket.send(loopbackPacket);
            } catch (Exception ignored) {}

            System.out.println("Đã phát tán yêu cầu tìm kiếm dịch vụ, đang đợi các server phản hồi...");

            byte[] buffer = new byte[2048];
            while (true) {
                try {
                    DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(responsePacket);

                    String response = new String(responsePacket.getData(),
                            responsePacket.getOffset(), responsePacket.getLength(),
                            StandardCharsets.UTF_8).trim();

                    // Sử dụng địa chỉ nguồn từ datagram phản hồi
                    String sourceIp = responsePacket.getAddress().getHostAddress();

                    // Phân tích định dạng: SERVICE tên_dịch_vụ tcp_port phiên_bản
                    String[] tokens = response.split("\\s+");
                    if (tokens.length >= 4 && tokens[0].equalsIgnoreCase("SERVICE")) {
                        String sName = tokens[1];
                        int tPort = Integer.parseInt(tokens[2]);
                        String sVer = tokens[3];

                        // Khóa định danh để loại bỏ phản hồi trùng lặp (IP + tcpPort)
                        String key = sourceIp + ":" + tPort;
                        if (!uniqueServices.containsKey(key)) {
                            uniqueServices.put(key, new DiscoveredService(sName, sourceIp, tPort, sVer));
                            System.out.printf("-> Tìm thấy server mới: %s tại %s:%d\n", sName, sourceIp, tPort);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Hết thời gian chờ khám phá UDP (" + TIMEOUT_MS + " ms).");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi quá trình UDP Discovery: " + e.getMessage());
        }

        return new ArrayList<>(uniqueServices.values());
    }

    public static void testConnectTcp(DiscoveredService s) {
        try (Socket socket = new Socket(s.hostIp, s.tcpPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String banner = in.readLine();
            System.out.println("<- Server Chào mừng: " + banner);

            // Gửi lệnh kiểm tra INFO
            System.out.println("-> Gửi lệnh TCP: INFO");
            out.println("INFO");
            System.out.println("<- Nhận từ TCP: " + in.readLine());

            // Gửi lệnh ECHO
            System.out.println("-> Gửi lệnh TCP: ECHO Xin chao IUH 2026");
            out.println("ECHO Xin chao IUH 2026");
            System.out.println("<- Nhận từ TCP: " + in.readLine());

            // Gửi QUIT
            System.out.println("-> Gửi lệnh TCP: QUIT");
            out.println("QUIT");
            System.out.println("<- Nhận từ TCP: " + in.readLine());

            System.out.println("Kết nối TCP hoàn tất và đóng an toàn.");
        } catch (IOException e) {
            System.err.println("Lỗi khi kết nối TCP tới " + s.hostIp + ":" + s.tcpPort + ": " + e.getMessage());
        }
    }
}
