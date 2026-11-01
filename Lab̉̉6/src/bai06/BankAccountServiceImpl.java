package bai06;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BankAccountServiceImpl extends UnicastRemoteObject implements BankAccountService {

    private double balance;

    // Khởi tạo tài khoản với số dư ban đầu mặc định (ví dụ 10,000,000 VND)
    public BankAccountServiceImpl() throws RemoteException {
        this(10000000.0);
    }

    public BankAccountServiceImpl(double initialBalance) throws RemoteException {
        super();
        this.balance = initialBalance;
        System.out.printf("[Server] Khởi tạo tài khoản với số dư ban đầu: %,.2f VND%n", balance);
    }

    @Override
    public synchronized double getBalance() throws RemoteException {
        System.out.printf("[Server] Kiểm tra số dư: %,.2f VND%n", balance);
        return balance;
    }

    @Override
    public synchronized void deposit(double amount) throws RemoteException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền gửi phải lớn hơn 0!");
        }
        balance += amount;
        System.out.printf("[Server] Gửi tiền: +%,.2f VND. Số dư mới: %,.2f VND%n", amount, balance);
    }

    @Override
    public synchronized boolean withdraw(double amount) throws RemoteException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền rút phải lớn hơn 0!");
        }
        if (balance >= amount) {
            balance -= amount;
            System.out.printf("[Server] Rút tiền: -%,.2f VND. Số dư còn lại: %,.2f VND%n", amount, balance);
            return true;
        } else {
            System.out.printf("[Server] Rút tiền thất bại! Yêu cầu: %,.2f VND, nhưng số dư chỉ có: %,.2f VND%n", amount, balance);
            return false;
        }
    }
}

