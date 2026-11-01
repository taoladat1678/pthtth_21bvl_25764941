package bai07;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface cho dịch vụ Tính Diện Tích Hình Học qua RMI.
 */
public interface GeometryService extends Remote {
    double rectangleArea(double width, double height) throws RemoteException;
    double circleArea(double radius) throws RemoteException;
    double triangleArea(double base, double height) throws RemoteException;
}

