import java.sql.*;
import java.util.ArrayList;

public class DB {

    String url = "jdbc:mysql://localhost:3306/dulichdb";
    String user = "root";
    String password = "";

    // =========================
    // 1. LẤY DANH SÁCH ĐỊA ĐIỂM
    // =========================
    public ArrayList<String[]> getDiaDiem(){

        ArrayList<String[]> list = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM diadiem")) {

            while(rs.next()){
                list.add(new String[]{
                        rs.getString("id"),
                        rs.getString("tenDiaDiem"),
                        rs.getString("tinhThanh"),
                        rs.getString("chiPhiDuKien"),
                        rs.getString("mucDoUuTien")
                });
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // 2. LẤY CHI PHÍ THEO ĐỊA ĐIỂM
    // =========================
    public ArrayList<String[]> getChiPhiByDiaDiem(int diaDiemId){

        ArrayList<String[]> list = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT tenDo, gia, soLuong FROM ChiPhi WHERE diaDiemId = ?")) {

            ps.setInt(1, diaDiemId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                list.add(new String[]{
                        rs.getString("tenDo"),
                        rs.getString("gia"),
                        rs.getString("soLuong")
                });
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // 3. THÊM ĐỊA ĐIỂM
    // =========================
    public boolean insertDiaDiem(String ten, String tinh, double chiPhi, int uuTien){

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO DiaDiem(tenDiaDiem, tinhThanh, chiPhiDuKien, mucDoUuTien) VALUES (?, ?, ?, ?)")) {

            ps.setString(1, ten);
            ps.setString(2, tinh);
            ps.setDouble(3, chiPhi);
            ps.setInt(4, uuTien);

            return ps.executeUpdate() > 0;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // 4. THÊM CHI PHÍ
    // =========================
    public boolean insertChiPhi(int diaDiemId, String tenDo, double gia, int soLuong){

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO ChiPhi(diaDiemId, tenDo, gia, soLuong) VALUES (?, ?, ?, ?)")) {

            ps.setInt(1, diaDiemId);
            ps.setString(2, tenDo);
            ps.setDouble(3, gia);
            ps.setInt(4, soLuong);

            return ps.executeUpdate() > 0;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}