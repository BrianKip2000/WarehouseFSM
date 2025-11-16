
import gui.utils.Dialogs;
import javax.swing.*;
import java.awt.*;

public class ManagerPanel extends JPanel implements WarehouseState {
    private final WarehouseContext ctx;
    private JTable table;

    public ManagerPanel(WarehouseContext ctx) {
        this.ctx = ctx;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        btns.add(btn("Add Product", this::addProduct));
        btns.add(btn("Receive Shipment", this::receiveShipment));
        btns.add(btn("Show All", this::refresh));
        add(btns, BorderLayout.SOUTH);

        add(btnPanel("Become Clerk", () -> ctx.setState(WarehouseContext.CLERK_STATE)), BorderLayout.NORTH);
    }

    private JButton btn(String t, Runnable a) { JButton b = new JButton(t); b.addActionListener(e -> a.run()); return b; }
    private JPanel btnPanel(String t, Runnable a) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(btn(t, a)); return p;
    }

    private void addProduct() {
        String name = Dialogs.input("Product name:", "Add Product");
        if (name == null) return;
        int qty = Dialogs.inputInt("Initial stock:", "Add Product");
        if (qty < 0) return;
        double price = Dialogs.inputDouble("Unit price:", "Add Product");
        if (price < 0) return;
        String id = ctx.warehouse().addProduct(name, qty, price);
        Dialogs.info("Product created: " + id);
        refresh();
    }

    private void receiveShipment() {
        String pid = Dialogs.input("Product ID:", "Receive Shipment");
        if (pid == null) return;
        int qty = Dialogs.inputInt("Quantity received:", "Receive Shipment");
        if (qty <= 0) return;
        try {
            ctx.warehouse().receiveShipment(pid.trim().toUpperCase(), qty);
            Dialogs.info("Shipment processed.");
            refresh();
        } catch (Exception ex) { Dialogs.error(ex.getMessage()); }
    }

    private void refresh() {
        java.util.List<Product> list = new java.util.ArrayList<>(ctx.warehouse().getAllProducts());
        String[][] data = new String[list.size()][4];
        String[] cols = {"ID", "Name", "Stock", "Price"};
        for (int i = 0; i < list.size(); i++) {
            Product p = list.get(i);
            data[i][0] = p.getId();
            data[i][1] = p.getName();
            data[i][2] = String.valueOf(p.getStock());
            data[i][3] = String.format("%.2f", p.getPrice());
        }
        table.setModel(new javax.swing.table.DefaultTableModel(data, cols));
    }

    @Override public void run() { refresh(); ctx.changePanel(this, "Manager"); }
    @Override public String getName() { return "Manager"; }
}