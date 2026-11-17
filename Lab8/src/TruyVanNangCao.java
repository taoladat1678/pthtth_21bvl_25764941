import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Bai tap 5: Truy van du lieu nang cao (Aggregation Functions)
 * 
 * - Them cot luong vao bang NhanVien
 * - Tinh tong luong: SUM(luong)
 * - Tim muc luong trung binh: AVG(luong)
 * - Tim nhan vien co muc luong cao nhat/thap nhat: MIN/MAX(luong)
 * - Dem so luong nhan vien theo chuc vu: COUNT va GROUP BY
 */
public class TruyVanNangCao {

    private static final String DB_URL = "jdbc:sqlite:nhanvien.db";

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Loi: Khong tim thay driver SQLite JDBC!");
            e.printStackTrace();
            return;
        }

        // Tao bang NhanVien voi cot luong
        taoBangNhanVienVoiLuong();

        // Them du lieu nhan vien voi luong
        themNhanVien();

        // Hien thi danh sach nhan vien
        System.out.println("========== DANH SACH NHAN VIEN ==========");
        hienThiDanhSach();

        // Tinh tong luong - SUM(luong)
        System.out.println("\n========== TONG LUONG - SUM(luong) ==========");
        tinhTongLuong();

        // Tinh luong trung binh - AVG(luong)
        System.out.println("\n========== LUONG TRUNG BINH - AVG(luong) ==========");
        tinhLuongTrungBinh();

        // Tim luong cao nhat va thap nhat - MAX(luong), MIN(luong)
        System.out.println("\n========== LUONG CAO NHAT / THAP NHAT - MAX/MIN ==========");
        timLuongMaxMin();

        // Dem so nhan vien theo chuc vu - COUNT + GROUP BY
        System.out.println("\n========== SO NHAN VIEN THEO CHUC VU - COUNT + GROUP BY ==========");
        demTheoChucVu();
    }

    /**
     * Tao bang NhanVien voi cot luong (salary)
     */
    private static void taoBangNhanVienVoiLuong() {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();

            // Xoa bang cu va tao lai voi cot luong
            stmt.executeUpdate("DROP TABLE IF EXISTS NhanVien");

            String createSQL = "CREATE TABLE NhanVien ("
                    + "id INTEGER PRIMARY KEY, "
                    + "ten TEXT NOT NULL, "
                    + "chuc_vu TEXT, "
                    + "luong REAL"
                    + ")";
            stmt.executeUpdate(createSQL);
            System.out.println("Da tao bang NhanVien (co cot luong) thanh cong!");

        } catch (SQLException e) {
            System.err.println("Loi SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                /* ignore */ }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                /* ignore */ }
        }
    }

    /**
     * Them nhieu nhan vien voi cac muc luong khac nhau
     * Su dung PreparedStatement va Transaction
     */
    private static void themNhanVien() {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(false); // Su dung giao dich

            String insertSQL = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSQL);

            // Mang du lieu nhan vien: {id, ten, chuc_vu, luong}
            Object[][] nhanViens = {
                    { 1, "Nguyen Van An", "Giam doc", 50000000.0 },
                    { 2, "Tran Thi Binh", "Truong phong", 30000000.0 },
                    { 3, "Le Hoang Cuong", "Nhan vien", 12000000.0 },
                    { 4, "Pham Minh Duc", "Nhan vien", 11000000.0 },
                    { 5, "Hoang Thi Em", "Truong phong", 28000000.0 },
                    { 6, "Vo Van Phuc", "Nhan vien", 13000000.0 },
                    { 7, "Dang Thi Giang", "Nhan vien", 10500000.0 },
                    { 8, "Bui Van Hung", "Truong phong", 32000000.0 },
                    { 9, "Ly Thi Inh", "Nhan vien", 11500000.0 },
                    { 10, "Ngo Van Khanh", "Nhan vien", 14000000.0 }
            };

            for (Object[] nv : nhanViens) {
                pstmt.setInt(1, (int) nv[0]);
                pstmt.setString(2, (String) nv[1]);
                pstmt.setString(3, (String) nv[2]);
                pstmt.setDouble(4, (double) nv[3]);
                pstmt.addBatch(); // Them vao batch
            }

            // Thuc thi batch
            pstmt.executeBatch();
            conn.commit(); // Commit giao dich
            System.out.println("Da them " + nhanViens.length + " nhan vien thanh cong!\n");

        } catch (SQLException e) {
            System.err.println("Loi SQL: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    /* ignore */ }
            }
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                /* ignore */ }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                /* ignore */ }
        }
    }

    /**
     * Hien thi danh sach tat ca nhan vien
     */
    private static void hienThiDanhSach() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id, ten, chuc_vu, luong FROM NhanVien ORDER BY id");

            System.out.printf("%-5s %-20s %-15s %-15s%n", "ID", "Ten", "Chuc vu", "Luong");
            System.out.println("-".repeat(58));

            while (rs.next()) {
                System.out.printf("%-5d %-20s %-15s %,.0f%n",
                        rs.getInt("id"),
                        rs.getString("ten"),
                        rs.getString("chuc_vu"),
                        rs.getDouble("luong"));
            }

        } catch (SQLException e) {
            System.err.println("Loi SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                /* ignore */ }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                /* ignore */ }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                /* ignore */ }
        }
    }

    /**
     * Tinh tong luong cua tat ca nhan vien: SUM(luong)
     */
    private static void tinhTongLuong() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT SUM(luong) AS tong_luong FROM NhanVien");

            if (rs.next()) {
                double tongLuong = rs.getDouble("tong_luong");
                System.out.printf("Tong luong cua tat ca nhan vien: %,.0f VND%n", tongLuong);
            }

        } catch (SQLException e) {
            System.err.println("Loi SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                /* ignore */ }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                /* ignore */ }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                /* ignore */ }
        }
    }

    /**
     * Tinh luong trung binh: AVG(luong)
     */
    private static void tinhLuongTrungBinh() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT AVG(luong) AS luong_tb FROM NhanVien");

            if (rs.next()) {
                double luongTB = rs.getDouble("luong_tb");
                System.out.printf("Luong trung binh: %,.0f VND%n", luongTB);
            }

        } catch (SQLException e) {
            System.err.println("Loi SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                /* ignore */ }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                /* ignore */ }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                /* ignore */ }
        }
    }

    /**
     * Tim nhan vien co luong cao nhat va thap nhat: MAX(luong), MIN(luong)
     */
    private static void timLuongMaxMin() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();

            // Tim nhan vien co luong cao nhat
            rs = stmt.executeQuery(
                    "SELECT ten, chuc_vu, luong FROM NhanVien WHERE luong = (SELECT MAX(luong) FROM NhanVien)");
            if (rs.next()) {
                System.out.printf("Luong CAO NHAT: %s (%s) - %,.0f VND%n",
                        rs.getString("ten"), rs.getString("chuc_vu"), rs.getDouble("luong"));
            }
            rs.close();

            // Tim nhan vien co luong thap nhat
            rs = stmt.executeQuery(
                    "SELECT ten, chuc_vu, luong FROM NhanVien WHERE luong = (SELECT MIN(luong) FROM NhanVien)");
            if (rs.next()) {
                System.out.printf("Luong THAP NHAT: %s (%s) - %,.0f VND%n",
                        rs.getString("ten"), rs.getString("chuc_vu"), rs.getDouble("luong"));
            }

        } catch (SQLException e) {
            System.err.println("Loi SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                /* ignore */ }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                /* ignore */ }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                /* ignore */ }
        }
    }

    /**
     * Dem so luong nhan vien theo chuc vu: COUNT + GROUP BY
     */
    private static void demTheoChucVu() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();

            String sql = "SELECT chuc_vu, COUNT(*) AS so_luong, "
                    + "SUM(luong) AS tong_luong, AVG(luong) AS luong_tb "
                    + "FROM NhanVien GROUP BY chuc_vu ORDER BY so_luong DESC";
            rs = stmt.executeQuery(sql);

            System.out.printf("%-15s %-12s %-18s %-18s%n",
                    "Chuc vu", "So luong", "Tong luong", "Luong TB");
            System.out.println("-".repeat(65));

            while (rs.next()) {
                String chucVu = rs.getString("chuc_vu");
                int soLuong = rs.getInt("so_luong");
                double tongLuong = rs.getDouble("tong_luong");
                double luongTB = rs.getDouble("luong_tb");
                System.out.printf("%-15s %-12d %,-18.0f %,-18.0f%n",
                        chucVu, soLuong, tongLuong, luongTB);
            }

        } catch (SQLException e) {
            System.err.println("Loi SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                /* ignore */ }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                /* ignore */ }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                /* ignore */ }
        }
    }
}
