package bai08;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "FlightService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            FlightService service = (FlightService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n===== HỆ THỐNG ĐẶT VÉ MÁY BAY (RMI) =====");
                System.out.println("1. Xem danh sách các chuyến bay & ghế trống");
                System.out.println("2. Tra cứu số ghế trống của một chuyến bay");
                System.out.println("3. Đặt vé máy bay");
                System.out.println("4. Hủy vé máy bay");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng: ");

                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        Map<String, Integer> flights = service.getAllFlights();
                        System.out.println("--- DANH SÁCH CHUYẾN BAY HIỆN TẠI ---");
                        for (Map.Entry<String, Integer> entry : flights.entrySet()) {
                            System.out.printf(" • Chuyến bay %-6s: %d ghế còn trống%n", entry.getKey(), entry.getValue());
                        }
                        break;

                    case "2":
                        System.out.print("Nhập mã số chuyến bay (ví dụ: VN123): ");
                        String flightNo = scanner.nextLine().trim().toUpperCase();
                        int seats = service.availableSeats(flightNo);
                        if (seats >= 0) {
                            System.out.printf("=> Chuyến bay %s hiện còn %d ghế trống.%n", flightNo, seats);
                        } else {
                            System.out.printf("=> Không tìm thấy thông tin chuyến bay %s!%n", flightNo);
                        }
                        break;

                    case "3":
                        System.out.print("Nhập mã chuyến bay muốn đặt: ");
                        String bookFlight = scanner.nextLine().trim().toUpperCase();
                        System.out.print("Nhập số lượng ghế muốn đặt: ");
                        int bookSeats = Integer.parseInt(scanner.nextLine().trim());
                        boolean bookOk = service.bookTicket(bookFlight, bookSeats);
                        if (bookOk) {
                            System.out.printf("=> Đặt vé thành công! Đã giữ %d ghế trên chuyến bay %s.%n", bookSeats, bookFlight);
                        } else {
                            System.out.println("=> Đặt vé thất bại! Số ghế trống không đủ hoặc chuyến bay không tồn tại.");
                        }
                        break;

                    case "4":
                        System.out.print("Nhập mã chuyến bay muốn hủy: ");
                        String cancelFlight = scanner.nextLine().trim().toUpperCase();
                        System.out.print("Nhập số lượng ghế muốn hủy: ");
                        int cancelSeats = Integer.parseInt(scanner.nextLine().trim());
                        boolean cancelOk = service.cancelBooking(cancelFlight, cancelSeats);
                        if (cancelOk) {
                            System.out.printf("=> Hủy vé thành công! Đã hoàn trả %d ghế cho chuyến bay %s.%n", cancelSeats, cancelFlight);
                        } else {
                            System.out.println("=> Hủy vé thất bại! Chuyến bay không tồn tại hoặc số ghế không hợp lệ.");
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

