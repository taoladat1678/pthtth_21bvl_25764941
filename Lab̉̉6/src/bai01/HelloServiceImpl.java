package bai01;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Lớp cài đặt (implementation) của HelloService.
 */
public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {

    public HelloServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String sayHello() throws RemoteException {
        return "Hello, World!";
    }
}

