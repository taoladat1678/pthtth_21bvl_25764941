package bai09;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AuctionCallbackImpl extends UnicastRemoteObject implements AuctionCallback {

    private final String clientUsername;

    public AuctionCallbackImpl(String clientUsername) throws RemoteException {
        super();
        this.clientUsername = clientUsername;
    }

    @Override
    public void notifyNewBid(String productId, String productName, double newBid, String bidderName) throws RemoteException {
        System.out.println();
        System.out.println(">>> [THÔNG BÁO REAL-TIME] <<<");
        System.out.printf("Sản phẩm: '%s' (Mã: %s)%n", productName, productId);
        System.out.printf("Mức giá thầu mới: %,.2f VND (Người trả giá: %s)%n", newBid, bidderName);
        if (bidderName.equalsIgnoreCase(clientUsername)) {
            System.out.println("(Bạn hiện đang là người trả giá cao nhất!)");
        }
        System.out.println("-----------------------------");
        System.out.print("Chọn chức năng: ");
    }

    @Override
    public void notifyNewProduct(Product product) throws RemoteException {
        System.out.println();
        System.out.println(">>> [THÔNG BÁO REAL-TIME] CÓ SẢN PHẨM MỚI LÊN SÀN! <<<");
        System.out.printf("Sản phẩm: '%s' (Mã: %s) - Giá khởi điểm: %,.2f VND (Người bán: %s)%n",
                product.getName(), product.getId(), product.getStartingPrice(), product.getSellerName());
        System.out.println("-----------------------------");
        System.out.print("Chọn chức năng: ");
    }
}

