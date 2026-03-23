import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ChiPhiFrame extends JDialog {

    JTable table;
    DefaultTableModel model;
    DB db = new DB();
    int diaDiemId;

    public ChiPhiFrame(JFrame parent, int diaDiemId){

        super(parent, "Chi phí", true);

        this.diaDiemId = diaDiemId;

        setSize(500,400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(
                new String[]{"ID","Tên","Giá","Số lượng"}, 0
        ){
            public boolean isCellEditable(int r, int c){ return false; }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel p = new JPanel();

        JButton them = new JButton("Thêm");
        JButton sua = new JButton("Sửa");
        JButton xoa = new JButton("Xóa");

        p.add(them);
        p.add(sua);
        p.add(xoa);

        add(p, BorderLayout.SOUTH);

        them.addActionListener(e -> them());
        sua.addActionListener(e -> sua());
        xoa.addActionListener(e -> xoa());

        load();
    }

    void load(){
        model.setRowCount(0);
        ArrayList<String[]> list = db.getChiPhiByDiaDiem(diaDiemId);
        for(String[] r : list){
            model.addRow(r);
        }
    }

    void them(){
        JTextField ten = new JTextField();
        JTextField gia = new JTextField();
        JTextField sl = new JTextField();

        Object[] msg = {"Tên:",ten,"Giá:",gia,"SL:",sl};

        if(JOptionPane.showConfirmDialog(this,msg)==0){
            try{
                db.insertChiPhi(diaDiemId,ten.getText(),
                        Double.parseDouble(gia.getText()),
                        Integer.parseInt(sl.getText()));
                load();
            }catch(Exception e){
                JOptionPane.showMessageDialog(this,"Lỗi!");
            }
        }
    }

    void sua(){
        int r = table.getSelectedRow();
        if(r==-1) return;

        int id = Integer.parseInt(model.getValueAt(r,0).toString());

        JTextField ten = new JTextField(model.getValueAt(r,1).toString());
        JTextField gia = new JTextField(model.getValueAt(r,2).toString());
        JTextField sl = new JTextField(model.getValueAt(r,3).toString());

        Object[] msg = {"Tên:",ten,"Giá:",gia,"SL:",sl};

        if(JOptionPane.showConfirmDialog(this,msg)==0){
            try{
                db.updateChiPhi(id,ten.getText(),
                        Double.parseDouble(gia.getText()),
                        Integer.parseInt(sl.getText()));
                load();
            }catch(Exception e){
                JOptionPane.showMessageDialog(this,"Lỗi!");
            }
        }
    }

    void xoa(){
        int r = table.getSelectedRow();
        if(r==-1) return;

        int id = Integer.parseInt(model.getValueAt(r,0).toString());

        if(JOptionPane.showConfirmDialog(this,"Xóa?")==0){
            try{
                db.deleteChiPhi(id);
                load();
            }catch(Exception e){
                JOptionPane.showMessageDialog(this,"Lỗi!");
            }
        }
    }
}