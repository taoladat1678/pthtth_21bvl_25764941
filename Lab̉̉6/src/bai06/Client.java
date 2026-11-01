package bai06;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "BankAccountService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            BankAccountService service = (BankAccountService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n===== HỆ THỐNG QUẢN LÝ TÀI KHOẢN NGÂN HÀNG (RMI) =====");
                System.out.println("1. Xem số dư tài khoản");
                System.out.println("2. Nạp tiền vào tài khoản");
                System.out.println("3. Rút tiền từ tài khoản");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng: ");

                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        double balance = service.getBalance();
                        System.out.printf("=> Số dư hiện tại của bạn: %,.2f VND%n", balance);
                        break;

                    case "2":
                        System.out.print("Nhập số tiền muốn nạp (VND): ");
                        double depAmount = Double.parseDouble(scanner.nextLine().trim());
                        if (depAmount <= 0) {
                            System.out.println("=> Số tiền nạp phải lớn hơn 0!");
                        } else {
                            service.deposit(depAmount);
                            System.out.printf("=> Nạp tiền thành công! Số dư mới: %,.2f VND%n", service.getBalance());
                        }
                        break;

                    case "3":
                        System.out.print("Nhập số tiền muốn rút (VND): ");
                        double withAmount = Double.parseDouble(scanner.nextLine().trim());
                        if (withAmount <= 0) {
                            System.out.println("=> Số tiền rút phải lớn hơn 0!");
                        } else {
                            boolean success = service.withdraw(withAmount);
                            if (success) {
                                System.out.printf("=> Rút tiền thành công! Số dư còn lại: %,.2f VND%n", service.getBalance());
                            } else {
                                System.out.println("=> Giao dịch thất bại! Số dư không đủ để rút số tiền này.");
                            }
                        }
                        break;

                    case "0":
                        System.out.println("Cảm ơn bạn đã sử dụng dịch vụ ngân hàng. Tạm biệt!");
                        return;

                    default:
                        System.out.println("Lựa chọn không hợp lệ, vui lòng chọn lại.");
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

