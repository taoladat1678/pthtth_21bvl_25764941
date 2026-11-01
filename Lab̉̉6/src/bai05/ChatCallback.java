package bai05;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface callback để Client nhận phản hồi từ Server.
 */
public interface ChatCallback extends Remote {
    void receiveMessage(String sender, String message) throws RemoteException;
}

