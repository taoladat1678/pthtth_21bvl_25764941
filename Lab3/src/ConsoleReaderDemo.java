import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ConsoleReaderDemo {
    public static void main(String[] args) {
        // 1. Ép System.out xuất ra màn hình bằng UTF-8
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        // 2. Đọc vào bằng UTF-8
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        int count = 0;
        System.out.println("Nhập văn bản (nhấn 'q' để thoát): ");
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null || line.equalsIgnoreCase("q")) {
                    break;
                }
                count++;
                System.out.println();
                System.out.printf("Dòng %d: %s%n", count, line);
            }
        } catch (IOException e) {
            System.out.println("Không thể đọc: " + e.getMessage());
        }
        System.out.println("Tổng số dòng đã nhập: " + count);
    }
}
