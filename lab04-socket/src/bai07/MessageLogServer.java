package bai07;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class MessageLogServer {
    public static final int DEFAULT_PORT = 5007;
    private static final String LOG_DIR = "data/logs";
    private static final Pattern CLIENT_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    // Khóa đồng bộ cho từng file log của từng clientId để đảm bảo thread-safe khi ghi file
    private static final ConcurrentHashMap<String, Object> fileLocks = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        ExecutorService pool = Executors.newFixedThreadPool(20);

        // Tạo thư mục log nếu chưa tồn tại
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Message Log Server listening on port " + port);
            System.out.println("Thư mục lưu nhật ký: " + dir.getAbsolutePath());

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
        String remoteAddress = String.valueOf(socket.getRemoteSocketAddress());
        System.out.println("Kết nối mới từ: " + remoteAddress);

        String clientId = null;

        try (socket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line;
            while ((line = in.readLine()) != null) {
                // Kiểm tra lệnh kết thúc phiên: QUIT
                if (line.trim().equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }

                // Nếu client chưa gửi HELLO
                if (clientId == null) {
                    if (line.regionMatches(true, 0, "HELLO ", 0, 6)) {
                        String idCandidate = line.substring(6).trim();
                        if (CLIENT_ID_PATTERN.matcher(idCandidate).matches()) {
                            clientId = idCandidate;
                            out.println("OK HELLO " + clientId);
                            System.out.println("Client " + remoteAddress + " xác thực thành công với clientId: " + clientId);
                        } else {
                            out.println("ERR INVALID_CLIENT_ID ClientId chỉ được chứa chữ, số, gạch ngang và gạch dưới");
                        }
                    } else {
                        out.println("ERR NOT_AUTHENTICATED Phải gửi HELLO clientId trước khi gửi nội dung");
                    }
                    continue;
                }

                // Khi đã xác thực, bất kể chuỗi là "HET" hay chứa chữ "HET", đều ghi nhận là nội dung tin nhắn
                saveLog(clientId, remoteAddress, line);
                out.println("OK SAVED");
            }
        } catch (IOException e) {
            System.err.println("Lỗi phiên làm việc với " + remoteAddress + ": " + e.getMessage());
        } finally {
            System.out.println("Client ngắt kết nối: " + remoteAddress + (clientId != null ? " (" + clientId + ")" : ""));
        }
    }

    private static void saveLog(String clientId, String remoteAddress, String content) {
        Object lock = fileLocks.computeIfAbsent(clientId, k -> new Object());
        synchronized (lock) {
            File logFile = new File(LOG_DIR, clientId + ".txt");
            String timestamp = LocalDateTime.now().format(ISO_FORMATTER);
            String logEntry = String.format("[%s] [%s] %s%n", timestamp, remoteAddress, content);

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(logFile, true), StandardCharsets.UTF_8))) {
                writer.write(logEntry);
                writer.flush();
            } catch (IOException e) {
                System.err.println("Lỗi ghi file log cho " + clientId + ": " + e.getMessage());
            }
        }
    }
}
