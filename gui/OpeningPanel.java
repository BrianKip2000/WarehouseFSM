import gui.utils.Dialogs;
import javax.swing.*;
import java.awt.*;

public class OpeningPanel extends JPanel implements WarehouseState {
    private final WarehouseContext ctx;

    public OpeningPanel(WarehouseContext ctx) {
        this.ctx = ctx;
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(5, 1, 10, 10));
        add(btn("1) Login as Client",   this::loginClient));
        add(btn("2) Login as Clerk",    () -> ctx.setState(WarehouseContext.CLERK_STATE)));
        add(btn("3) Login as Manager",  () -> ctx.setState(WarehouseContext.MANAGER_STATE)));
        add(btn("0) Quit",              () -> ctx.setState(WarehouseContext.EXIT_STATE)));
    }

    private JButton btn(String text, Runnable a) {
        JButton b = new JButton(text);
        b.addActionListener(e -> a.run());
        return b;
    }

    private void loginClient() {
        String id = Dialogs.input("Enter Client ID (e.g., C1): ", "Client Login");
        if (id == null) return;
        String cid = id.trim().toUpperCase(); // normalize and store in final local
        boolean ok = ctx.warehouse().getAllClients().stream()
                .anyMatch(c -> c.getId().equalsIgnoreCase(cid)); // use final var inside lambda
        if (ok) {
            ctx.setCurrentClient(cid);
            ctx.setPreviousState(WarehouseContext.OPENING_STATE);
            ctx.setState(WarehouseContext.CLIENT_STATE);
        } else {
            Dialogs.error("Invalid client ID.");
        }
    }

    @Override public void run() { ctx.changePanel(this, "Login"); }
    @Override public String getName() { return "Opening"; }
}