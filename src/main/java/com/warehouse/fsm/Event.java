package com.warehouse.fsm;

/**
 * Represents an event that triggers a state transition
 */
public enum Event {
    START_RECEIVING("Start Receiving Inventory"),
    FINISH_RECEIVING("Finish Receiving Inventory"),
    START_STORING("Start Storing Items"),
    FINISH_STORING("Finish Storing Items"),
    START_PICKING("Start Picking Order"),
    FINISH_PICKING("Finish Picking Order"),
    START_PACKING("Start Packing Order"),
    FINISH_PACKING("Finish Packing Order"),
    START_SHIPPING("Start Shipping Order"),
    FINISH_SHIPPING("Finish Shipping Order"),
    RESET("Reset to Idle");

    private final String description;

    Event(String description) {
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
