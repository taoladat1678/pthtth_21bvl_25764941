package bai02;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface cho dịch vụ tính toán từ xa.
 */
public interface CalculatorService extends Remote {
    int add(int a, int b) throws RemoteException;
}

