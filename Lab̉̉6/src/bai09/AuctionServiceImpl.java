package bai09;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class AuctionServiceImpl extends UnicastRemoteObject implements AuctionService {

    private final Map<String, Product> productMap;
    private final List<AuctionCallback> clients;
    private final AtomicInteger idCounter;

    public AuctionServiceImpl() throws RemoteException {
        super();
        this.productMap = new LinkedHashMap<>();
        this.clients = new CopyOnWriteArrayList<>();
        this.idCounter = new AtomicInteger(1);

        // Khởi tạo một số sản phẩm mẫu ban đầu
        initSampleProducts();
    }

    private void initSampleProducts() {
        Product p1 = new Product(generateId(), "iPhone 15 Pro Max 256GB", 25_000_000.0, "Apple Official");
        Product p2 = new Product(generateId(), "MacBook Pro M3 Max 36GB", 45_000_000.0, "TechZone");
        Product p3 = new Product(generateId(), "Dong ho Rolex Submariner", 150_000_000.0, "Luxury Store");

        productMap.put(p1.getId(), p1);
        productMap.put(p2.getId(), p2);
        productMap.put(p3.getId(), p3);
    }

    private String generateId() {
        return String.format("SP%02d", idCounter.getAndIncrement());
    }

    @Override
    public synchronized String addProduct(String name, double startingPrice, String sellerName) throws RemoteException {
        if (startingPrice <= 0) {
            throw new IllegalArgumentException("Giá khởi điểm phải lớn hơn 0!");
        }
        String id = generateId();
        Product product = new Product(id, name, startingPrice, sellerName);
        productMap.put(id, product);

        System.out.printf("[Server] Seller '%s' đã thêm sản phẩm mới: [%s] %s với giá khởi điểm %,.2f VND%n",
                sellerName, id, name, startingPrice);

        // Phát thông báo real-time tới tất cả client đang theo dõi sàn
        broadcastNewProduct(product);
        return id;
    }

    @Override
    public synchronized List<Product> getProducts() throws RemoteException {
        return new ArrayList<>(productMap.values());
    }

    @Override
    public synchronized Product getProduct(String id) throws RemoteException {
        return productMap.get(id);
    }

    @Override
    public synchronized boolean placeBid(String productId, double bidAmount, String bidderName) throws RemoteException {
        Product product = productMap.get(productId);
        if (product == null) {
            System.out.println("[Server] Đặt giá thất bại: Không tìm thấy sản phẩm mã " + productId);
            return false;
        }

        // Yêu cầu: Đưa ra giá thầu cao hơn giá hiện tại
        if (bidAmount > product.getCurrentHighestBid()) {
            product.setCurrentHighestBid(bidAmount);
            product.setHighestBidder(bidderName);

            System.out.printf("[Server] Giá thầu mới cho [%s - %s]: %,.2f VND bởi '%s'%n",
                    product.getId(), product.getName(), bidAmount, bidderName);

            // Bắn thông báo real-time tới tất cả client
            broadcastNewBid(product.getId(), product.getName(), bidAmount, bidderName);
            return true;
        } else {
            System.out.printf("[Server] Đặt giá không hợp lệ: %,.2f VND không cao hơn giá hiện tại %,.2f VND%n",
                    bidAmount, product.getCurrentHighestBid());
            return false;
        }
    }

    @Override
    public synchronized void registerClient(AuctionCallback client) throws RemoteException {
        if (!clients.contains(client)) {
            clients.add(client);
            System.out.println("[Server] Một client mới đã đăng ký nhận thông báo real-time.");
        }
    }

    @Override
    public synchronized void unregisterClient(AuctionCallback client) throws RemoteException {
        clients.remove(client);
        System.out.println("[Server] Một client đã hủy đăng ký thông báo.");
    }

    private void broadcastNewBid(String productId, String productName, double newBid, String bidderName) {
        for (AuctionCallback client : clients) {
            try {
                client.notifyNewBid(productId, productName, newBid, bidderName);
            } catch (RemoteException e) {
                // Client có thể đã ngắt kết nối đột ngột
                clients.remove(client);
            }
        }
    }

    private void broadcastNewProduct(Product product) {
        for (AuctionCallback client : clients) {
            try {
                client.notifyNewProduct(product);
            } catch (RemoteException e) {
                clients.remove(client);
            }
        }
    }
}

