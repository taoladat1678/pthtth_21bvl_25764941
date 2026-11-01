package bai07;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GeometryServiceImpl extends UnicastRemoteObject implements GeometryService {

    public GeometryServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public double rectangleArea(double width, double height) throws RemoteException {
        System.out.printf("[Server] Tính diện tích hình chữ nhật: rộng = %.2f, cao = %.2f%n", width, height);
        return width * height;
    }

    @Override
    public double circleArea(double radius) throws RemoteException {
        System.out.printf("[Server] Tính diện tích hình tròn: bán kính = %.2f%n", radius);
        return Math.PI * radius * radius;
    }

    @Override
    public double triangleArea(double base, double height) throws RemoteException {
        System.out.printf("[Server] Tính diện tích hình tam giác: đáy = %.2f, chiều cao = %.2f%n", base, height);
        return 0.5 * base * height;
    }
}

