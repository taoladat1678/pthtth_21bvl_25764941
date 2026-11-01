package bai09;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface cho dịch vụ Đấu giá trực tuyến qua RMI.
 */
public interface AuctionService extends Remote {
    /**
     * Dành cho Chủ sở hữu (Seller): Thêm sản phẩm vào hệ thống đấu giá.
     */
    String addProduct(String name, double startingPrice, String sellerName) throws RemoteException;

    /**
     * Dành cho Người đấu giá (Bidder): Xem danh sách sản phẩm đang đấu giá.
     */
    List<Product> getProducts() throws RemoteException;

    /**
     * Lấy thông tin chi tiết một sản phẩm theo ID.
     */
    Product getProduct(String id) throws RemoteException;

    /**
     * Dành cho Người đấu giá (Bidder): Đưa ra giá thầu cao hơn giá hiện tại.
     */
    boolean placeBid(String productId, double bidAmount, String bidderName) throws RemoteException;

    /**
     * Đăng ký nhận thông báo thời gian thực từ Server.
     */
    void registerClient(AuctionCallback client) throws RemoteException;

    /**
     * Hủy đăng ký nhận thông báo.
     */
    void unregisterClient(AuctionCallback client) throws RemoteException;
}

