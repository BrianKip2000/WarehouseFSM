import java.util.Scanner;

public class WarehouseContext {
  public static final int OPENING_STATE = 0;
  public static final int CLIENT_STATE  = 1;
  public static final int CLERK_STATE   = 2;
  public static final int MANAGER_STATE = 3;
  public static final int EXIT_STATE    = 4;

  public static final int CMD_QUIT_OR_LOGOUT = 0;
  public static final int CMD_CLIENT         = 1;
  public static final int CMD_CLERK          = 2;
  public static final int CMD_MANAGER        = 3;

  private static WarehouseContext singleton;

  private final Scanner in = new Scanner(System.in);
  private final Warehouse warehouse;

  private final WarehouseState[] states = new WarehouseState[5];
  private int currentStateIndex = OPENING_STATE;

  private int previousStateIndex = OPENING_STATE;
  private String currentClientId = null;

  private final int[][] transitions = new int[][]{
      /*             QUIT/LOGOUT  CLIENT         CLERK         MANAGER */
      /* OPENING */ { EXIT_STATE,  CLIENT_STATE,  CLERK_STATE,  MANAGER_STATE },
      /* CLIENT  */ { -1,          -1,            -1,           -1            },
      /* CLERK   */ { -1,          CLIENT_STATE,  -1,           -1            },
      /* MANAGER */ { -1,          -1,            CLERK_STATE,   -1            },
      /* EXIT    */ { EXIT_STATE,  EXIT_STATE,    EXIT_STATE,    EXIT_STATE    }
  };

  private WarehouseContext() {
    this.warehouse = new Warehouse(); 

    states[OPENING_STATE] = new OpeningState(this);
    states[CLIENT_STATE]  = new ClientMenuState(this);
    states[CLERK_STATE]   = new ClerkMenuState(this);
    states[MANAGER_STATE] = new ManagerMenuState(this);
  }

  public static WarehouseContext instance() {
    if (singleton == null) singleton = new WarehouseContext();
    return singleton;
  }

  public Scanner scanner() { return in; }
  public Warehouse warehouse() { return warehouse; }

  public void setCurrentClient(String clientId) { this.currentClientId = clientId; }
  public String getCurrentClient() { return currentClientId; }

  public WarehouseState getState(int index) { return states[index]; }
  public void setState(int index) { this.currentStateIndex = index; }
  public int getCurrentStateIndex() { return currentStateIndex; }

  public void setPreviousState(int index) { this.previousStateIndex = index; }
  public int getPreviousState() { return previousStateIndex; }

  public int getNextState(int current, int command) {
    if (current < 0 || current >= transitions.length) return -1;
    if (command < 0 || command >= transitions[current].length) return -1;
    return transitions[current][command];
  }

  public void logout() {
    if (currentStateIndex == CLIENT_STATE) {
      setState(previousStateIndex);
      currentClientId = null;
    } else if (currentStateIndex == CLERK_STATE || currentStateIndex == MANAGER_STATE) {
      setState(OPENING_STATE);
    } else {
      setState(EXIT_STATE);
    }
  }

  public void process() {
    System.out.println("== Warehouse (FSM, Text Version) ==");
    while (currentStateIndex != EXIT_STATE) {
      WarehouseState st = states[currentStateIndex];
      if (st == null) break;
      st.run();
    }
    System.out.println("Goodbye.");
  }

  // some input helpers
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
}
