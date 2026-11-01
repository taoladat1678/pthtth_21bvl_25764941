package bai02;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CalculatorServiceImpl extends UnicastRemoteObject implements CalculatorService {

    public CalculatorServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public int add(int a, int b) throws RemoteException {
        System.out.println("[Server] Nhận yêu cầu tính tổng: " + a + " + " + b);
        return a + b;
    }
}

