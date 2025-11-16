import javax.swing.*; // import Swing UI components for GUI
import java.awt.*; // import AWT for layout and dimension classes
import java.awt.event.*; // import event classes for listeners
import java.util.List; // import List for wishlist rows

// GUI state for managing a client's wishlist and placing orders
public class WishlistState implements WarehouseState {
  private final WarehouseContext ctx; // reference to the warehouse context
  private JFrame frame; // frame to host wishlist UI
  private String clientId; // current client id being managed

  // constructor stores the context reference
  public WishlistState(WarehouseContext ctx) { this.ctx = ctx; }

  @Override public String getName() { return "WishlistState"; } // return state name

  @Override
  public void run() {
    // build and show GUI on the Swing Event Dispatch Thread
    SwingUtilities.invokeLater(() -> {
      clientId = ctx.getCurrentClient(); // read the active client id from context
      frame = new JFrame("Wishlist - Client: " + (clientId == null ? "-" : clientId)); // create window with title
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // dispose window when closed
      frame.setSize(600, 400); // set initial window size
      frame.setLayout(new BorderLayout()); // set layout manager

      // top area: label and back button
      JPanel top = new JPanel(new BorderLayout()); // container panel for header
      JLabel lbl = new JLabel("Wishlist for: " + (clientId == null ? "(none)" : clientId)); // status label
      top.add(lbl, BorderLayout.WEST); // add label to left of header
      JButton btnBack = new JButton("Back to Client Menu"); // button to return to client menu
      top.add(btnBack, BorderLayout.EAST); // add button to right of header
      frame.add(top, BorderLayout.NORTH); // add header panel to top of frame

      // center area: wishlist display in a scrollable list
      DefaultListModel<String> listModel = new DefaultListModel<>(); // model backing the JList
      JList<String> wishList = new JList<>(listModel); // list component showing wishlist rows
      JScrollPane scroll = new JScrollPane(wishList); // scroll pane wrapping the list
      frame.add(scroll, BorderLayout.CENTER); // place scroll pane in center

      // right area: controls to add/remove items and place orders
      JPanel right = new JPanel(); // panel for controls on the right
      right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS)); // vertical layout for controls

      JTextField tfProduct = new JTextField(); // text field for product id input
      tfProduct.setMaximumSize(new Dimension(200, 25)); // limit size of the product field
      tfProduct.setAlignmentX(Component.LEFT_ALIGNMENT); // align left inside box layout
      JTextField tfQty = new JTextField(); // text field for quantity input
      tfQty.setMaximumSize(new Dimension(200, 25)); // limit size of qty field
      tfQty.setAlignmentX(Component.LEFT_ALIGNMENT); // align left

      JButton btnAdd = new JButton("Add/Update Item"); // button to add or update wishlist entry
      JButton btnRemove = new JButton("Remove Selected"); // button to remove selected wishlist entry
      JButton btnPlace = new JButton("Place Order"); // button to place order for wishlist

      right.add(new JLabel("Product ID:")); // label above product field
      right.add(tfProduct); // add product text field
      right.add(new JLabel("Quantity:")); // label above quantity field
      right.add(tfQty); // add quantity text field
      right.add(Box.createRigidArea(new Dimension(0,10))); // spacer between controls
      right.add(btnAdd); // add "Add/Update" button
      right.add(Box.createRigidArea(new Dimension(0,5))); // small spacer
      right.add(btnRemove); // add "Remove" button
      right.add(Box.createRigidArea(new Dimension(0,20))); // larger spacer
      right.add(btnPlace); // add "Place Order" button

      frame.add(right, BorderLayout.EAST); // add control panel to right side of frame

      // bottom area: single-line status label
      JLabel status = new JLabel(" "); // blank status label initially
      frame.add(status, BorderLayout.SOUTH); // add status label to bottom

      // load wishlist entries into the list model
      refreshList(listModel); // populate the list model from warehouse

      // back button action: close this GUI and return to client menu
      btnBack.addActionListener(e -> {
        frame.dispose(); // close the wishlist window
        ctx.setState(WarehouseContext.CLIENT_STATE); // transition back to client menu state
      });

      // add/update action: validate inputs and update wishlist in warehouse
      btnAdd.addActionListener(e -> {
        String pid = tfProduct.getText().trim(); // read product id text
        String sq = tfQty.getText().trim(); // read quantity text
        if (clientId == null) { status.setText("No active client."); return; } // require active client
        if (pid.isEmpty() || sq.isEmpty()) { status.setText("Provide product id and qty."); return; } // require inputs
        try {
          int q = Integer.parseInt(sq); // parse quantity as integer
          ctx.warehouse().addOrUpdateWishlistItem(clientId, pid, q); // call warehouse to update wishlist
          status.setText("Added/Updated " + pid + " x" + q); // show success
          refreshList(listModel); // refresh displayed list
        } catch (NumberFormatException ex) {
          status.setText("Quantity must be an integer."); // parsing error
        } catch (Exception ex) {
          status.setText("Failed: " + ex.getMessage()); // other errors
        }
      });

      // remove action: remove the selected wishlist entry by setting qty to 0
      btnRemove.addActionListener(e -> {
        String sel = wishList.getSelectedValue(); // get currently selected list entry
        if (sel == null) { status.setText("Select an item to remove."); return; } // require selection
        // expected row format: "P1 | Name | qty=5"
        String[] parts = sel.split("\\|"); // split on pipe character
        if (parts.length < 1) { status.setText("Malformed selection."); return; } // validate format
        String pid = parts[0].trim(); // extract product id from first column
        try {
          ctx.warehouse().addOrUpdateWishlistItem(clientId, pid, 0); // set quantity to 0 to remove
          status.setText("Removed " + pid); // update status
          refreshList(listModel); // refresh list to reflect removal
        } catch (Exception ex) {
          status.setText("Failed: " + ex.getMessage()); // show errors
        }
      });

      // place order action: attempt to buy all wishlist items
      btnPlace.addActionListener(e -> {
        if (clientId == null) { status.setText("No active client."); return; } // require client
        try {
          ctx.warehouse().placeOrder(clientId); // invoke warehouse ordering logic
          status.setText("Order placed for " + clientId); // success message
          refreshList(listModel); // refresh wishlist after order
        } catch (Exception ex) {
          status.setText("Failed: " + ex.getMessage()); // show failure
        }
      });

      // finalize and show window
      frame.setLocationRelativeTo(null); // center the window on screen
      frame.setVisible(true); // display the GUI
    });

    // wait loop: keep the run method alive until state changes away from wishlist
    while (ctx.getCurrentStateIndex() == WarehouseContext.WISHLIST_STATE || ctx.getCurrentStateIndex() == WarehouseContext.CLIENT_STATE) {
      try { Thread.sleep(200); } catch (InterruptedException e) { break; } // sleep briefly to reduce CPU
      if (ctx.getCurrentStateIndex() != WarehouseContext.WISHLIST_STATE) break; // exit if state changed
    }
  }

  // helper that reloads the wishlist display from the warehouse
  private void refreshList(DefaultListModel<String> listModel) {
    listModel.clear(); // clear existing entries in the list model
    try {
      List<String> rows = ctx.warehouse().getWishlistForClient(clientId); // query wishlist rows
      if (rows == null || rows.isEmpty()) { listModel.addElement("(empty)"); return; } // show empty if none
      for (String r : rows) listModel.addElement(r); // add each row to the model
    } catch (Exception e) {
      listModel.addElement("(error: " + e.getMessage() + ")"); // display any errors
    }
  }
}
