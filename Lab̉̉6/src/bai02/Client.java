package bai02;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "CalculatorService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            CalculatorService service = (CalculatorService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);
            System.out.println("=== CHƯƠNG TRÌNH TÍNH TỔNG HAI SỐ (RMI) ===");
            System.out.print("Nhập số thứ nhất (a): ");
            int a = scanner.nextInt();
            System.out.print("Nhập số thứ hai (b): ");
            int b = scanner.nextInt();

            int sum = service.add(a, b);
            System.out.println("=========================================");
            System.out.println("Kết quả từ server: " + a + " + " + b + " = " + sum);
            System.out.println("=========================================");
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

