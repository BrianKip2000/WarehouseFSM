import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final WarehouseContext ctx;
    private final CardLayout card = new CardLayout();
    private final JPanel cardPanel = new JPanel(card);

    public MainFrame(WarehouseContext ctx) {
        this.ctx = ctx;
        setTitle("Warehouse Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 680);
        add(cardPanel);
        setLocationRelativeTo(null);
    }

    public void changePanel(JPanel panel, String title) {
        String name = panel.getClass().getSimpleName();
        JPanel existing = findPanel(name);
        if (existing == null) {
            cardPanel.add(panel, name);
        } else {
            cardPanel.remove(existing);
            cardPanel.add(panel, name);
        }
        card.show(cardPanel, name);
        setTitle("Warehouse – " + title);
    }

    private JPanel findPanel(String name) {
        for (Component c : cardPanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c; // cast component to JPanel for older Java versions
                if (name.equals(p.getClass().getSimpleName())) { // compare simple class name
                    return p; // return the matching panel
                }
            }
        }
        return null;
    }

    public void start() {
        ctx.changePanel((JPanel) ctx.getState(WarehouseContext.OPENING_STATE), "Login");
        setVisible(true);
    }
}