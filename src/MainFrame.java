import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MainFrame extends JFrame {

    private JTable tableDiaDiem;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtGiaTu, txtGiaDen;
    private DB db = new DB();

    // Màu sắc chủ đạo (Brand Colors)
    private final Color PRIMARY_COLOR = new Color(63, 81, 181); // Blue Indigo

    public MainFrame() {
        initUI();
        initEvents();
        loadDiaDiem();
    }

    private void initUI() {
        setTitle("Hệ thống quản lý danh sách địa điểm giải trí tại Hà Nội");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Panel chính với Padding lớn để tạo khoảng trống
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // --- HEADER AREA ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Danh sách điểm đến hot nhất");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(33, 33, 33));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // --- FILTER AREA (Gọn gàng hơn) ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);

        txtGiaTu = new JTextField(8);
        txtGiaDen = new JTextField(8);

        // Custom Placeholder (Gợi ý nhập liệu)
        txtGiaTu.putClientProperty("JTextField.placeholderText", "Giá từ...");
        txtGiaDen.putClientProperty("JTextField.placeholderText", "đến...");

        JButton btnLoc = new JButton("Tìm kiếm");
        btnLoc.setBackground(PRIMARY_COLOR);
        btnLoc.setForeground(Color.WHITE);
        btnLoc.putClientProperty("JButton.buttonType", "roundRect"); // Bo góc nút

        JButton btnReset = new JButton("Làm mới");
        btnReset.putClientProperty("JButton.buttonType", "roundRect");

        filterPanel.add(new JLabel("Ngân sách:"));
        filterPanel.add(txtGiaTu);
        filterPanel.add(txtGiaDen);
        filterPanel.add(btnLoc);
        filterPanel.add(btnReset);

        headerPanel.add(filterPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- TABLE AREA ---
        String[] columnNames = {"ID", "Địa điểm", "Tỉnh", "Chi phí", "Ưu tiên"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableDiaDiem = new JTable(model);
        sorter = new TableRowSorter<>(model);
        tableDiaDiem.setRowSorter(sorter);

        // Styling cho Table
        tableDiaDiem.setRowHeight(40); // Dòng cao hơn nhìn sang hơn
        tableDiaDiem.setShowVerticalLines(false); // Bỏ vạch kẻ dọc
        tableDiaDiem.setIntercellSpacing(new Dimension(0, 5)); // Khoảng cách giữa các dòng
        tableDiaDiem.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableDiaDiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableDiaDiem.setSelectionBackground(new Color(232, 234, 246));
        tableDiaDiem.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(tableDiaDiem);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Bỏ viền ScrollPane
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- FOOTER ---
        JLabel lblStatus = new JLabel("Double-click để xem lịch trình chi tiết");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(Color.GRAY);
        mainPanel.add(lblStatus, BorderLayout.SOUTH);

        // Event Buttons
        btnLoc.addActionListener(e -> thucHienLoc());
        btnReset.addActionListener(e -> {
            txtGiaTu.setText("");
            txtGiaDen.setText("");
            sorter.setRowFilter(null);
        });
    }

    // Đã sửa lại lỗi copy-paste ở hàm này
    private void thucHienLoc() {
        String strTu = txtGiaTu.getText().trim();
        String strDen = txtGiaDen.getText().trim();

        try {
            final double giaTu = strTu.isEmpty() ? 0 : Double.parseDouble(strTu);
            final double giaDen = strDen.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(strDen);

            RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                    try {
                        String giaStr = entry.getStringValue(3).replaceAll("[^0-9.]", "");
                        double giaHienTai = Double.parseDouble(giaStr);
                        return (giaHienTai >= giaTu && giaHienTai <= giaDen);
                    } catch (Exception e) {
                        return false;
                    }
                }
            };

            sorter.setRowFilter(filter);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chỉ nhập số vào ô ngân sách!",
                    "Lỗi nhập liệu",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initEvents() {
        tableDiaDiem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    openDetailDialog();
                }
            }
        });
    }

    // Đã bổ sung logic mở Dialog (Sử dụng đúng index khi dùng Sorter)
    private void openDetailDialog() {
        int viewRow = tableDiaDiem.getSelectedRow();
        if (viewRow == -1) return;

        try {
            int modelRow = tableDiaDiem.convertRowIndexToModel(viewRow);
            Object idValue = model.getValueAt(modelRow, 0);
            int id = Integer.parseInt(idValue.toString());

            ChiPhiFrame dialog = new ChiPhiFrame(this, id);
            dialog.setVisible(true);

            loadDiaDiem();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi mở chi tiết: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadDiaDiem() {
        model.setRowCount(0);
        try {
            ArrayList<String[]> list = db.getDiaDiem();
            if (list != null) {
                for (String[] row : list) {
                    model.addRow(row);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi load dữ liệu: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 15);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}