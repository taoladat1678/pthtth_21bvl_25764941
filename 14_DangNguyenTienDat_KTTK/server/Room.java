package server;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name;
    private List<ClientHandler> clients;

    public Room(String name) {
        this.name = name;
        this.clients = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public synchronized void addClient(ClientHandler client) {
        clients.add(client);
        broadcast("Hệ thống: " + client.getClientName() + " đã tham gia phòng.", client);
    }

    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
        broadcast("Hệ thống: " + client.getClientName() + " đã rời phòng.", client);
    }

    public synchronized void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }
}