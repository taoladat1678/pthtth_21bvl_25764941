package bai03;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PrimeServiceImpl extends UnicastRemoteObject implements PrimeService {

    public PrimeServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean isPrime(int n) throws RemoteException {
        System.out.println("[Server] Nhận yêu cầu kiểm tra số nguyên tố cho n = " + n);
        if (n <= 1) {
            return false;
        }
        if (n == 2) {
            return true;
        }
        if (n % 2 == 0) {
            return false;
        }
        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}

