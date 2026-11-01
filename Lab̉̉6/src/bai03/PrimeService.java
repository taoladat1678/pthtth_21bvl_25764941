package bai03;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface cho dịch vụ kiểm tra số nguyên tố.
 */
public interface PrimeService extends Remote {
    boolean isPrime(int n) throws RemoteException;
}

