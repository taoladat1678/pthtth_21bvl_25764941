package bai06;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "BankAccountService";

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

            BankAccountService service = new BankAccountServiceImpl(10_000_000.0);
            registry.rebind(SERVICE_NAME, service);

            System.out.println("=========================================");
            System.out.println("[Bài 6] Server Ngân hàng đã khởi động!");
            System.out.println("Dịch vụ đăng ký: " + SERVICE_NAME);
            System.out.println("Số dư khởi tạo: 10,000,000.00 VND");
            System.out.println("Đang chờ client thực hiện giao dịch...");
            System.out.println("=========================================");
        } catch (Exception e) {
            System.err.println("Lỗi khởi động Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

