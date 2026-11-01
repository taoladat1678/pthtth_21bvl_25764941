package bai09;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "AuctionService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            AuctionService service = (AuctionService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);
            System.out.println("=================================================");
            System.out.println("   HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN QUA JAVA RMI     ");
            System.out.println("=================================================");
            System.out.print("Vui lòng nhập tên/nickname của bạn: ");
            String username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                username = "User_" + System.currentTimeMillis() % 1000;
            }

            // Khởi tạo Callback để nhận thông báo real-time
            AuctionCallbackImpl callbackObj = new AuctionCallbackImpl(username);
            service.registerClient(callbackObj);

            // Shutdown hook để hủy đăng ký nếu người dùng đóng terminal đột ngột
            final String finalUsername = username;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    service.unregisterClient(callbackObj);
                    UnicastRemoteObject.unexportObject(callbackObj, true);
                } catch (Exception ignored) {
                }
            }));

            System.out.println("Xin chào, " + finalUsername + "! Bạn đã kết nối và nhận thông báo thời gian thực.");

            while (true) {
                System.out.println("\n----------------- MENU CHỨC NĂNG -----------------");
                System.out.println("1. [Chủ sở hữu (Seller)] Đăng sản phẩm mới lên sàn");
                System.out.println("2. [Người đấu giá (Bidder)] Xem danh sách sản phẩm đang đấu giá");
                System.out.println("3. [Người đấu giá (Bidder)] Đưa ra giá thầu (Place Bid)");
                System.out.println("4. Xem chi tiết một sản phẩm");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng: ");

                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        System.out.println("\n--- ĐĂNG SẢN PHẨM MỚI (SELLER) ---");
                        System.out.print("Nhập tên sản phẩm: ");
                        String pName = scanner.nextLine().trim();
                        System.out.print("Nhập giá khởi điểm (VND): ");
                        double startPrice = Double.parseDouble(scanner.nextLine().trim());
                        if (startPrice <= 0) {
                            System.out.println("=> Lỗi: Giá khởi điểm phải lớn hơn 0!");
                            break;
                        }
                        String newId = service.addProduct(pName, startPrice, finalUsername);
                        System.out.printf("=> Đăng sản phẩm thành công! Mã sản phẩm: %s%n", newId);
                        break;

                    case "2":
                        System.out.println("\n--- DANH SÁCH SẢN PHẨM ĐANG ĐẤU GIÁ ---");
                        List<Product> products = service.getProducts();
                        if (products.isEmpty()) {
                            System.out.println("(Hiện chưa có sản phẩm nào trên sàn)");
                        } else {
                            for (Product p : products) {
                                System.out.println(p);
                            }
                        }
                        break;

                    case "3":
                        System.out.println("\n--- ĐẶT GIÁ THẦU (BIDDER) ---");
                        System.out.print("Nhập mã sản phẩm muốn đấu giá (ví dụ: SP01): ");
                        String bidId = scanner.nextLine().trim().toUpperCase();
                        Product target = service.getProduct(bidId);
                        if (target == null) {
                            System.out.println("=> Không tìm thấy sản phẩm có mã: " + bidId);
                            break;
                        }
                        System.out.printf("Sản phẩm: %s | Giá cao nhất hiện tại: %,.2f VND (Người giữ: %s)%n",
                                target.getName(), target.getCurrentHighestBid(), target.getHighestBidder());
                        System.out.print("Nhập mức giá bạn muốn trả (phải lớn hơn giá hiện tại): ");
                        double bidAmount = Double.parseDouble(scanner.nextLine().trim());

                        boolean bidSuccess = service.placeBid(bidId, bidAmount, finalUsername);
                        if (bidSuccess) {
                            System.out.printf("=> Đặt giá thành công! Bạn hiện đang là người trả giá cao nhất cho '%s'.%n",
                                    target.getName());
                        } else {
                            System.out.println("=> Đặt giá thất bại! Giá bạn đưa ra phải lớn hơn giá cao nhất hiện tại.");
                        }
                        break;

                    case "4":
                        System.out.print("Nhập mã sản phẩm cần xem chi tiết: ");
                        String detailId = scanner.nextLine().trim().toUpperCase();
                        Product detail = service.getProduct(detailId);
                        if (detail != null) {
                            System.out.println("\n--- THÔNG TIN CHI TIẾT SẢN PHẨM ---");
                            System.out.println("Mã sản phẩm         : " + detail.getId());
                            System.out.println("Tên sản phẩm        : " + detail.getName());
                            System.out.printf("Giá khởi điểm       : %,.2f VND%n", detail.getStartingPrice());
                            System.out.printf("Giá thầu cao nhất   : %,.2f VND%n", detail.getCurrentHighestBid());
                            System.out.println("Người giữ giá cao nhất: " + detail.getHighestBidder());
                            System.out.println("Người đăng bán      : " + detail.getSellerName());
                        } else {
                            System.out.println("=> Không tìm thấy sản phẩm mã: " + detailId);
                        }
                        break;

                    case "0":
                        System.out.println("Đang ngắt kết nối và thoát...");
                        service.unregisterClient(callbackObj);
                        UnicastRemoteObject.unexportObject(callbackObj, true);
                        System.out.println("Tạm biệt " + finalUsername + "!");
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

