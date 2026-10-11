package bai04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoteCalculatorServer {
    public static final int DEFAULT_PORT = 5005;

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        ExecutorService pool = Executors.newFixedThreadPool(20);

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Remote Calculator Server listening on port " + port);

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
        System.out.println("Client connected: " + client);

        try (socket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                if (request.trim().equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }

                String response = calculate(request);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            System.out.println("Client disconnected: " + client);
        }
    }

    public static String calculate(String request) {
        if (request == null) {
            return "ERR INVALID_FORMAT";
        }

        String trimmed = request.trim();
        if (trimmed.isEmpty()) {
            return "ERR INVALID_FORMAT";
        }

        // Tách theo một hoặc nhiều khoảng trắng
        String[] tokens = trimmed.split("\\s+");
        if (tokens.length != 4 || !tokens[0].equalsIgnoreCase("CALC")) {
            return "ERR INVALID_FORMAT";
        }

        String op = tokens[1];
        String num1Str = tokens[2];
        String num2Str = tokens[3];

        // Kiểm tra toán tử hỗ trợ
        if (!op.equals("+") && !op.equals("-") && !op.equals("*") && !op.equals("/")) {
            return "ERR UNSUPPORTED_OPERATOR";
        }

        // Phân tích toán hạng
        double num1;
        double num2;
        try {
            num1 = Double.parseDouble(num1Str);
            num2 = Double.parseDouble(num2Str);
        } catch (NumberFormatException e) {
            return "ERR INVALID_NUMBER";
        }

        // Phép chia cho 0
        if (op.equals("/") && num2 == 0.0) {
            return "ERR DIVIDE_BY_ZERO";
        }

        double result;
        switch (op) {
            case "+":
                result = num1 + num2;
                break;
            case "-":
                result = num1 - num2;
                break;
            case "*":
                result = num1 * num2;
                break;
            case "/":
                result = num1 / num2;
                break;
            default:
                return "ERR UNSUPPORTED_OPERATOR";
        }

        // Định dạng kết quả: nếu là số nguyên thì in dạng số nguyên (ví dụ: 300 thay vì 300.0)
        String formattedResult;
        if (result == Math.floor(result) && !Double.isInfinite(result) && Math.abs(result) < Long.MAX_VALUE) {
            formattedResult = String.valueOf((long) result);
        } else {
            formattedResult = String.valueOf(result);
        }

        return "OK " + formattedResult;
    }
}
