package bai06;

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

public class TcpUdpBenchmarkClient {
    private static final int NUM_MESSAGES = 1000;
    private static final int ITERATIONS = 5;
    private static final int MESSAGE_SIZE_BYTES = 256;
    private static final int UDP_TIMEOUT_MS = 500;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int tcpPort = args.length > 1 ? Integer.parseInt(args[1]) : TcpUdpBenchmarkServer.TCP_PORT;
        int udpPort = args.length > 2 ? Integer.parseInt(args[2]) : TcpUdpBenchmarkServer.UDP_PORT;

        System.out.println("========================================================================");
        System.out.println("BÀI 6: THỰC NGHIỆM ĐO VÀ SO SÁNH HIỆU NĂNG TCP VÀ UDP TRÊN CHO 1000 GÓI");
        System.out.println("========================================================================");
        System.out.println("Môi trường:      " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")");
        System.out.println("Phiên bản Java:  " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")");
        System.out.println("Máy chủ mục tiêu:" + host + " (Localhost)");
        System.out.println("Số thông điệp:   " + NUM_MESSAGES + " gói / lần chạy");
        System.out.println("Số lần lặp lại:  " + ITERATIONS + " lần (theo yêu cầu ít nhất 5 lần)");
        System.out.println("Kích thước gói:  " + MESSAGE_SIZE_BYTES + " bytes");
        System.out.println("UDP Timeout:     " + UDP_TIMEOUT_MS + " ms");
        System.out.println("Phương pháp đo:  System.nanoTime() độ phân giải nano giây");
        System.out.println("========================================================================\n");

        // Tạo dữ liệu payload cố định đúng kích thước
        String payload = createFixedPayload(MESSAGE_SIZE_BYTES);

        // 1. Benchmark TCP
        System.out.println(">>> 1. TIẾN HÀNH THỬ NGHIỆM VỚI TCP <<<");
        double[] tcpTimes = new double[ITERATIONS];
        int[] tcpReceived = new int[ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) {
            System.out.print("Lần chạy " + (i + 1) + "/" + ITERATIONS + " (TCP)... ");
            BenchmarkResult res = runTcpTest(host, tcpPort, payload);
            tcpTimes[i] = res.durationMs;
            tcpReceived[i] = res.receivedCount;
            System.out.printf("Hoàn thành: %.2f ms | Nhận: %d/%d (Mất: %.2f%%) | Tốc độ: %.0f gói/s\n",
                    res.durationMs, res.receivedCount, NUM_MESSAGES, res.lossPercent(), res.throughput);
        }

        // Nghỉ một chút giữa 2 pha kiểm thử
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // 2. Benchmark UDP
        System.out.println("\n>>> 2. TIẾN HÀNH THỬ NGHIỆM VỚI UDP <<<");
        double[] udpTimes = new double[ITERATIONS];
        int[] udpReceived = new int[ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) {
            System.out.print("Lần chạy " + (i + 1) + "/" + ITERATIONS + " (UDP)... ");
            BenchmarkResult res = runUdpTest(host, udpPort, payload);
            udpTimes[i] = res.durationMs;
            udpReceived[i] = res.receivedCount;
            System.out.printf("Hoàn thành: %.2f ms | Nhận: %d/%d (Mất: %.2f%%) | Tốc độ: %.0f gói/s\n",
                    res.durationMs, res.receivedCount, NUM_MESSAGES, res.lossPercent(), res.throughput);
        }

