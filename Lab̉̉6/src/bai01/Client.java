package bai01;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "HelloService";

    public static void main(String[] args) {
        try {
            System.out.println("Đang kết nối đến RMI Server tại " + HOST + ":" + PORT + "...");
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            HelloService service = (HelloService) registry.lookup(SERVICE_NAME);

            System.out.println("Kết nối thành công! Đang gọi hàm sayHello()...");
            String response = service.sayHello();

            System.out.println("=========================================");
            System.out.println("Kết quả nhận được từ Server: " + response);
            System.out.println("=========================================");
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

