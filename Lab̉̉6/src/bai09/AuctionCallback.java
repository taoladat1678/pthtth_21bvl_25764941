package bai09;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface callback để Server thông báo thời gian thực đến các Client.
 */
public interface AuctionCallback extends Remote {
    /**
     * Thông báo khi có người đặt giá thầu cao hơn.
     */
    void notifyNewBid(String productId, String productName, double newBid, String bidderName) throws RemoteException;

    /**
     * Thông báo khi có sản phẩm mới được thêm vào đấu giá.
     */
    void notifyNewProduct(Product product) throws RemoteException;
}

