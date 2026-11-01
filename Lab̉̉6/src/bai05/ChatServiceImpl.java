package bai05;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServiceImpl extends UnicastRemoteObject implements ChatService {

    private final List<ChatCallback> clients;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ChatServiceImpl() throws RemoteException {
        super();
        this.clients = new CopyOnWriteArrayList<>();
    }

    @Override
    public synchronized void registerClient(ChatCallback clientCallback) throws RemoteException {
        if (!clients.contains(clientCallback)) {
            clients.add(clientCallback);
            System.out.println("[Server] Một client mới đã kết nối chat.");
        }
    }

    @Override
    public synchronized void unregisterClient(ChatCallback clientCallback) throws RemoteException {
        clients.remove(clientCallback);
        System.out.println("[Server] Một client đã ngắt kết nối chat.");
    }

    @Override
    public synchronized void sendMessage(String message) throws RemoteException {
        String time = LocalTime.now().format(TIME_FORMATTER);
        System.out.println("[" + time + "] [Client]: " + message);

        // Tạo phản hồi từ server
        String reply = "Server đã nhận '" + message + "' thành công lúc " + time;

        // Gửi phản hồi qua callback cho tất cả client đã kết nối
        for (ChatCallback client : clients) {
            try {
                client.receiveMessage("Server", reply);
            } catch (RemoteException e) {
                System.err.println("[Server] Không thể gửi phản hồi tới client, hủy đăng ký: " + e.getMessage());
                clients.remove(client);
            }
        }
    }

    @Override
    public synchronized String sendMessageWithReply(String message) throws RemoteException {
        String time = LocalTime.now().format(TIME_FORMATTER);
        System.out.println("[" + time + "] [Client (Sync)]: " + message);
        return "Server phản hồi: Đã nhận tin nhắn \"" + message + "\" (" + time + ")";
    }
}

