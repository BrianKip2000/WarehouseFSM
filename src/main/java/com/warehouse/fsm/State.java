package com.warehouse.fsm;

/**
 * Represents a state in the Finite State Machine
 */
public enum State {
    IDLE("Idle"),
    RECEIVING("Receiving Inventory"),
    STORING("Storing Items"),
    PICKING("Picking Orders"),
    PACKING("Packing Orders"),
    SHIPPING("Shipping Orders"),
    COMPLETED("Completed");

    private final String description;

    State(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
