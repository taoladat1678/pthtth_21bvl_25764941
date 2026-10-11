package bai08;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecureFileTransferServer {
    public static final int DEFAULT_PORT = 5008;
    public static final String UPLOAD_DIR = "uploads";

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        ExecutorService pool = Executors.newFixedThreadPool(10);

        File uploadFolder = new File(UPLOAD_DIR);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Secure File Transfer Server listening on port " + port);
            System.out.println("Thư mục lưu trữ tệp: " + uploadFolder.getAbsolutePath());

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
        String client = String.valueOf(socket.getRemoteSocketAddress());
        System.out.println("Client kết nối: " + client);

        try (socket) {
            InputStream rawIn = socket.getInputStream();
            OutputStream rawOut = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(rawIn, StandardCharsets.UTF_8));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(rawOut, StandardCharsets.UTF_8), true);

            String headerLine;
            while ((headerLine = reader.readLine()) != null) {
                headerLine = headerLine.trim();
                if (headerLine.isEmpty()) continue;
                if (headerLine.equalsIgnoreCase("QUIT")) {
                    writer.println("OK BYE");
                    break;
                }

                // Cú pháp: FILE <filename> <fileSize> <sha256>
                String[] parts = headerLine.split("\\s+");
                if (parts.length != 4 || !parts[0].equalsIgnoreCase("FILE")) {
                    writer.println("ERR INVALID_METADATA Cú pháp yêu cầu: FILE <filename> <fileSize> <sha256>");
                    continue;
                }

                String rawFilename = parts[1];
                long fileSize;
                try {
                    fileSize = Long.parseLong(parts[2]);
                    if (fileSize < 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    writer.println("ERR INVALID_FILE_SIZE Kích thước file phải là số nguyên dương");
                    continue;
                }
                String clientSha256 = parts[3];

                // Kiểm tra an toàn bảo mật đường dẫn (Path Traversal Prevention)
                if (rawFilename.contains("..") || rawFilename.contains("/") || rawFilename.contains("\\")) {
                    System.err.println("CẢNH BÁO: Phát hiện tấn công Path Traversal trong tên tệp: " + rawFilename);
                    writer.println("ERR INVALID_FILENAME Tên file không được chứa ký tự đường dẫn hoặc ..");
                    continue;
                }

                // Lấy tên tệp thuần túy
                Path safePath = Paths.get(rawFilename).getFileName();
                String safeFilename = safePath.toString();
                File destFile = new File(UPLOAD_DIR, safeFilename);

                // Báo cho client sẵn sàng nhận dữ liệu
                writer.println("READY");

                System.out.printf("Bắt đầu nhận file: '%s' (%d bytes) từ %s\n", safeFilename, fileSize, client);

                // Đọc chính xác fileSize byte từ luồng nhị phân
                MessageDigest digest;
                try {
                    digest = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }

                long remaining = fileSize;
                byte[] buffer = new byte[8192];

                try (FileOutputStream fos = new FileOutputStream(destFile)) {
                    while (remaining > 0) {
                        int readLength = (int) Math.min(buffer.length, remaining);
                        int bytesRead = rawIn.read(buffer, 0, readLength);
                        if (bytesRead == -1) {
                            throw new IOException("Đứt kết nối bất ngờ trước khi nhận đủ " + fileSize + " bytes (còn thiếu " + remaining + " bytes)");
                        }
                        fos.write(buffer, 0, bytesRead);
                        digest.update(buffer, 0, bytesRead);
                        remaining -= bytesRead;
                    }
                    fos.flush();
                }

                // Tính toán mã SHA-256 sau khi ghi xong
                byte[] hashBytes = digest.digest();
                String serverSha256 = bytesToHex(hashBytes);

                if (serverSha256.equalsIgnoreCase(clientSha256)) {
                    System.out.println("Tệp '" + safeFilename + "' khớp mã SHA-256: " + serverSha256);
                    writer.println("OK");
                } else {
                    System.err.println("Mã SHA-256 không khớp! Client: " + clientSha256 + ", Server: " + serverSha256);
                    destFile.delete(); // Xóa file hỏng
                    writer.println("ERR HASH_MISMATCH");
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi phiên truyền file với " + client + ": " + e.getMessage());
        } finally {
            System.out.println("Kết thúc phiên làm việc với: " + client);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
