import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Bai tap 1, 2, 3, 4: Quan ly Danh muc San Pham
 * 
 * Bai tap 1: CRUD cho bang SanPham (maSP, tenSP, gia, soluong)
 * Bai tap 2: Su dung giao dich (Transaction)
 * Bai tap 3: Xu ly ngoai le (Exception Handling) voi try-catch-finally
 * Bai tap 4: Su dung PreparedStatement
 */
public class QuanLySanPham {

    // Duong dan ket noi SQLite
    private static final String DB_URL = "jdbc:sqlite:nhanvien.db";

    public static void main(String[] args) {
        try {
            // Nap driver SQLite
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Loi: Khong tim thay driver SQLite JDBC!");
            e.printStackTrace();
            return;
        }

        // Tao bang SanPham
        taoBangSanPham();

        // Bai tap 1 + 4: CREATE - Them san pham (su dung PreparedStatement)
        themSanPham(1, "Laptop Dell", 15000000, 10);
        themSanPham(2, "Chuot Logitech", 500000, 50);
        themSanPham(3, "Ban phim co", 1200000, 30);
        themSanPham(4, "Man hinh LG", 8000000, 15);
        themSanPham(5, "Tai nghe Sony", 2500000, 25);

        // Bai tap 1: READ - Liet ke tat ca san pham
        System.out.println("\n========== DANH SACH TAT CA SAN PHAM ==========");
        lietKeSanPham();

        // Bai tap 1: READ - Tim kiem san pham theo ten
        System.out.println("\n========== TIM KIEM SAN PHAM THEO TEN: 'Laptop' ==========");
        timKiemSanPham("Laptop");

        // Bai tap 1 + 4: UPDATE - Cap nhat gia va so luong
        System.out.println("\n========== CAP NHAT SAN PHAM ==========");
        capNhatSanPham(1, 14500000, 8);

        // Hien thi lai sau khi cap nhat
        System.out.println("\n========== DANH SACH SAU KHI CAP NHAT ==========");
        lietKeSanPham();

        // Bai tap 1 + 4: DELETE - Xoa san pham theo maSP
        System.out.println("\n========== XOA SAN PHAM (maSP = 4) ==========");
        xoaSanPham(4);

        // Hien thi lai sau khi xoa
        System.out.println("\n========== DANH SACH SAU KHI XOA ==========");
        lietKeSanPham();

        // Bai tap 2: Su dung Transaction
        System.out.println("\n========== DEMO GIAO DICH (TRANSACTION) ==========");
        demoTransaction();

        // Hien thi ket qua sau transaction
        System.out.println("\n========== DANH SACH SAU GIAO DICH ==========");
        lietKeSanPham();
    }

