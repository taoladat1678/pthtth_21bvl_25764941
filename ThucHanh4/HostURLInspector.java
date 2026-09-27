import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HostURLInspector {

    public static void main(String[] args) {
        // 1. Xử lý thiếu tham số
        if (args.length < 2) {
            System.err.println("Lỗi: Thiếu tham số.");
            System.err.println("Cách sử dụng: java HostURLInspector <hostname> <URL>");
            return;
        }

        String hostname = args[0];
        String urlString = args[1];

        System.out.println("=== KẾT QUẢ KIỂM TRA HOSTNAME ===");
        try {
            // Lấy tất cả các địa chỉ IP của hostname
            InetAddress[] addresses = InetAddress.getAllByName(hostname);
            System.out.println("Hostname: " + hostname);
            for (InetAddress addr : addresses) {
                System.out.println("IP: " + addr.getHostAddress());

                // In loại IPv4 hoặc IPv6
                if (addr instanceof Inet4Address) {
                    System.out.println("  - Loại: IPv4");
                } else if (addr instanceof Inet6Address) {
                    System.out.println("  - Loại: IPv6");
                } else {
                    System.out.println("  - Loại: Không xác định");
                }

                // In trạng thái loopback và site local
                System.out.println("  - Loopback: " + addr.isLoopbackAddress());
                System.out.println("  - Site Local: " + addr.isSiteLocalAddress());
            }
        } catch (UnknownHostException e) {
            // Xử lý hostname không phân giải được
            System.err.println("Lỗi: Không thể phân giải hostname '" + hostname + "'");
        }

        System.out.println("\n=== KẾT QUẢ KIỂM TRA URL ===");
        try {
            // Phân tích URL (Vẫn dùng java.net.URI theo đúng yêu cầu đề bài)
            URI url = new URI(urlString);
            System.out.println("URL gốc: " + urlString);
            System.out.println("  - Scheme: " + url.getScheme());
            System.out.println("  - Host: " + url.getHost());
            System.out.println("  - Port: " + url.getPort());
            System.out.println("  - Path: " + url.getPath());
            System.out.println("  - Query: " + url.getQuery());
            System.out.println("  - Fragment: " + url.getFragment());
        } catch (URISyntaxException e) {
            // Xử lý URL sai định dạng
            System.err.println("Lỗi: Cú pháp URL không hợp lệ '" + urlString + "'");
            System.err.println("Chi tiết lỗi: " + e.getMessage());
        }
    }
}
