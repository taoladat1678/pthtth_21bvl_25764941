package bai08;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class FlightServiceImpl extends UnicastRemoteObject implements FlightService {

    // Danh sách chuyến bay và số ghế còn trống
    private final Map<String, Integer> flightMap;

    public FlightServiceImpl() throws RemoteException {
        super();
        this.flightMap = new HashMap<>();
        // Khởi tạo một số chuyến bay mẫu
        flightMap.put("VN123", 50);
        flightMap.put("VJ456", 30);
        flightMap.put("QH789", 40);
        flightMap.put("VU888", 20);
    }

    @Override
    public synchronized boolean bookTicket(String flightNumber, int seats) throws RemoteException {
        System.out.printf("[Server] Yêu cầu đặt %d vé cho chuyến bay %s%n", seats, flightNumber);
        if (seats <= 0 || !flightMap.containsKey(flightNumber)) {
            return false;
        }
        int currentSeats = flightMap.get(flightNumber);
        if (currentSeats >= seats) {
            flightMap.put(flightNumber, currentSeats - seats);
            System.out.printf("[Server] Đặt vé thành công! Chuyến bay %s còn %d ghế trống.%n",
                    flightNumber, currentSeats - seats);
            return true;
        } else {
            System.out.printf("[Server] Đặt vé thất bại! Chuyến bay %s chỉ còn %d ghế trống (yêu cầu %d ghế).%n",
                    flightNumber, currentSeats, seats);
            return false;
        }
    }

    @Override
    public synchronized int availableSeats(String flightNumber) throws RemoteException {
        Integer seats = flightMap.get(flightNumber);
        if (seats == null) {
            System.out.println("[Server] Tra cứu chuyến bay không tồn tại: " + flightNumber);
            return -1; // -1 biểu thị chuyến bay không tồn tại
        }
        System.out.printf("[Server] Tra cứu số ghế trống của chuyến bay %s: %d ghế%n", flightNumber, seats);
        return seats;
    }

    @Override
    public synchronized boolean cancelBooking(String flightNumber, int seats) throws RemoteException {
        System.out.printf("[Server] Yêu cầu hủy %d vé cho chuyến bay %s%n", seats, flightNumber);
        if (seats <= 0 || !flightMap.containsKey(flightNumber)) {
            return false;
        }
        int currentSeats = flightMap.get(flightNumber);
        flightMap.put(flightNumber, currentSeats + seats);
        System.out.printf("[Server] Hủy vé thành công! Chuyến bay %s hiện có %d ghế trống.%n",
                flightNumber, currentSeats + seats);
        return true;
    }

    @Override
    public synchronized Map<String, Integer> getAllFlights() throws RemoteException {
        return new HashMap<>(flightMap);
    }
}

