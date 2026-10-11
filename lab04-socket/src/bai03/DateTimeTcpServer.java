package bai03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DateTimeTcpServer {
    public static final int DEFAULT_PORT = 5003;

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        ExecutorService pool = Executors.newFixedThreadPool(20);

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("DateTime TCP Server listening on port " + port);

            while (true) {
                Socket socket = server.accept();
                pool.submit(() -> handleClient(socket));
            }
        } catch (IOException e) {
            System.err.println("TCP Server error: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handleClient(Socket socket) {
        String client = String.valueOf(socket.getRemoteSocketAddress());
        System.out.println("TCP Client connected: " + client);

        try (socket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                String response = processCommand(request);
                out.println(response);
                if (request.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi phiên TCP client " + client + ": " + e.getMessage());
        } finally {
            System.out.println("TCP Client disconnected: " + client);
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
            case "QUIT":
                return "OK BYE";
            default:
                return "ERR UNKNOWN_COMMAND";
        }
    }
}
