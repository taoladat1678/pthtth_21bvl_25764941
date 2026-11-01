package bai08;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "FlightService";

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

            FlightService service = new FlightServiceImpl();
            registry.rebind(SERVICE_NAME, service);

            System.out.println("=========================================");
            System.out.println("[Bài 8] Server Đặt vé máy bay đã khởi động!");
            System.out.println("Dịch vụ đăng ký: " + SERVICE_NAME);
            System.out.println("Đang chờ client gửi yêu cầu...");
            System.out.println("=========================================");
        } catch (Exception e) {
            System.err.println("Lỗi khởi động Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

