package bai01;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface cho dịch vụ RMI Hello World.
 */
public interface HelloService extends Remote {
    String sayHello() throws RemoteException;
}

