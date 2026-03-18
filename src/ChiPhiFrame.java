import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ChiPhiFrame extends JDialog {

    JTable table;
    DefaultTableModel model;
    DB db = new DB();

    public ChiPhiFrame(JFrame parent, int diaDiemId){

        super(parent, "Chi phí", true);

        setSize(400,300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(){
            public boolean isCellEditable(int r, int c){ return false; }
        };

        model.addColumn("Tên");
        model.addColumn("Giá");
        model.addColumn("Số lượng");

        table = new JTable(model);

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadChiPhi(diaDiemId);
    }

    void loadChiPhi(int id){
        model.setRowCount(0);

        ArrayList<String[]> list = db.getChiPhiByDiaDiem(id);

        for(String[] row : list){
            model.addRow(row);
        }
    }
}