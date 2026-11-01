package bai05;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "ChatService";

    public static void main(String[] args) {
        try {
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORT);
                System.out.println("RMI Registry được tạo mới trên cổng " + PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(PORT);
                System.out.println("RMI Registry đã tồn tại trên cổng " + PORT);
            }

            ChatService service = new ChatServiceImpl();
            registry.rebind(SERVICE_NAME, service);

            System.out.println("=========================================");
            System.out.println("[Bài 5] Server Chat 1-1 đã khởi động!");
            System.out.println("Dịch vụ đăng ký: " + SERVICE_NAME);
            System.out.println("Đang chờ client gửi tin nhắn...");
            System.out.println("=========================================");
        } catch (Exception e) {
            System.err.println("Lỗi khởi động Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

