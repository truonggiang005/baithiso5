import java.sql.*;
import java.util.ArrayList;

public class DB {

    private final String url = "jdbc:mysql://localhost:3306/dulichdb";
    private final String user = "root";
    private final String password = "";

    // ===== LẤY ĐỊA ĐIỂM =====
    public ArrayList<String[]> getDiaDiem() {
        ArrayList<String[]> list = new ArrayList<>();

        String sql = "SELECT * FROM DiaDiem";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("id"),
                        rs.getString("tenDiaDiem"),
                        rs.getString("tinhThanh"),
                        rs.getString("chiPhiDuKien"),
                        rs.getString("mucDoUuTien"),
                        "0"
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== FIX QUAN TRỌNG: PHẢI CÓ ID =====
    public ArrayList<String[]> getChiPhiByDiaDiem(int diaDiemId) {

        ArrayList<String[]> list = new ArrayList<>();

        String sql = "SELECT id, tenDo, gia, soLuong FROM ChiPhi WHERE diaDiemId = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, diaDiemId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("id"),
                        rs.getString("tenDo"),
                        rs.getString("gia"),
                        rs.getString("soLuong")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===== CRUD ĐỊA ĐIỂM =====
    public void insertDiaDiem(String ten, String tinh, double chiPhi, int uuTien) throws Exception {
        String sql = "INSERT INTO DiaDiem(tenDiaDiem, tinhThanh, chiPhiDuKien, mucDoUuTien) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ten);
            ps.setString(2, tinh);
            ps.setDouble(3, chiPhi);
            ps.setInt(4, uuTien);
            ps.executeUpdate();
        }
    }

    public void updateDiaDiem(int id, String ten, String tinh, double chiPhi, int uuTien) throws Exception {
        String sql = "UPDATE DiaDiem SET tenDiaDiem=?, tinhThanh=?, chiPhiDuKien=?, mucDoUuTien=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ten);
            ps.setString(2, tinh);
            ps.setDouble(3, chiPhi);
            ps.setInt(4, uuTien);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
    }

    public void deleteDiaDiem(int id) throws Exception {
        String sql = "DELETE FROM DiaDiem WHERE id=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ===== CRUD CHI PHÍ =====
    public void insertChiPhi(int diaDiemId, String tenDo, double gia, int soLuong) throws Exception {
        String sql = "INSERT INTO ChiPhi(diaDiemId, tenDo, gia, soLuong) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, diaDiemId);
            ps.setString(2, tenDo);
            ps.setDouble(3, gia);
            ps.setInt(4, soLuong);
            ps.executeUpdate();
        }
    }

    public void updateChiPhi(int id, String tenDo, double gia, int soLuong) throws Exception {
        String sql = "UPDATE ChiPhi SET tenDo=?, gia=?, soLuong=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDo);
            ps.setDouble(2, gia);
            ps.setInt(3, soLuong);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    public void deleteChiPhi(int id) throws Exception {
        String sql = "DELETE FROM ChiPhi WHERE id=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    public ArrayList<String[]> getDiaDiemWithTongChiPhi() {

        ArrayList<String[]> list = new ArrayList<>();

        String sql = """
        SELECT d.id, d.tenDiaDiem, d.tinhThanh, d.chiPhiDuKien,
               d.mucDoUuTien,
               IFNULL(SUM(c.gia * c.soLuong), 0) AS tongChiPhi
        FROM DiaDiem d
        LEFT JOIN ChiPhi c ON d.id = c.diaDiemId
        GROUP BY d.id
    """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("id"),
                        rs.getString("tenDiaDiem"),
                        rs.getString("tinhThanh"),
                        rs.getString("chiPhiDuKien"),
                        rs.getString("mucDoUuTien"),
                        rs.getString("tongChiPhi") // 👉 QUAN TRỌNG
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}