import gui.utils.Dialogs;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClerkPanel extends JPanel implements WarehouseState {
    private final WarehouseContext ctx;
    private JTable clientTable;
    private DefaultTableModel clientModel;

    public ClerkPanel(WarehouseContext ctx) {
        this.ctx = ctx;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();

        // === CLIENTS TAB ===
        JPanel clientPanel = new JPanel(new BorderLayout());
        clientModel = new DefaultTableModel(new String[]{"ID", "Name", "Address", "Balance"}, 0);
        clientTable = new JTable(clientModel);
        clientPanel.add(new JScrollPane(clientTable), BorderLayout.CENTER);

        JPanel cBtns = new JPanel();
        cBtns.add(btn("Add Client", this::addClient));
        cBtns.add(btn("Show All", this::showAllClients));
        cBtns.add(btn("Show With Balance", this::showClientsWithBalance));
        cBtns.add(btn("Record Payment", this::recordPayment));
        cBtns.add(btn("Become Client", this::becomeClient));
        clientPanel.add(cBtns, BorderLayout.SOUTH);
        tabs.addTab("Clients", clientPanel);

        add(tabs, BorderLayout.CENTER);
        add(btnPanel("Logout", ctx::logout), BorderLayout.NORTH);
    }

    private JButton btn(String text, Runnable action) {
        JButton b = new JButton(text);
        b.addActionListener(e -> action.run());
        return b;
    }

    private JPanel btnPanel(String text, Runnable action) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(btn(text, action));
        return p;
    }

    // ====================== ADD CLIENT ======================
    private void addClient() {
        String name = Dialogs.input("Client name:", "Add Client");
        if (name == null || name.trim().isEmpty()) return;
        String addr = Dialogs.input("Address:", "Add Client");
        if (addr == null || addr.trim().isEmpty()) return;

        String id = ctx.warehouse().addClient(name.trim(), addr.trim());
        Dialogs.info("Client created: " + id);
        showAllClients();
    }

    private void showAllClients() {
        clientModel.setRowCount(0);
        for (Client c : ctx.warehouse().getAllClients()) {
            clientModel.addRow(new Object[]{
                c.getId(),
                c.getName(),
                c.getAddress(),
                String.format("%.2f", c.getBalance())
            });
        }
    }

    private void showClientsWithBalance() {
        clientModel.setRowCount(0);
        for (Client c : ctx.warehouse().getAllClients()) {
            if (c.getBalance() > 0.01) {
                clientModel.addRow(new Object[]{
                    c.getId(), c.getName(), c.getAddress(),
                    String.format("%.2f", c.getBalance())
                });
            }
        }
    }

    private void recordPayment() {
        String id = Dialogs.input("Client ID:", "Record Payment");
        if (id == null) return;
        double amt = Dialogs.inputDouble("Amount:", "Record Payment");
        if (amt <= 0) return;
        try {
            ctx.warehouse().recordPayment(id.trim().toUpperCase(), amt);
            Dialogs.info("Payment recorded.");
            showAllClients();
        } catch (Exception ex) {
            Dialogs.error(ex.getMessage());
        }
    }

    private void becomeClient() {
        String id = Dialogs.input("Client ID:", "Become Client");
        if (id == null) return;
        String cid = id.trim().toUpperCase(); // normalize input and keep final for lambda
        boolean exists = ctx.warehouse().getAllClients().stream()
                .anyMatch(c -> c.getId().equalsIgnoreCase(cid));
        if (exists) {
            ctx.setCurrentClient(cid);
            ctx.setPreviousState(WarehouseContext.CLERK_STATE);
            ctx.setState(WarehouseContext.CLIENT_STATE);
        } else {
            Dialogs.error("Client not found.");
        }
    }

    @Override public void run() {
        showAllClients();
        ctx.changePanel(this, "Clerk");
    }

    @Override public String getName() { return "Clerk"; }
}