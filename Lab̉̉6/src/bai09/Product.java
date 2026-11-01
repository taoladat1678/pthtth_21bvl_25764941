package bai09;

import java.io.Serializable;

/**
 * Lớp đại diện cho một sản phẩm đấu giá.
 * Thực thi Serializable để có thể truyền qua RMI giữa Server và Client.
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private double startingPrice;
    private double currentHighestBid;
    private String highestBidder;
    private String sellerName;

    public Product(String id, String name, double startingPrice, String sellerName) {
        this.id = id;
        this.name = name;
        this.startingPrice = startingPrice;
        this.currentHighestBid = startingPrice;
        this.highestBidder = "(Chưa có)";
        this.sellerName = sellerName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public double getCurrentHighestBid() {
        return currentHighestBid;
    }

    public void setCurrentHighestBid(double currentHighestBid) {
        this.currentHighestBid = currentHighestBid;
    }

    public String getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(String highestBidder) {
        this.highestBidder = highestBidder;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-25s | Khởi điểm: %,12.2f VND | Giá cao nhất: %,12.2f VND | Người giữ giá: %-15s | Người bán: %s",
                id, name, startingPrice, currentHighestBid, highestBidder, sellerName);
    }
}