        // 3. Tổng hợp kết quả và phân tích
        printSummary(tcpTimes, tcpReceived, udpTimes, udpReceived);
    }

    private static String createFixedPayload(int size) {
        StringBuilder sb = new StringBuilder();
        String base = "BENCHMARK_PAYLOAD_TEST_DATA_";
        while (sb.length() < size) {
            sb.append(base);
        }
        return sb.substring(0, size);
    }

    private static BenchmarkResult runTcpTest(String host, int port, String payload) {
        int received = 0;
        long startTime = System.nanoTime();
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            for (int i = 0; i < NUM_MESSAGES; i++) {
                out.println(payload);
                String resp = in.readLine();
                if (resp != null) {
                    received++;
                }
            }
            out.println("QUIT");
        } catch (IOException e) {
            System.err.println("TCP Test Error: " + e.getMessage());
        }
        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        double throughput = (received / (durationMs / 1000.0));
        return new BenchmarkResult(durationMs, received, throughput);
    }

    private static BenchmarkResult runUdpTest(String host, int port, String payload) {
        int received = 0;
        byte[] sendData = payload.getBytes(StandardCharsets.UTF_8);
        byte[] buffer = new byte[4096];

        long startTime = System.nanoTime();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(UDP_TIMEOUT_MS);
            InetAddress address = InetAddress.getByName(host);

            for (int i = 0; i < NUM_MESSAGES; i++) {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                socket.send(sendPacket);

                DatagramPacket recvPacket = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(recvPacket);
                    received++;
                } catch (SocketTimeoutException e) {
                    // Mất gói hoặc timeout
                }
            }
        } catch (Exception e) {
            System.err.println("UDP Test Error: " + e.getMessage());
        }
        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        double throughput = (received / (durationMs / 1000.0));
        return new BenchmarkResult(durationMs, received, throughput);
    }

    private static void printSummary(double[] tcpTimes, int[] tcpRecv, double[] udpTimes, int[] udpRecv) {
        System.out.println("\n========================================================================");
        System.out.println("BẢNG TỔNG HỢP SO SÁNH 5 LẦN CHẠY (1000 GÓI TIN 256 BYTES)");
        System.out.println("========================================================================");
        System.out.printf("%-10s | %-16s | %-16s | %-16s | %-16s\n",
                "Lần chạy", "TCP Thời gian", "TCP Gói nhận", "UDP Thời gian", "UDP Gói nhận");
        System.out.println("-----------+------------------+------------------+------------------+-----------------");

        double tcpSum = 0;
        double udpSum = 0;
        int tcpRecvSum = 0;
        int udpRecvSum = 0;

        for (int i = 0; i < ITERATIONS; i++) {
            tcpSum += tcpTimes[i];
            udpSum += udpTimes[i];
            tcpRecvSum += tcpRecv[i];
            udpRecvSum += udpRecv[i];
            System.out.printf("Lần %-6d | %10.2f ms   | %10d/%d    | %10.2f ms   | %10d/%d\n",
                    (i + 1), tcpTimes[i], tcpRecv[i], NUM_MESSAGES, udpTimes[i], udpRecv[i], NUM_MESSAGES);
        }
        System.out.println("-----------+------------------+------------------+------------------+-----------------");
        System.out.printf("Trung bình | %10.2f ms   | %10.1f/%d    | %10.2f ms   | %10.1f/%d\n",
                (tcpSum / ITERATIONS), ((double) tcpRecvSum / ITERATIONS), NUM_MESSAGES,
                (udpSum / ITERATIONS), ((double) udpRecvSum / ITERATIONS), NUM_MESSAGES);
        System.out.println("========================================================================");
        System.out.println("\nNHẬN XÉT VÀ KẾT LUẬN:");
        System.out.println("1. Về độ tin cậy: TCP luôn đảm bảo 100% gói tin đến nơi đầy đủ và có thứ tự nhờ cơ chế bắt tay 3 bước và ACK/Truyền lại.");
        System.out.println("   Trong khi đó, UDP không có cơ chế xác nhận gói tin tầng giao vận. Trên localhost ít mất gói, nhưng trên mạng thực tế có thể xảy ra mất hoặc đảo thứ tự gói.");
        System.out.println("2. Về độ trễ và thời gian thực thi: Do UDP không cần quản lý trạng thái kết nối và không cần chờ ACK tầng giao vận cho mỗi byte, nên trong nhiều kịch bản UDP có thể nhanh hơn. Tuy nhiên không thể vội kết luận UDP luôn nhanh hơn trong mọi tình huống (ví dụ khi có cơ chế batching / windowing của TCP tối ưu luồng dữ liệu).");
        System.out.println("3. Kết luận lựa chọn: Dùng TCP khi cần sự chính xác tuyệt đối (giao dịch, file, chat, lệnh điều khiển). Dùng UDP khi cần độ trễ thấp tối đa và chấp nhận mất gói (voice/video streaming, DNS, khám phá dịch vụ, game online).");
        System.out.println("========================================================================\n");
    }

    private static class BenchmarkResult {
        final double durationMs;
        final int receivedCount;
        final double throughput;

        public BenchmarkResult(double durationMs, int receivedCount, double throughput) {
            this.durationMs = durationMs;
            this.receivedCount = receivedCount;
            this.throughput = throughput;
        }

        public double lossPercent() {
            return ((NUM_MESSAGES - receivedCount) * 100.0) / NUM_MESSAGES;
        }
    }
}