    /**
     * Tao bang SanPham neu chua ton tai
     * Bai tap 3: Su dung try-catch-finally de xu ly ngoai le
     */
    private static void taoBangSanPham() {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();

            // Xoa bang cu neu ton tai (de demo lai)
            stmt.executeUpdate("DROP TABLE IF EXISTS SanPham");

            // Tao bang SanPham voi cac cot: maSP, tenSP, gia, soluong
            String createSQL = "CREATE TABLE SanPham ("
                    + "maSP INTEGER PRIMARY KEY, "
                    + "tenSP TEXT NOT NULL, "
                    + "gia REAL, "
                    + "soluong INTEGER"
                    + ")";
            stmt.executeUpdate(createSQL);
            System.out.println("Da tao bang SanPham thanh cong!");

        } catch (SQLException e) {
            System.err.println("Loi SQL khi tao bang: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Dam bao dong tat ca tai nguyen trong khoi finally
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong Statement: " + e.getMessage());
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong Connection: " + e.getMessage());
            }
        }
    }

    /**
     * Bai tap 1 (Create) + Bai tap 4 (PreparedStatement):
     * Them san pham moi vao bang SanPham su dung PreparedStatement
     */
    private static void themSanPham(int maSP, String tenSP, double gia, int soLuong) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL);

            // Su dung PreparedStatement de tang cuong bao mat (chong SQL Injection)
            String insertSQL = "INSERT INTO SanPham (maSP, tenSP, gia, soluong) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSQL);

            // Gan gia tri cho cac tham so
            pstmt.setInt(1, maSP);
            pstmt.setString(2, tenSP);
            pstmt.setDouble(3, gia);
            pstmt.setInt(4, soLuong);

            // Thuc thi cau lenh INSERT
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Da them san pham: " + tenSP + " (so dong anh huong: " + rowsAffected + ")");

        } catch (SQLException e) {
            System.err.println("Loi SQL khi them san pham: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Bai tap 3: Dam bao dong tai nguyen trong finally
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong PreparedStatement: " + e.getMessage());
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong Connection: " + e.getMessage());
            }
        }
    }

    /**
     * Bai tap 1 (Read): Liet ke tat ca san pham
     * Bai tap 3: Xu ly ngoai le try-catch-finally
     */
    private static void lietKeSanPham() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();

            String selectSQL = "SELECT maSP, tenSP, gia, soluong FROM SanPham";
            rs = stmt.executeQuery(selectSQL);

            System.out.printf("%-8s %-20s %-15s %-10s%n", "Ma SP", "Ten SP", "Gia", "So luong");
            System.out.println("-".repeat(55));

            while (rs.next()) {
                int maSP = rs.getInt("maSP");
                String tenSP = rs.getString("tenSP");
                double gia = rs.getDouble("gia");
                int soLuong = rs.getInt("soluong");
                System.out.printf("%-8d %-20s %-15.0f %-10d%n", maSP, tenSP, gia, soLuong);
            }

        } catch (SQLException e) {
            System.err.println("Loi SQL khi liet ke san pham: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Dong tai nguyen theo thu tu nguoc lai: ResultSet -> Statement -> Connection
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong ResultSet: " + e.getMessage());
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong Statement: " + e.getMessage());
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong Connection: " + e.getMessage());
            }
        }
    }

    /**
     * Bai tap 1 (Read): Tim kiem san pham theo tenSP
     * Bai tap 4: Su dung PreparedStatement
     */
    private static void timKiemSanPham(String tuKhoa) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL);

            // Su dung PreparedStatement voi LIKE de tim kiem
            String searchSQL = "SELECT maSP, tenSP, gia, soluong FROM SanPham WHERE tenSP LIKE ?";
            pstmt = conn.prepareStatement(searchSQL);
            pstmt.setString(1, "%" + tuKhoa + "%");

            rs = pstmt.executeQuery();

            System.out.printf("%-8s %-20s %-15s %-10s%n", "Ma SP", "Ten SP", "Gia", "So luong");
            System.out.println("-".repeat(55));

            boolean found = false;
            while (rs.next()) {
                found = true;
                int maSP = rs.getInt("maSP");
                String tenSP = rs.getString("tenSP");
                double gia = rs.getDouble("gia");
                int soLuong = rs.getInt("soluong");
                System.out.printf("%-8d %-20s %-15.0f %-10d%n", maSP, tenSP, gia, soLuong);
            }

            if (!found) {
                System.out.println("Khong tim thay san pham nao voi tu khoa: " + tuKhoa);
            }

        } catch (SQLException e) {
            System.err.println("Loi SQL khi tim kiem: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong ResultSet: " + e.getMessage());
            }
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong PreparedStatement: " + e.getMessage());
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong Connection: " + e.getMessage());
            }
        }
    }

    /**
     * Bai tap 1 (Update): Cap nhat gia va so luong theo maSP
     * Bai tap 4: Su dung PreparedStatement
     */
    private static void capNhatSanPham(int maSP, double giaMoi, int soLuongMoi) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL);

            String updateSQL = "UPDATE SanPham SET gia = ?, soluong = ? WHERE maSP = ?";
            pstmt = conn.prepareStatement(updateSQL);
            pstmt.setDouble(1, giaMoi);
            pstmt.setInt(2, soLuongMoi);
            pstmt.setInt(3, maSP);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.printf("Da cap nhat san pham maSP=%d -> gia=%,.0f VND, soluong=%d%n",
                        maSP, giaMoi, soLuongMoi);
            } else {
                System.out.println("Khong tim thay san pham voi maSP=" + maSP);
            }

        } catch (SQLException e) {
            System.err.println("Loi SQL khi cap nhat: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong PreparedStatement: " + e.getMessage());
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong Connection: " + e.getMessage());
            }
        }
    }

    /**
     * Bai tap 1 (Delete): Xoa san pham theo maSP
     * Bai tap 4: Su dung PreparedStatement
     */
    private static void xoaSanPham(int maSP) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL);

            String deleteSQL = "DELETE FROM SanPham WHERE maSP = ?";
            pstmt = conn.prepareStatement(deleteSQL);
            pstmt.setInt(1, maSP);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Da xoa san pham voi maSP=" + maSP);
            } else {
                System.out.println("Khong tim thay san pham voi maSP=" + maSP);
            }

        } catch (SQLException e) {
            System.err.println("Loi SQL khi xoa: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong PreparedStatement: " + e.getMessage());
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong Connection: " + e.getMessage());
            }
        }
    }

    /**
     * Bai tap 2: Su dung giao dich (Transaction)
     * - Tat che do auto-commit: conn.setAutoCommit(false)
     * - Thuc hien nhieu cau lenh INSERT/UPDATE trong mot giao dich
     * - Goi conn.commit() khi thanh cong
     * - Goi conn.rollback() trong catch() khi that bai
     */
    private static void demoTransaction() {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL);

            // Tat che do auto-commit de su dung giao dich
            conn.setAutoCommit(false);
            System.out.println("Da tat auto-commit. Bat dau giao dich...");

            // Thao tac 1: Them san pham moi
            String insertSQL = "INSERT INTO SanPham (maSP, tenSP, gia, soluong) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSQL);
            pstmt.setInt(1, 6);
            pstmt.setString(2, "USB Kingston 32GB");
            pstmt.setDouble(3, 150000);
            pstmt.setInt(4, 100);
            pstmt.executeUpdate();
            System.out.println("  -> Da them: USB Kingston 32GB");

            // Thao tac 2: Cap nhat so luong san pham khac
            pstmt.close();
            String updateSQL = "UPDATE SanPham SET soluong = soluong - 5 WHERE maSP = ?";
            pstmt = conn.prepareStatement(updateSQL);
            pstmt.setInt(1, 2);
            pstmt.executeUpdate();
            System.out.println("  -> Da cap nhat so luong cua maSP=2 (giam 5)");

            // Thao tac 3: Them them mot san pham nua
            pstmt.close();
            pstmt = conn.prepareStatement(insertSQL);
            pstmt.setInt(1, 7);
            pstmt.setString(2, "Op lung dien thoai");
            pstmt.setDouble(3, 200000);
            pstmt.setInt(4, 200);
            pstmt.executeUpdate();
            System.out.println("  -> Da them: Op lung dien thoai");

            // Tat ca thao tac thanh cong -> Commit giao dich
            conn.commit();
            System.out.println("GIAO DICH THANH CONG! Da commit tat ca thay doi.");

        } catch (SQLException e) {
            System.err.println("Loi SQL trong giao dich: " + e.getMessage());
            // Rollback tat ca cac thay doi khi co loi
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("DA ROLLBACK! Huy bo tat ca cac thay doi.");
                } catch (SQLException ex) {
                    System.err.println("Loi khi rollback: " + ex.getMessage());
                }
            }
            e.printStackTrace();
        } finally {
            // Bai tap 3: Dong tai nguyen trong finally
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                System.err.println("Loi khi dong PreparedStatement: " + e.getMessage());
            }
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Khoi phuc lai auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Loi khi dong Connection: " + e.getMessage());
            }
        }
    }
}
