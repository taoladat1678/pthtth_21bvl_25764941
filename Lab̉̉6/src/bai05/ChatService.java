package bai05;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface cho dịch vụ Chat 1-1 qua RMI.
 */
public interface ChatService extends Remote {
    /**
     * Gửi tin nhắn từ client đến server (theo đúng yêu cầu đề bài).
     */
    void sendMessage(String message) throws RemoteException;

    /**
     * Đăng ký callback để client nhận phản hồi từ server.
     */
    void registerClient(ChatCallback clientCallback) throws RemoteException;

    /**
     * Hủy đăng ký callback khi client ngắt kết nối.
     */
    void unregisterClient(ChatCallback clientCallback) throws RemoteException;

    /**
     * Phương thức tiện ích: Gửi tin nhắn và nhận chuỗi phản hồi trực tiếp.
     */
    String sendMessageWithReply(String message) throws RemoteException;
}

