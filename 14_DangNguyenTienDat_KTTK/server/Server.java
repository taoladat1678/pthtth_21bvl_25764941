package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 8888;
    private List<Room> rooms;

    public Server() {
        rooms = new ArrayList<>();
        rooms.add(new Room("Study"));
        rooms.add(new Room("Game"));
        rooms.add(new Room("TraiNghiem"));
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server đang chạy trên port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client mới kết nối: " + socket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(socket, this);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}