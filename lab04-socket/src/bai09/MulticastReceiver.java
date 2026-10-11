package bai09;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class MulticastReceiver {
    public static final String MULTICAST_ADDRESS = "239.255.0.1";
    public static final int MULTICAST_PORT = 5009;

    public static void main(String[] args) {
        String receiverId = args.length > 0 ? args[0] : "Receiver_1";
        // Tham số thứ 2 (tùy chọn): tên card mạng hoặc "loopback"
        String ifaceName = args.length > 1 ? args[1] : null;

        try {
            InetAddress groupAddress = InetAddress.getByName(MULTICAST_ADDRESS);
            InetSocketAddress group = new InetSocketAddress(groupAddress, MULTICAST_PORT);

            NetworkInterface netIf = selectNetworkInterface(ifaceName);
            if (netIf == null) {
                System.err.println("Không tìm thấy Network Interface nào hỗ trợ IPv4 Multicast!");
                return;
            }

            System.out.printf("[%s] Sử dụng Network Interface: %s (%s)\n",
                    receiverId, netIf.getName(), netIf.getDisplayName());

            // MulticastSocket mặc định bật SO_REUSEADDR giúp nhiều receiver cùng lắng nghe một cổng
            try (MulticastSocket socket = new MulticastSocket(MULTICAST_PORT)) {
                // Tham gia nhóm với NetworkInterface chỉ định rõ ràng theo chuẩn JDK 17+
                socket.joinGroup(group, netIf);
                System.out.printf("[%s] Đã tham gia nhóm Multicast %s:%d thành công. Đang lắng nghe thông điệp...\n",
                        receiverId, MULTICAST_ADDRESS, MULTICAST_PORT);

                byte[] buffer = new byte[4096];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String message = new String(packet.getData(),
                            packet.getOffset(), packet.getLength(),
                            StandardCharsets.UTF_8);

                    System.out.printf("[%s] Nhận được từ %s: %s\n",
                            receiverId, packet.getSocketAddress(), message);

                    if (message.equalsIgnoreCase("SHUTDOWN")) {
                        System.out.printf("[%s] Nhận lệnh SHUTDOWN, chuẩn bị rời nhóm...\n", receiverId);
                        break;
                    }
                }

                // Rời nhóm trước khi đóng socket theo yêu cầu
                socket.leaveGroup(group, netIf);
                System.out.printf("[%s] Đã rời nhóm Multicast an toàn.\n", receiverId);
            }
        } catch (IOException e) {
            System.err.printf("[%s] Lỗi Multicast Receiver: %s\n", receiverId, e.getMessage());
        }
    }

    public static NetworkInterface selectNetworkInterface(String preferredName) throws SocketException {
        if (preferredName != null && !preferredName.isEmpty()) {
            if (preferredName.equalsIgnoreCase("loopback") || preferredName.equalsIgnoreCase("lo") || preferredName.equalsIgnoreCase("localhost")) {
                return NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress());
            }
            NetworkInterface custom = NetworkInterface.getByName(preferredName);
            if (custom != null && custom.isUp() && hasValidIPv4(custom)) return custom;
        }

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface physicalCandidate = null;
        NetworkInterface fallbackCandidate = null;
        NetworkInterface loopbackCandidate = null;

        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (ni.isUp() && ni.supportsMulticast() && hasValidIPv4(ni)) {
                if (ni.isLoopback()) {
                    loopbackCandidate = ni;
                } else {
                    String desc = ni.getDisplayName().toLowerCase();
                    // Loại bỏ các adapter ảo Npcap, VMware, VirtualBox khi tìm card mạng vật lý thật
                    if (!desc.contains("npcap") && !desc.contains("vmware") && !desc.contains("virtual") && !desc.contains("vbox")) {
                        if (physicalCandidate == null) {
                            physicalCandidate = ni;
                        }
                    } else if (fallbackCandidate == null) {
                        fallbackCandidate = ni;
                    }
                }
            }
        }

        if (physicalCandidate != null) {
            return physicalCandidate;
        }
        if (loopbackCandidate != null) {
            return loopbackCandidate;
        }
        if (fallbackCandidate != null) {
            return fallbackCandidate;
        }
        return NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress());
    }

    public static boolean hasValidIPv4(NetworkInterface ni) {
        Enumeration<InetAddress> addrs = ni.getInetAddresses();
        while (addrs.hasMoreElements()) {
            InetAddress a = addrs.nextElement();
            if (a instanceof Inet4Address && !a.isAnyLocalAddress()) {
                return true;
            }
        }
        return false;
    }
}
