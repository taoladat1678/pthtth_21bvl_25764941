package bai01;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HostAndUriInspector {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Cú pháp: java bai01.HostAndUriInspector <hostname> <uri>");
            System.out.println("Ví dụ:  java bai01.HostAndUriInspector google.com https://iuh.edu.vn:443/daotao?id=101#khoa");
            return;
        }

        String hostArg = args[0];
        String uriArg = args[1];

        System.out.println("==================================================");
        System.out.println("1. KHẢO SÁT HOSTNAME: " + hostArg);
        System.out.println("==================================================");
        inspectHost(hostArg);

        System.out.println("\n==================================================");
        System.out.println("2. PHÂN TÍCH URI: " + uriArg);
        System.out.println("==================================================");
        inspectUri(uriArg);
    }

    public static void inspectHost(String hostname) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostname);
            System.out.println("Số lượng địa chỉ phân giải: " + addresses.length);
            for (int i = 0; i < addresses.length; i++) {
                InetAddress addr = addresses[i];
                String ipType = (addr instanceof Inet4Address) ? "IPv4"
                        : (addr instanceof Inet6Address) ? "IPv6" : "Không xác định";
                System.out.println("  [" + (i + 1) + "] IP: " + addr.getHostAddress());
                System.out.println("      Loại IP: " + ipType);
                System.out.println("      Canonical Hostname: " + addr.getCanonicalHostName());
                System.out.println("      Loopback: " + addr.isLoopbackAddress());
                System.out.println("      Site Local: " + addr.isSiteLocalAddress());
            }
        } catch (UnknownHostException e) {
            System.err.println("Lỗi: Không thể phân giải hostname '" + hostname + "' (" + e.getMessage() + ")");
        }
    }

    public static void inspectUri(String uriString) {
        try {
            URI uri = new URI(uriString);
            if (uri.getScheme() == null) {
                System.err.println("Cảnh báo URI: Không có Scheme hợp lệ (ví dụ http, https, ftp).");
            }
            System.out.println("  - Scheme:   " + (uri.getScheme() != null ? uri.getScheme() : "(Không có)"));
            System.out.println("  - Host:     " + (uri.getHost() != null ? uri.getHost() : "(Không có)"));
            System.out.println("  - Port:     " + (uri.getPort() != -1 ? uri.getPort() : "(Mặc định / Không có)"));
            System.out.println("  - Path:     " + (uri.getPath() != null && !uri.getPath().isEmpty() ? uri.getPath() : "(Trống)"));
            System.out.println("  - Query:    " + (uri.getQuery() != null ? uri.getQuery() : "(Không có)"));
            System.out.println("  - Fragment: " + (uri.getFragment() != null ? uri.getFragment() : "(Không có)"));
        } catch (URISyntaxException e) {
            System.err.println("Lỗi cú pháp URI: Chuỗi '" + uriString + "' không phải là URI hợp lệ.");
            System.err.println("Chi tiết lỗi: " + e.getReason() + " tại chỉ số " + e.getIndex());
        }
    }
}
