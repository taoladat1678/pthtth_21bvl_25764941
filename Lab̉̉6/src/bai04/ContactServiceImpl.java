package bai04;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ContactServiceImpl extends UnicastRemoteObject implements ContactService {

    // Server lưu trữ danh bạ bằng HashMap<String, String>
    private final HashMap<String, String> contactBook;

    public ContactServiceImpl() throws RemoteException {
        super();
        contactBook = new HashMap<>();
        // Khởi tạo một số liên hệ mẫu
        contactBook.put("Nguyen Van A", "0901234567");
        contactBook.put("Tran Thi B", "0912345678");
        contactBook.put("Le Van C", "0987654321");
    }

    @Override
    public synchronized void addContact(String name, String phone) throws RemoteException {
        contactBook.put(name, phone);
        System.out.println("[Server] Đã thêm/cập nhật liên hệ: " + name + " -> " + phone);
    }

    @Override
    public synchronized String findContact(String name) throws RemoteException {
        System.out.println("[Server] Tìm kiếm liên hệ: " + name);
        return contactBook.get(name);
    }

    @Override
    public synchronized boolean deleteContact(String name) throws RemoteException {
        System.out.println("[Server] Yêu cầu xóa liên hệ: " + name);
        if (contactBook.containsKey(name)) {
            contactBook.remove(name);
            return true;
        }
        return false;
    }

    @Override
    public synchronized Map<String, String> getAllContacts() throws RemoteException {
        return new HashMap<>(contactBook);
    }
}

