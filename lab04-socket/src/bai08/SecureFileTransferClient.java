package bai08;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecureFileTransferClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : SecureFileTransferServer.DEFAULT_PORT;
        boolean autoTest = args.length > 2 && args[2].equalsIgnoreCase("--test");

        try (Socket socket = new Socket(host, port)) {
            InputStream rawIn = socket.getInputStream();
            OutputStream rawOut = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(rawIn, StandardCharsets.UTF_8));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(rawOut, StandardCharsets.UTF_8), true);

            System.out.println("Đã kết nối tới Secure File Transfer Server tại " + host + ":" + port);

            if (autoTest) {
                runSecurityAndFileTests(writer, reader, rawOut);
                return;
            }

            if (args.length > 2) {
                String filePath = args[2];
                sendFile(new File(filePath), writer, reader, rawOut);
            } else {
                System.out.println("Cách dùng: java bai08.SecureFileTransferClient <host> <port> <duong_dan_file>");
                System.out.println("Hoặc chạy kiểm thử tự động: java bai08.SecureFileTransferClient localhost 5008 --test");
            }

            writer.println("QUIT");
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
        }
    }

    public static void sendFile(File file, PrintWriter writer, BufferedReader reader, OutputStream rawOut) throws Exception {
        if (!file.exists()) {
            System.err.println("File không tồn tại: " + file.getAbsolutePath());
            return;
        }

        String filename = file.getName();
        long fileSize = file.length();
        String sha256 = calculateSHA256(file);

        System.out.printf("Đang chuẩn bị gửi '%s' | Kích thước: %d bytes | SHA-256: %s\n", filename, fileSize, sha256);

        // Bước 1: Gửi Metadata
        writer.println("FILE " + filename + " " + fileSize + " " + sha256);

        // Bước 2: Chờ phản hồi READY từ server
        String readyResp = reader.readLine();
        if (readyResp == null || !readyResp.equalsIgnoreCase("READY")) {
            System.err.println("Server từ chối nhận file: " + readyResp);
            return;
        }

        // Bước 3: Truyền đúng fileSize bytes dữ liệu
        byte[] buffer = new byte[8192];
        try (FileInputStream fis = new FileInputStream(file)) {
            long remaining = fileSize;
            while (remaining > 0) {
                int readLength = (int) Math.min(buffer.length, remaining);
                int bytesRead = fis.read(buffer, 0, readLength);
                if (bytesRead == -1) break;
                rawOut.write(buffer, 0, bytesRead);
                remaining -= bytesRead;
            }
            rawOut.flush();
        }

        // Bước 4: Nhận kết quả xác thực từ server
        String result = reader.readLine();
        System.out.println("<- Kết quả từ Server: " + result);
    }

    private static void runSecurityAndFileTests(PrintWriter writer, BufferedReader reader, OutputStream rawOut) throws Exception {
        System.out.println(">>> BẮT ĐẦU KIỂM THỬ TRUYỀN TỆP AN TOÀN (BÀI 8) <<<");

        File testDir = new File("test_temp_files");
        if (!testDir.exists()) testDir.mkdirs();

        // 1. Kiểm thử File rỗng (0 bytes)
        File emptyFile = new File(testDir, "empty_file.txt");
        if (!emptyFile.exists()) emptyFile.createNewFile();
        System.out.println("\n[1] Kiểm thử File rỗng (0 bytes):");
        sendFile(emptyFile, writer, reader, rawOut);

        // 2. Kiểm thử File tiếng Việt UTF-8
        File viTextFile = new File(testDir, "tieng_viet.txt");
        try (FileOutputStream fos = new FileOutputStream(viTextFile)) {
            String content = "Cộng hòa Xã hội Chủ nghĩa Việt Nam\nĐộc lập - Tự do - Hạnh phúc\nKhoa Công nghệ Thông tin - IUH 2026\n";
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        }
        System.out.println("\n[2] Kiểm thử File văn bản tiếng Việt UTF-8:");
        sendFile(viTextFile, writer, reader, rawOut);

        // 3. Kiểm thử File ảnh nhị phân (.png)
        File imageFile = new File(testDir, "test_image.png");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            // PNG signature: 89 50 4E 47 0D 0A 1A 0A + dummy binary bytes
            byte[] pngHeader = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            fos.write(pngHeader);
            byte[] dummyData = new byte[1024];
            for (int i = 0; i < dummyData.length; i++) dummyData[i] = (byte) (i % 256);
            fos.write(dummyData);
        }
        System.out.println("\n[3] Kiểm thử File ảnh nhị phân (PNG):");
        sendFile(imageFile, writer, reader, rawOut);

        // 4. Kiểm thử Tên file chứa chuỗi Path Traversal (../)
        System.out.println("\n[4] Kiểm thử bảo mật Path Traversal (tên chứa ../):");
        writer.println("FILE ../hack_system.txt 100 0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");
        String traversalResp = reader.readLine();
        System.out.println("<- Phản hồi từ server: " + traversalResp);

        System.out.println("\n>>> HOÀN TẤT BỘ KIỂM THỬ BÀI 8 <<<");
    }

    public static String calculateSHA256(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, n);
            }
        }
        return SecureFileTransferServer.bytesToHex(digest.digest());
    }
}
