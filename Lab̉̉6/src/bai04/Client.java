package bai04;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "ContactService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            ContactService service = (ContactService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n===== HỆ THỐNG QUẢN LÝ DANH BẠ (RMI) =====");
                System.out.println("1. Thêm / Cập nhật liên hệ");
                System.out.println("2. Tìm kiếm liên hệ");
                System.out.println("3. Xóa liên hệ");
                System.out.println("4. Xem toàn bộ danh bạ");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng: ");

                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        System.out.print("Nhập tên: ");
                        String name = scanner.nextLine().trim();
                        System.out.print("Nhập số điện thoại: ");
                        String phone = scanner.nextLine().trim();
                        service.addContact(name, phone);
                        System.out.println("=> Đã lưu liên hệ thành công!");
                        break;

                    case "2":
                        System.out.print("Nhập tên cần tìm: ");
                        String searchName = scanner.nextLine().trim();
                        String foundPhone = service.findContact(searchName);
                        if (foundPhone != null) {
                            System.out.println("=> Tìm thấy: " + searchName + " - SĐT: " + foundPhone);
                        } else {
                            System.out.println("=> Không tìm thấy liên hệ nào có tên: " + searchName);
                        }
                        break;

                    case "3":
                        System.out.print("Nhập tên cần xóa: ");
                        String delName = scanner.nextLine().trim();
                        boolean deleted = service.deleteContact(delName);
                        if (deleted) {
                            System.out.println("=> Đã xóa liên hệ " + delName + " thành công!");
                        } else {
                            System.out.println("=> Không tìm thấy liên hệ " + delName + " để xóa.");
                        }
                        break;

                    case "4":
                        Map<String, String> contacts = service.getAllContacts();
                        System.out.println("--- DANH SÁCH LIÊN HỆ HIỆN TẠI (" + contacts.size() + ") ---");
                        if (contacts.isEmpty()) {
                            System.out.println("(Danh bạ trống)");
                        } else {
                            for (Map.Entry<String, String> entry : contacts.entrySet()) {
                                System.out.println(" • " + entry.getKey() + ": " + entry.getValue());
                            }
                        }
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

