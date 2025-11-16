import gui.utils.Dialogs;
import javax.swing.*;
import java.awt.*;

public class ClientMenuPanel extends JPanel implements WarehouseState {
    private final WarehouseContext ctx;

    public ClientMenuPanel(WarehouseContext ctx) {
        this.ctx = ctx;
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(4, 1, 10, 20));
        JLabel title = new JLabel("Client: " + ctx.getCurrentClient(), SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(title);

        add(btn("Wishlist Operations →", () -> {
            WishlistPanel wp = new WishlistPanel(ctx);
            ctx.setWishlistState(wp);
            ctx.setState(WarehouseContext.WISHLIST_STATE);
        }));
        add(btn("Place Order (Buy Wishlist)", this::placeOrder));
        add(btn("Logout", ctx::logout));
    }

    private JButton btn(String text, Runnable a) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(350, 55));
        b.addActionListener(e -> a.run());
        return b;
    }

    private void placeOrder() {
        try {
            ctx.warehouse().placeOrder(ctx.getCurrentClient());
            Dialogs.info("Order placed! Check balance in Clerk view.");
        } catch (Exception ex) {
            Dialogs.error(ex.getMessage());
        }
    }

    @Override public void run() { ctx.changePanel(this, "Client Menu"); }
    @Override public String getName() { return "ClientMenu"; }
}