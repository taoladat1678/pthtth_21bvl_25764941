import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Bài tập cơ bản B1-B5: Kết nối JDBC với SQLite
 * - B1: Kết nối đến cơ sở dữ liệu SQLite (nhanvien.db)
 * - B2: Tạo bảng NhanVien (id, ten, chuc_vu)
 * - B3: Thêm ít nhất 3 nhân viên
 * - B4: Truy vấn tất cả nhân viên bằng ResultSet, in ra console
 * - B5: Đóng tài nguyên đúng cách
 */
public class BaiTapCoBan {

    // Đường dẫn kết nối SQLite
    private static final String DB_URL = "jdbc:sqlite:nhanvien.db";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // ===== B1: Ket noi den co so du lieu SQLite =====
            System.out.println("===== B1: Ket noi co so du lieu =====");
            Class.forName("org.sqlite.JDBC"); // Nap driver SQLite
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Ket noi thanh cong den co so du lieu nhanvien.db!");

            stmt = conn.createStatement();

            // ===== B2: Tao bang NhanVien =====
            System.out.println("\n===== B2: Tao bang NhanVien =====");
            // Xoa bang cu neu ton tai de chay lai duoc nhieu lan
            stmt.executeUpdate("DROP TABLE IF EXISTS NhanVien");

            String createTableSQL = "CREATE TABLE NhanVien ("
                    + "id INTEGER PRIMARY KEY, "
                    + "ten TEXT NOT NULL, "
                    + "chuc_vu TEXT"
                    + ")";
            stmt.executeUpdate(createTableSQL);
            System.out.println("Tao bang NhanVien thanh cong!");

            // ===== B3: Them it nhat 3 nhan vien =====
            System.out.println("\n===== B3: Them nhan vien =====");

            String insert1 = "INSERT INTO NhanVien (id, ten, chuc_vu) VALUES (1, 'Nguyen Van An', 'Giam doc')";
            String insert2 = "INSERT INTO NhanVien (id, ten, chuc_vu) VALUES (2, 'Tran Thi Binh', 'Truong phong')";
            String insert3 = "INSERT INTO NhanVien (id, ten, chuc_vu) VALUES (3, 'Le Hoang Cuong', 'Nhan vien')";
            String insert4 = "INSERT INTO NhanVien (id, ten, chuc_vu) VALUES (4, 'Pham Minh Duc', 'Nhan vien')";
            String insert5 = "INSERT INTO NhanVien (id, ten, chuc_vu) VALUES (5, 'Hoang Thi Em', 'Truong phong')";

            stmt.executeUpdate(insert1);
            System.out.println("Da them nhan vien: Nguyen Van An");
            stmt.executeUpdate(insert2);
            System.out.println("Da them nhan vien: Tran Thi Binh");
            stmt.executeUpdate(insert3);
            System.out.println("Da them nhan vien: Le Hoang Cuong");
            stmt.executeUpdate(insert4);
            System.out.println("Da them nhan vien: Pham Minh Duc");
            stmt.executeUpdate(insert5);
            System.out.println("Da them nhan vien: Hoang Thi Em");

            // ===== B4: Truy van tat ca nhan vien =====
            System.out.println("\n===== B4: Danh sach nhan vien =====");
            rs = stmt.executeQuery("SELECT id, ten, chuc_vu FROM NhanVien");

            System.out.printf("%-5s %-25s %-20s%n", "ID", "Ten", "Chuc vu");
            System.out.println("-".repeat(50));

            while (rs.next()) {
                int id = rs.getInt("id");
                String ten = rs.getString("ten");
                String chucVu = rs.getString("chuc_vu");
                System.out.printf("%-5d %-25s %-20s%n", id, ten, chucVu);
            }

            System.out.println("\nChuong trinh thuc thi thanh cong!");

        } catch (ClassNotFoundException e) {
            // Loi khong tim thay driver JDBC
            System.err.println("Loi: Khong tim thay driver SQLite JDBC!");
            System.err.println("Hay dam bao da them sqlite-jdbc.jar vao classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            // Loi SQL
            System.err.println("Loi SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // ===== B5: Dong tai nguyen dung cach =====
            System.out.println("\n===== B5: Dong tai nguyen =====");
            try {
                if (rs != null) {
                    rs.close();
                    System.out.println("Da dong ResultSet.");
                }
            } catch (SQLException e) {
                System.err.println("Loi khi dong ResultSet: " + e.getMessage());
            }
            try {
                if (stmt != null) {
                    stmt.close();
                    System.out.println("Da dong Statement.");
                }
            } catch (SQLException e) {
                System.err.println("Loi khi dong Statement: " + e.getMessage());
            }
            try {
                if (conn != null) {
                    conn.close();
                    System.out.println("Da dong Connection.");
                }
            } catch (SQLException e) {
                System.err.println("Loi khi dong Connection: " + e.getMessage());
            }
        }
    }
}
