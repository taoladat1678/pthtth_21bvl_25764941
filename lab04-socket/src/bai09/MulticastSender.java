package bai09;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;

public class MulticastSender {
    public static void main(String[] args) {
        String msgArg = args.length > 0 ? args[0] : null;
        String ifaceName = args.length > 1 ? args[1] : null;

        try {
            InetAddress group = InetAddress.getByName(MulticastReceiver.MULTICAST_ADDRESS);
            int port = MulticastReceiver.MULTICAST_PORT;

            NetworkInterface netIf = MulticastReceiver.selectNetworkInterface(ifaceName);
            System.out.println("Multicast Sender gửi tới " + group.getHostAddress() + ":" + port);
            if (netIf != null) {
                System.out.println("Giao diện mạng sử dụng: " + netIf.getName() + " (" + netIf.getDisplayName() + ")");
            }

            try (MulticastSocket socket = new MulticastSocket()) {
                if (netIf != null) {
                    socket.setNetworkInterface(netIf);
                }
                socket.setTimeToLive(1); // Phạm vi mạng cục bộ

                if (msgArg != null) {
                    sendNotification(socket, group, port, msgArg);
                    return;
                }

                System.out.println("Nhập thông báo cần broadcast tới nhóm (hoặc 'exit' để dừng):");
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                String line;
                while ((line = console.readLine()) != null) {
                    if (line.trim().equalsIgnoreCase("exit")) break;
                    sendNotification(socket, group, port, line.trim());
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi Multicast Sender: " + e.getMessage());
        }
    }

    public static void sendNotification(MulticastSocket socket, InetAddress group, int port, String text) throws Exception {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
        socket.send(packet);
        System.out.println("-> Đã phát thông báo multicast: [" + text + "]");
    }
}
