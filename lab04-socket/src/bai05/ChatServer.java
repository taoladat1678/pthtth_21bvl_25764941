package bai05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    public static final int DEFAULT_PORT = 5006;
    private static final int MAX_CLIENTS = 50;

    // Quản lý danh sách client đồng thời một cách an toàn đa luồng (thread-safe)
    private static final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Multi-client Chat Server đang lắng nghe tại cổng " + port);

            while (true) {
                Socket socket = server.accept();
                pool.submit(new ClientHandler(socket));
            }
        } catch (IOException e) {
            System.err.println("Lỗi Chat Server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    public static class ClientHandler implements Runnable {
        private final Socket socket;
        private String nickname = null;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String remoteAddr = String.valueOf(socket.getRemoteSocketAddress());
            System.out.println("Kết nối mới từ: " + remoteAddr);

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

                out.println("WELCOME Hãy nhập lệnh: NICK <nickname>");

                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    // Nếu chưa đăng ký Nickname
                    if (nickname == null) {
                        if (line.regionMatches(true, 0, "NICK ", 0, 5)) {
                            String chosenNick = line.substring(5).trim();
                            if (chosenNick.isEmpty() || chosenNick.contains(" ")) {
                                out.println("ERR INVALID_NICK Nickname không được để trống hoặc chứa dấu cách");
                                continue;
                            }

                            // Đăng ký nickname duy nhất
                            ClientHandler existing = clients.putIfAbsent(chosenNick, this);
                            if (existing != null) {
                                out.println("ERR NICK_TAKEN Nickname '" + chosenNick + "' đã được sử dụng. Hãy chọn tên khác");
                            } else {
                                this.nickname = chosenNick;
                                out.println("OK NICK_ACCEPTED " + chosenNick);
                                broadcast("[SYSTEM] " + chosenNick + " đã tham gia phòng chat.", this);
                                System.out.println("Client " + remoteAddr + " đã đăng ký nickname: " + chosenNick);
                            }
                        } else if (line.equalsIgnoreCase("QUIT")) {
                            out.println("OK BYE");
                            break;
                        } else {
                            out.println("ERR MUST_SET_NICK Hãy đặt nickname trước bằng lệnh: NICK <nickname>");
                        }
                        continue;
                    }

                    // Sau khi đã có Nickname
                    if (line.equalsIgnoreCase("QUIT")) {
                        out.println("OK BYE");
                        break;
                    } else if (line.equalsIgnoreCase("USERS")) {
                        String userList = String.join(", ", clients.keySet());
                        out.println("OK USERS " + userList);
                    } else if (line.regionMatches(true, 0, "MSG ", 0, 4)) {
                        String message = line.substring(4).trim();
                        if (!message.isEmpty()) {
                            broadcast("[" + nickname + "]: " + message, this);
                            out.println("OK SENT");
                        } else {
                            out.println("ERR EMPTY_MESSAGE");
                        }
                    } else {
                        out.println("ERR UNKNOWN_COMMAND Lệnh không hợp lệ (hỗ trợ USERS, MSG <nội dung>, QUIT)");
                    }
                }
            } catch (IOException e) {
                System.err.println("Client ngắt kết nối đột ngột: " + (nickname != null ? nickname : remoteAddr) + " (" + e.getMessage() + ")");
            } finally {
                cleanup();
            }
        }

        public void sendMessage(String msg) {
            if (out != null) {
                out.println(msg);
            }
        }

        private void cleanup() {
            if (nickname != null) {
                clients.remove(nickname);
                broadcast("[SYSTEM] " + nickname + " đã rời phòng chat.", null);
                System.out.println("Đã dọn dẹp và xóa nickname: " + nickname);
            }
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    // Broadcast tin nhắn tới tất cả client (ngoại trừ excludeUser nếu có)
    public static void broadcast(String message, ClientHandler excludeUser) {
        for (ClientHandler client : clients.values()) {
            if (client != excludeUser) {
                client.sendMessage(message);
            }
        }
    }
}
