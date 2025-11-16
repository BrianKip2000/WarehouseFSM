package com.warehouse.fsm;

/**
 * Demo class to demonstrate the WarehouseFSM functionality
 */
public class WarehouseFSMDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Warehouse Finite State Machine Demo ===\n");
        
        // Create a new FSM instance
        WarehouseFSM fsm = new WarehouseFSM();
        
        System.out.println("Initial State: " + fsm.getCurrentState() + "\n");
        
        // Scenario 1: Receiving and storing inventory
        System.out.println("--- Scenario 1: Receiving and Storing Inventory ---");
        fsm.trigger(Event.START_RECEIVING);
        fsm.trigger(Event.FINISH_RECEIVING);
        fsm.trigger(Event.FINISH_STORING);
        System.out.println();
        
        // Scenario 2: Processing an order (picking, packing, shipping)
        System.out.println("--- Scenario 2: Processing an Order ---");
        fsm.trigger(Event.START_PICKING);
        fsm.trigger(Event.FINISH_PICKING);
        fsm.trigger(Event.FINISH_PACKING);
        fsm.trigger(Event.FINISH_SHIPPING);
        System.out.println();
        
        // Reset to idle
        System.out.println("--- Resetting FSM ---");
        fsm.trigger(Event.RESET);
        System.out.println();
        
        // Scenario 3: Receiving, storing, then immediately picking
        System.out.println("--- Scenario 3: Receive, Store, and Pick ---");
        fsm.trigger(Event.START_RECEIVING);
        fsm.trigger(Event.FINISH_RECEIVING);
        fsm.trigger(Event.START_PICKING);
        fsm.trigger(Event.FINISH_PICKING);
        fsm.trigger(Event.FINISH_PACKING);
        fsm.trigger(Event.FINISH_SHIPPING);
        System.out.println();
        
        // Show transition history
        System.out.println("--- Transition History ---");
        for (String transition : fsm.getTransitionHistory()) {
            System.out.println(transition);
        }
        System.out.println();
        
        // Demonstrate invalid transition
        System.out.println("--- Attempting Invalid Transition ---");
        fsm.reset();
        fsm.trigger(Event.FINISH_PACKING); // Invalid: can't pack from IDLE
        System.out.println();
        
        // Show valid events from current state
        System.out.println("--- Valid Events from Current State (" + fsm.getCurrentState() + ") ---");
        for (Event event : fsm.getValidEvents()) {
            System.out.println("  - " + event);
        }
    }
}
