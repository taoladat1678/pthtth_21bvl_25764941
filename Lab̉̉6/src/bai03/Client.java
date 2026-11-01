package bai03;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "PrimeService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            PrimeService service = (PrimeService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);
            System.out.println("=== CHƯƠNG TRÌNH KIỂM TRA SỐ NGUYÊN TỐ (RMI) ===");
            System.out.print("Nhập số nguyên n cần kiểm tra: ");
            int n = scanner.nextInt();

            boolean result = service.isPrime(n);
            System.out.println("=========================================");
            if (result) {
                System.out.println("Kết quả từ server: " + n + " LÀ số nguyên tố.");
            } else {
                System.out.println("Kết quả từ server: " + n + " KHÔNG PHẢI là số nguyên tố.");
            }
            System.out.println("=========================================");
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

