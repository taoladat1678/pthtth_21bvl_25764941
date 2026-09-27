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
import java.time.format.DateTimeFormatter;

public class DateTimeServer {
    private static final int PORT = 5002;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MM yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH mm ss");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd MM yyyy HH mm ss");

    public static void main(String[] args) {
        // Chạy UDP Server trên một luồng (thread) riêng
        new Thread(() -> startUdpServer()).start();
        
        // Chạy TCP Server trên luồng chính
        startTcpServer();
    }

    private static void startTcpServer() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("TCP Server đang lắng nghe trên port " + PORT);
            while (true) {
                Socket socket = server.accept();
                new Thread(() -> serveTcp(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi TCP Server: " + e.getMessage());
        }
    }

    private static void serveTcp(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String request;
            while ((request = in.readLine()) != null) {
                String reqTrimmed = request.trim();
                if (reqTrimmed.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }
                out.println(processRequest(reqTrimmed));
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối TCP client: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException e) {}
        }
    }

    private static void startUdpServer() {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server đang lắng nghe trên port " + PORT);
            byte[] buf = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String request = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
                
                String responseStr = processRequest(request);
                byte[] responseBytes = responseStr.getBytes(StandardCharsets.UTF_8);
                DatagramPacket responsePacket = new DatagramPacket(
                        responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());
                socket.send(responsePacket);
            }
        } catch (IOException e) {
            System.err.println("Lỗi UDP Server: " + e.getMessage());
        }
    }

    private static String processRequest(String request) {
        LocalDateTime now = LocalDateTime.now();
        if (request.equalsIgnoreCase("DATE")) {
            return now.format(DATE_FORMATTER);
        } else if (request.equalsIgnoreCase("TIME")) {
            return now.format(TIME_FORMATTER);
        } else if (request.equalsIgnoreCase("DATETIME")) {
            return now.format(DATETIME_FORMATTER);
        } else {
            return "ERR UNKNOWN_COMMAND";
        }
    }
}
