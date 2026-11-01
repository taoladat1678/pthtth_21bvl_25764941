package bai07;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "GeometryService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            GeometryService service = (GeometryService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n===== CHƯƠNG TRÌNH TÍNH DIỆN TÍCH HÌNH HỌC (RMI) =====");
                System.out.println("1. Tính diện tích hình chữ nhật");
                System.out.println("2. Tính diện tích hình tròn");
                System.out.println("3. Tính diện tích hình tam giác");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng: ");

                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        System.out.print("Nhập chiều rộng (width): ");
                        double width = Double.parseDouble(scanner.nextLine().trim());
                        System.out.print("Nhập chiều cao (height): ");
                        double height = Double.parseDouble(scanner.nextLine().trim());
                        double rectArea = service.rectangleArea(width, height);
                        System.out.printf("=> Diện tích hình chữ nhật: %.4f%n", rectArea);
                        break;

                    case "2":
                        System.out.print("Nhập bán kính (radius): ");
                        double radius = Double.parseDouble(scanner.nextLine().trim());
                        double circleArea = service.circleArea(radius);
                        System.out.printf("=> Diện tích hình tròn: %.4f%n", circleArea);
                        break;

                    case "3":
                        System.out.print("Nhập độ dài cạnh đáy (base): ");
                        double base = Double.parseDouble(scanner.nextLine().trim());
                        System.out.print("Nhập chiều cao tương ứng (height): ");
                        double triHeight = Double.parseDouble(scanner.nextLine().trim());
                        double triArea = service.triangleArea(base, triHeight);
                        System.out.printf("=> Diện tích hình tam giác: %.4f%n", triArea);
                        break;

                    case "0":
                        System.out.println("Tạm biệt!");
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

