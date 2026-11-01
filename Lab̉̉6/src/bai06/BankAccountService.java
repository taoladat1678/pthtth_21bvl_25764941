package bai06;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface cho dịch vụ Quản lý Tài khoản Ngân hàng từ xa.
 */
public interface BankAccountService extends Remote {
    double getBalance() throws RemoteException;
    void deposit(double amount) throws RemoteException;
    boolean withdraw(double amount) throws RemoteException;
}

