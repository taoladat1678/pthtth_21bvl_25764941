package bai05;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Client implements ChatCallback {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "ChatService";

    @Override
    public void receiveMessage(String sender, String message) throws RemoteException {
        System.out.println("\n[" + sender + "]: " + message);
        System.out.print("[Bạn]: ");
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            ChatService service = (ChatService) registry.lookup(SERVICE_NAME);

            Client clientApp = new Client();
            ChatCallback stub = (ChatCallback) UnicastRemoteObject.exportObject(clientApp, 0);

            // Đăng ký callback để nhận phản hồi từ server
            service.registerClient(stub);

            System.out.println("=== ỨNG DỤNG CHAT 1-1 QUA RMI ===");
            System.out.println("Kết nối tới server thành công! Gõ 'exit' để thoát.");
            System.out.println("=================================");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("[Bạn]: ");
                String line = scanner.nextLine().trim();
                if (line.equalsIgnoreCase("exit")) {
                    service.unregisterClient(stub);
                    UnicastRemoteObject.unexportObject(clientApp, true);
                    System.out.println("Đã ngắt kết nối chat. Tạm biệt!");
                    break;
                }
                if (!line.isEmpty()) {
                    // Gọi phương thức void sendMessage(String message) theo đề bài
                    service.sendMessage(line);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

