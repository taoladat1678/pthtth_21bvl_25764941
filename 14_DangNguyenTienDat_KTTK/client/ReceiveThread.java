package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class ReceiveThread implements Runnable {
    private BufferedReader in;
    private Socket socket;

    public ReceiveThread(Socket socket, BufferedReader in) {
        this.socket = socket;
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                System.out.println("Mất kết nối tới server.");
            }
        }
    }
}