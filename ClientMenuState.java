import javax.swing.*; // import Swing components
import java.awt.*; // import AWT classes for layout/dimensions
import java.awt.event.*; // import event classes for listeners
import java.util.List; // import List to handle collections

// ClientMenuState converted to a simple GUI that delegates wishlist operations
public class ClientMenuState implements WarehouseState {
  private final WarehouseContext ctx; // reference to shared application context

  // constructor receives and stores the shared context
  public ClientMenuState(WarehouseContext ctx) { this.ctx = ctx; }

  @Override public String getName() { return "ClientMenuState"; } // return state name

  @Override
  public void run() {
    SwingUtilities.invokeLater(() -> showGui()); // schedule GUI construction on EDT

    // keep the state active until context state changes away from CLIENT_STATE
    while (ctx.getCurrentStateIndex() == WarehouseContext.CLIENT_STATE) {
      try { Thread.sleep(200); } catch (InterruptedException e) { break; } // small sleep to avoid busy loop
    }
  }

  // build and show the client menu GUI
  private void showGui() {
    String cid = ctx.getCurrentClient(); // fetch current client id from context
    JFrame frame = new JFrame("Client Menu - " + (cid == null ? "(none)" : cid)); // create frame with title
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // dispose frame on close
    frame.setSize(600, 400); // set window size
    frame.setLayout(new BorderLayout()); // use border layout manager

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT)); // top panel showing client id
    top.add(new JLabel("Client: " + (cid == null ? "(none)" : cid))); // label for client
    frame.add(top, BorderLayout.NORTH); // add top panel to frame

    DefaultListModel<String> listModel = new DefaultListModel<>(); // model for options list
    JList<String> list = new JList<>(listModel); // list UI component
    listModel.addElement("Show client details"); // option 0
    listModel.addElement("Show products (price)"); // option 1
    listModel.addElement("Show transactions"); // option 2
    listModel.addElement("Wishlist operations"); // option 3 - transitions to wishlist
    listModel.addElement("Place order (buy wishlist)"); // option 4
    listModel.addElement("Logout"); // option 5

    frame.add(new JScrollPane(list), BorderLayout.CENTER); // put list in center with scroll

    JLabel status = new JLabel(" "); // status line at bottom
    frame.add(status, BorderLayout.SOUTH); // add status to frame

    // double-click handler to select an option
    list.addMouseListener(new MouseAdapter() {
      @Override public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() != 2) return; // require double-click to activate
        int idx = list.getSelectedIndex(); // read selected index
        handleChoice(idx, status, frame); // process choice
      }
    });

    frame.setLocationRelativeTo(null); // center frame on screen
    frame.setVisible(true); // show the GUI
  }

  // handle the user's choice from the list
  private void handleChoice(int choice, JLabel status, JFrame frame) {
    String cid = ctx.getCurrentClient(); // refresh current client id
    switch (choice) {
      case 0: // show client details
        if (cid == null) { status.setText("No active client."); return; } // require client
        for (Client c : ctx.warehouse().getAllClients()) if (c.getId().equalsIgnoreCase(cid)) { status.setText(c.toString()); return; } // display client
        status.setText("Client not found."); // not found
        break;
      case 1: // show products list
        StringBuilder sb = new StringBuilder(); // build a display string
        for (Product p : ctx.warehouse().getAllProducts()) sb.append(p.toString()).append(" | "); // append products
        status.setText(sb.length() == 0 ? "(no products)" : sb.toString()); // set status with products
        break;
      case 2: // show transactions
        if (cid == null) { status.setText("No active client."); return; } // require client
        try {
          List<Transaction> txs = ctx.warehouse().getTransactionsForClient(cid); // fetch transactions
          StringBuilder s = new StringBuilder(); // builder for transactions
          for (Transaction t : txs) s.append(t.toString()).append(" | "); // append each
          status.setText(s.length() == 0 ? "(no transactions)" : s.toString()); // display
        } catch (Exception e) { status.setText("Failed: " + e.getMessage()); } // show error
        break;
      case 3: // wishlist operations
        if (cid == null) { status.setText("No active client."); return; } // require client
        ctx.setPreviousState(WarehouseContext.CLIENT_STATE); // record previous state
        ctx.setState(WarehouseContext.WISHLIST_STATE); // transition FSM to wishlist state
        frame.dispose(); // close the client menu GUI
        break;
      case 4: // place order for wishlist
        if (cid == null) { status.setText("No active client."); return; } // require client
        try { ctx.warehouse().placeOrder(cid); status.setText("Order placed for " + cid); } // attempt order
        catch (Exception e) { status.setText("Failed: " + e.getMessage()); } // show error
        break;
      case 5: // logout
        ctx.logout(); // perform logout via context
        frame.dispose(); // close GUI window
        break;
      default: // invalid choice
        status.setText("Invalid selection."); // show message
    }
  }
}
