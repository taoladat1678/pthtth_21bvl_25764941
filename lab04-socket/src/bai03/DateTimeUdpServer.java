package bai03;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUdpServer {
    public static final int DEFAULT_PORT = 5004;

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        byte[] buffer = new byte[1024];

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("DateTime UDP Server listening on port " + port);

            while (true) {
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                String request = new String(requestPacket.getData(),
                        requestPacket.getOffset(), requestPacket.getLength(),
                        StandardCharsets.UTF_8).trim();

                System.out.println("UDP nhận từ " + requestPacket.getSocketAddress() + ": [" + request + "]");

                String response = processCommand(request);
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

                DatagramPacket responsePacket = new DatagramPacket(
                        responseBytes, responseBytes.length,
                        requestPacket.getAddress(), requestPacket.getPort());
                socket.send(responsePacket);
            }
        } catch (IOException e) {
            System.err.println("Lỗi UDP Server: " + e.getMessage());
        }
    }

    public static String processCommand(String request) {
        if (request == null) return "ERR UNKNOWN_COMMAND";
        String cmd = request.trim().toUpperCase();

        switch (cmd) {
            case "DATE":
                return "OK " + LocalDate.now().format(DATE_FORMATTER);
            case "TIME":
                return "OK " + LocalTime.now().format(TIME_FORMATTER);
            case "DATETIME":
                return "OK " + LocalDateTime.now().format(DATETIME_FORMATTER);
            default:
                return "ERR UNKNOWN_COMMAND";
        }
    }
}
