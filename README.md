# WarehouseFSM

A Finite State Machine (FSM) implementation for warehouse operations using Java.

## Overview

This project implements a state machine to model and manage warehouse operations including receiving, storing, picking, packing, and shipping inventory.

## Features

- **State Management**: Track warehouse operations through distinct states
- **Event-Driven Transitions**: Transition between states using well-defined events
- **Validation**: Prevent invalid state transitions
- **History Tracking**: Keep track of all state transitions
- **Flexible Reset**: Reset to idle state from any state

## States

The FSM supports the following states:

- **IDLE**: Initial state, waiting for work
- **RECEIVING**: Receiving inventory from suppliers
- **STORING**: Storing received items in warehouse
- **PICKING**: Picking items for orders
- **PACKING**: Packing picked items
- **SHIPPING**: Shipping packed orders
- **COMPLETED**: Order fulfilled successfully

## Events

Transitions are triggered by the following events:

- `START_RECEIVING` - Begin receiving inventory
- `FINISH_RECEIVING` - Complete receiving and move to storing
- `FINISH_STORING` - Complete storing items
- `START_PICKING` - Begin picking an order
- `FINISH_PICKING` - Complete picking and move to packing
- `FINISH_PACKING` - Complete packing and move to shipping
- `FINISH_SHIPPING` - Complete shipping
- `RESET` - Reset to IDLE state from any state

## State Transitions

```
IDLE --[START_RECEIVING]--> RECEIVING --[FINISH_RECEIVING]--> STORING --[FINISH_STORING]--> IDLE
IDLE --[START_PICKING]--> PICKING --[FINISH_PICKING]--> PACKING --[FINISH_PACKING]--> SHIPPING --[FINISH_SHIPPING]--> COMPLETED
STORING --[START_PICKING]--> PICKING
COMPLETED --[RESET]--> IDLE
ANY_STATE --[RESET]--> IDLE
```

## Building the Project

This project uses Maven for build management.

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Build Commands

```bash
# Compile the project
mvn compile

# Run tests
mvn test

# Package as JAR
mvn package

# Clean build artifacts
mvn clean
```

## Running the Demo

To see the FSM in action:

```bash
# Compile and run the demo
mvn compile exec:java -Dexec.mainClass="com.warehouse.fsm.WarehouseFSMDemo"
```

Or after packaging:

```bash
java -cp target/warehouse-fsm-1.0-SNAPSHOT.jar com.warehouse.fsm.WarehouseFSMDemo
```

## Usage Example

```java
import com.warehouse.fsm.WarehouseFSM;
import com.warehouse.fsm.Event;

public class Example {
    public static void main(String[] args) {
        // Create FSM instance
        WarehouseFSM fsm = new WarehouseFSM();
        
        // Process an order
        fsm.trigger(Event.START_PICKING);
        fsm.trigger(Event.FINISH_PICKING);
        fsm.trigger(Event.FINISH_PACKING);
        fsm.trigger(Event.FINISH_SHIPPING);
        
        // Check current state
        System.out.println("Current state: " + fsm.getCurrentState());
        
        // View transition history
        for (String transition : fsm.getTransitionHistory()) {
            System.out.println(transition);
        }
        
        // Reset to idle
        fsm.reset();
    }
}
```

## Testing

The project includes comprehensive unit tests covering:

- Valid state transitions
- Invalid transition handling
- Transition history tracking
- Reset functionality
- Complete workflow scenarios

Run tests with:

```bash
mvn test
```

## Project Structure

```
warehouse-fsm/
├── src/
│   ├── main/java/com/warehouse/fsm/
│   │   ├── State.java           # State enumeration
│   │   ├── Event.java           # Event enumeration
│   │   ├── Transition.java      # Transition representation
│   │   ├── WarehouseFSM.java    # Main FSM implementation
│   │   └── WarehouseFSMDemo.java # Demo application
│   └── test/java/com/warehouse/fsm/
│       └── WarehouseFSMTest.java # Unit tests
├── pom.xml                      # Maven configuration
└── README.md                    # This file
```

## License

This project is open source and available for educational purposes.