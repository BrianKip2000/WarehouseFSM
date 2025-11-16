
import gui.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WishlistPanel extends JPanel implements WarehouseState {
    private final WarehouseContext ctx;
    private final String clientId;
    private JTable table;
    private DefaultTableModel model;

    public WishlistPanel(WarehouseContext ctx) {
        this.ctx = ctx;
        this.clientId = ctx.getCurrentClient();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"Product ID", "Name", "Qty"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        btns.add(btn("Add/Update", this::addItem));
        btns.add(btn("Delete Selected", this::deleteSelected));
        btns.add(btn("Refresh", this::refresh));
        add(btns, BorderLayout.SOUTH);

        add(btnPanel("← Back", () -> ctx.setState(WarehouseContext.CLIENT_STATE)), BorderLayout.NORTH);
        refresh();
    }

    private JButton btn(String t, Runnable a) {
        JButton b = new JButton(t); b.addActionListener(e -> a.run()); return b;
    }
    private JPanel btnPanel(String t, Runnable a) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(btn(t, a)); return p;
    }

    private void refresh() {
        model.setRowCount(0);
        Client c = ctx.warehouse().getAllClients().stream()
                .filter(cl -> cl.getId().equals(clientId))
                .findFirst().orElse(null);
        if (c == null) return;

        for (WishlistItem wi : c.getWishlist()) {
            Product p = ctx.warehouse().getAllProducts().stream()
                    .filter(pr -> pr.getId().equals(wi.getProductId()))
                    .findFirst().orElse(null);
            String name = p != null ? p.getName() : "(unknown)";
            model.addRow(new Object[]{wi.getProductId(), name, wi.getQuantity()});
        }
    }

    private void addItem() {
        String pid = Dialogs.input("Product ID:", "Add to Wishlist");
        if (pid == null) return;
        int qty = Dialogs.inputInt("Quantity:", "Add to Wishlist");
        if (qty <= 0) return;
        try {
            ctx.warehouse().addOrUpdateWishlistItem(clientId, pid.trim().toUpperCase(), qty);
            refresh();
        } catch (Exception ex) { Dialogs.error(ex.getMessage()); }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) { Dialogs.info("Select a row."); return; }
        String pid = (String) model.getValueAt(row, 0);
        ctx.warehouse().addOrUpdateWishlistItem(clientId, pid, 0);
        refresh();
    }

    @Override public void run() { ctx.changePanel(this, "Wishlist – " + clientId); }
    @Override public String getName() { return "Wishlist"; }
}