import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class WarehouseContext {
    public static final int OPENING_STATE = 0;
    public static final int CLIENT_STATE  = 1;
    public static final int CLERK_STATE   = 2;
    public static final int MANAGER_STATE = 3;
    public static final int EXIT_STATE    = 4;
    public static final int WISHLIST_STATE = 5;

    public static final int CMD_QUIT_OR_LOGOUT = 0;
    public static final int CMD_CLIENT         = 1;
    public static final int CMD_CLERK          = 2;
    public static final int CMD_MANAGER        = 3;
    public static final int CMD_WISHLIST       = 4;

    private static WarehouseContext singleton;
    private final Warehouse warehouse = new Warehouse();
    private final WarehouseState[] states = new WarehouseState[6];
    private final Scanner in = new Scanner(System.in); // input scanner for text states
    private int currentStateIndex = OPENING_STATE;
    private int previousStateIndex = OPENING_STATE;
    private String currentClientId = null;
    private MainFrame mainFrame;

    private final int[][] transitions = {
        /*           QUIT   CLIENT   CLERK   MANAGER  WISHLIST */
        /*OPEN*/  { EXIT_STATE, CLIENT_STATE, CLERK_STATE, MANAGER_STATE, -1 },
        /*CLI */  { -1,        -1,          -1,         -1,           WISHLIST_STATE },
        /*CLK */  { -1,        CLIENT_STATE,-1,         -1,           -1 },
        /*MGR */  { -1,        -1,          CLERK_STATE,-1,           -1 },
        /*EXIT*/  { EXIT_STATE,EXIT_STATE,  EXIT_STATE, EXIT_STATE,   EXIT_STATE },
        /*WISH*/  { -1,        CLIENT_STATE,-1,         -1,           -1 }
    };

    private WarehouseContext() {
        states[OPENING_STATE] = new OpeningPanel(this); // GUI opening panel
        states[CLIENT_STATE]  = new ClientMenuPanel(this); // GUI client menu panel
        states[CLERK_STATE]   = new ClerkPanel(this); // GUI clerk panel
        states[MANAGER_STATE] = new ManagerPanel(this); // GUI manager panel
        states[WISHLIST_STATE] = null; // wishlist panel will be set when needed
    }

    public static WarehouseContext instance() {
        if (singleton == null) singleton = new WarehouseContext();
        return singleton;
    }

    public Warehouse warehouse() { return warehouse; }
    public void setCurrentClient(String id) { this.currentClientId = id; }
    public String getCurrentClient() { return currentClientId; }
    public void setPreviousState(int s) { previousStateIndex = s; }
    public int getPreviousState() { return previousStateIndex; }
    public void setState(int s) { currentStateIndex = s; }
    public int getCurrentStateIndex() { return currentStateIndex; }
    public WarehouseState getState(int i) { return states[i]; }
    public void setWishlistState(WarehouseState s) { states[WISHLIST_STATE] = s; }

    // input helpers for text-based states
    public String promptLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    public int promptInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.println("Please enter an integer."); }
        }
    }

    public double promptDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try { return Double.parseDouble(s); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid number (e.g., 12.5)."); }
        }
    }

    public int getNextState(int cur, int cmd) {
        if (cur < 0 || cur >= transitions.length) return -1;
        if (cmd < 0 || cmd >= transitions[cur].length) return -1;
        return transitions[cur][cmd];
    }

    public void logout() {
        if (currentStateIndex == CLIENT_STATE || currentStateIndex == WISHLIST_STATE) {
            setState(previousStateIndex);
            currentClientId = null;
        } else if (currentStateIndex == CLERK_STATE || currentStateIndex == MANAGER_STATE) {
            setState(OPENING_STATE);
        } else {
            setState(EXIT_STATE);
        }
    }

    public void setMainFrame(MainFrame f) { this.mainFrame = f; }
    public void changePanel(JPanel p, String title) {
        SwingUtilities.invokeLater(() -> mainFrame.changePanel(p, title));
    }

    public void process() {
        try {
            // create and show main frame on EDT and wait until it's created
            SwingUtilities.invokeAndWait(() -> {
                MainFrame frame = new MainFrame(this); // create main frame on EDT
                setMainFrame(frame); // store reference for changePanel
                frame.start(); // initialize and show frame (calls changePanel for opening)
            });
        } catch (Exception e) {
            // if EDT creation fails, print and exit
            e.printStackTrace();
            System.exit(1);
        }

        // run FSM loop on this thread; GUI updates happen on EDT via changePanel
        while (currentStateIndex != EXIT_STATE) {
            WarehouseState st = states[currentStateIndex];
            if (st != null) st.run();
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
        System.exit(0);
    }
}