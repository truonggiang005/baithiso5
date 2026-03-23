import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainFrame extends JFrame {

    private JTable tableDiaDiem;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtGiaTu, txtGiaDen;
    private DB db = new DB();

    private boolean tangDan = true;

    public MainFrame() {
        initUI();
        loadDiaDiem();
    }

    private void initUI() {
        setTitle("Quản lý địa điểm Hà Nội");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(main);

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Danh sách địa điểm");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(title, BorderLayout.WEST);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        txtGiaTu = new JTextField(6);
        txtGiaDen = new JTextField(6);

        JButton btnLoc = new JButton("Lọc");
        JButton btnReset = new JButton("Reset");
        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnSort = new JButton("Ưu tiên ↑");
        JButton btnJoin = new JButton("Tổng chi phí");
        JButton btnXem = new JButton("Xem");

        panel.add(new JLabel("Giá:"));
        panel.add(txtGiaTu);
        panel.add(txtGiaDen);
        panel.add(btnLoc);
        panel.add(btnReset);
        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnSort);
        panel.add(btnJoin);
        panel.add(btnXem);

        header.add(panel, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] cols = {"ID", "Tên", "Tỉnh", "Chi phí", "Ưu tiên", "Tổng"};

        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tableDiaDiem = new JTable(model);
        sorter = new TableRowSorter<>(model);
        tableDiaDiem.setRowSorter(sorter);

        main.add(new JScrollPane(tableDiaDiem), BorderLayout.CENTER);

        // ===== EVENTS =====
        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> sua());
        btnXoa.addActionListener(e -> xoa());
        btnXem.addActionListener(e -> xemChiTiet());

        btnLoc.addActionListener(e -> loc());

        btnReset.addActionListener(e -> {
            txtGiaTu.setText("");
            txtGiaDen.setText("");
            sorter.setRowFilter(null);
        });

        btnSort.addActionListener(e -> {
            sorter.setSortKeys(List.of(
                    new RowSorter.SortKey(4,
                            tangDan ? SortOrder.ASCENDING : SortOrder.DESCENDING)
            ));
            tangDan = !tangDan;
            btnSort.setText(tangDan ? "Ưu tiên ↑" : "Ưu tiên ↓");
        });

        // ✅ NÚT TỔNG CHI PHÍ
        btnJoin.addActionListener(e -> loadJoin());

        // ✅ DOUBLE CLICK → POPUP CRUD
        tableDiaDiem.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {

                    int row = tableDiaDiem.getSelectedRow();
                    if (row == -1) return;

                    int m = tableDiaDiem.convertRowIndexToModel(row);
                    int id = Integer.parseInt(model.getValueAt(m, 0).toString());

                    new ChiPhiFrame(MainFrame.this, id).setVisible(true);
                }
            }
        });
    }

    // ================= CRUD =================

    private void them() {
        JTextField ten = new JTextField();
        JTextField tinh = new JTextField();
        JTextField gia = new JTextField();
        JTextField uuTien = new JTextField();

        Object[] msg = {"Tên:", ten, "Tỉnh:", tinh, "Chi phí:", gia, "Ưu tiên:", uuTien};

        if (JOptionPane.showConfirmDialog(this, msg, "Thêm",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                db.insertDiaDiem(
                        ten.getText(),
                        tinh.getText(),
                        Double.parseDouble(gia.getText()),
                        Integer.parseInt(uuTien.getText())
                );
                loadDiaDiem();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }

    private void sua() {
        int row = tableDiaDiem.getSelectedRow();
        if (row == -1) return;

        int m = tableDiaDiem.convertRowIndexToModel(row);
        int id = Integer.parseInt(model.getValueAt(m, 0).toString());

        JTextField ten = new JTextField(model.getValueAt(m, 1).toString());
        JTextField tinh = new JTextField(model.getValueAt(m, 2).toString());
        JTextField gia = new JTextField(model.getValueAt(m, 3).toString());
        JTextField uuTien = new JTextField(model.getValueAt(m, 4).toString());

        Object[] msg = {"Tên:", ten, "Tỉnh:", tinh, "Chi phí:", gia, "Ưu tiên:", uuTien};

        if (JOptionPane.showConfirmDialog(this, msg, "Sửa",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                db.updateDiaDiem(
                        id,
                        ten.getText(),
                        tinh.getText(),
                        Double.parseDouble(gia.getText()),
                        Integer.parseInt(uuTien.getText())
                );
                loadDiaDiem();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }

    private void xoa() {
        int row = tableDiaDiem.getSelectedRow();
        if (row == -1) return;

        int m = tableDiaDiem.convertRowIndexToModel(row);
        int id = Integer.parseInt(model.getValueAt(m, 0).toString());

        if (JOptionPane.showConfirmDialog(this, "Xóa?",
                "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                db.deleteDiaDiem(id);
                loadDiaDiem();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }

    // ================= FILTER =================

    private void loc() {
        try {
            double tu = txtGiaTu.getText().isEmpty() ? 0 : Double.parseDouble(txtGiaTu.getText());
            double den = txtGiaDen.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(txtGiaDen.getText());

            sorter.setRowFilter(new RowFilter<>() {
                public boolean include(Entry entry) {
                    double gia = Double.parseDouble(entry.getStringValue(3));
                    return gia >= tu && gia <= den;
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Nhập sai!");
        }
    }

    // ================= XEM CHI TIẾT =================

    private void xemChiTiet() {
        int row = tableDiaDiem.getSelectedRow();
        if (row == -1) return;

        int m = tableDiaDiem.convertRowIndexToModel(row);

        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));

        p.add(new JLabel("Tên:"));
        p.add(new JLabel(model.getValueAt(m, 1).toString()));

        p.add(new JLabel("Tỉnh:"));
        p.add(new JLabel(model.getValueAt(m, 2).toString()));

        p.add(new JLabel("Chi phí:"));
        p.add(new JLabel(model.getValueAt(m, 3).toString()));

        p.add(new JLabel("Ưu tiên:"));
        p.add(new JLabel(model.getValueAt(m, 4).toString()));

        p.add(new JLabel("Tổng:"));
        p.add(new JLabel(model.getValueAt(m, 5).toString()));

        JOptionPane.showMessageDialog(this, p);
    }

    // ================= LOAD =================

    private void loadDiaDiem() {
        model.setRowCount(0);
        for (String[] r : db.getDiaDiem()) {
            model.addRow(new Object[]{
                    r[0], r[1], r[2], r[3], r[4], "0"
            });
        }
    }

    private void loadJoin() {
        model.setRowCount(0);
        for (String[] r : db.getDiaDiemWithTongChiPhi()) {
            model.addRow(r);
        }
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}