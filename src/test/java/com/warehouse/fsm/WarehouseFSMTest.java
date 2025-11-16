package com.warehouse.fsm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WarehouseFSM
 */
class WarehouseFSMTest {
    
    private WarehouseFSM fsm;
    
    @BeforeEach
    void setUp() {
        fsm = new WarehouseFSM();
    }
    
    @Test
    @DisplayName("Initial state should be IDLE")
    void testInitialState() {
        assertEquals(State.IDLE, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("Valid transition from IDLE to RECEIVING")
    void testIdleToReceiving() {
        assertTrue(fsm.trigger(Event.START_RECEIVING));
        assertEquals(State.RECEIVING, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("Valid transition from RECEIVING to STORING")
    void testReceivingToStoring() {
        fsm.trigger(Event.START_RECEIVING);
        assertTrue(fsm.trigger(Event.FINISH_RECEIVING));
        assertEquals(State.STORING, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("Valid transition from STORING to IDLE")
    void testStoringToIdle() {
        fsm.trigger(Event.START_RECEIVING);
        fsm.trigger(Event.FINISH_RECEIVING);
        assertTrue(fsm.trigger(Event.FINISH_STORING));
        assertEquals(State.IDLE, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("Valid transition from IDLE to PICKING")
    void testIdleToPicking() {
        assertTrue(fsm.trigger(Event.START_PICKING));
        assertEquals(State.PICKING, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("Valid transition from PICKING to PACKING")
    void testPickingToPacking() {
        fsm.trigger(Event.START_PICKING);
        assertTrue(fsm.trigger(Event.FINISH_PICKING));
        assertEquals(State.PACKING, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("Valid transition from PACKING to SHIPPING")
    void testPackingToShipping() {
        fsm.trigger(Event.START_PICKING);
        fsm.trigger(Event.FINISH_PICKING);
        assertTrue(fsm.trigger(Event.FINISH_PACKING));
        assertEquals(State.SHIPPING, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("Valid transition from SHIPPING to COMPLETED")
    void testShippingToCompleted() {
        fsm.trigger(Event.START_PICKING);
        fsm.trigger(Event.FINISH_PICKING);
        fsm.trigger(Event.FINISH_PACKING);
        assertTrue(fsm.trigger(Event.FINISH_SHIPPING));
        assertEquals(State.COMPLETED, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("Valid transition from COMPLETED to IDLE via RESET")
    void testCompletedToIdle() {
        fsm.trigger(Event.START_PICKING);
        fsm.trigger(Event.FINISH_PICKING);
        fsm.trigger(Event.FINISH_PACKING);
        fsm.trigger(Event.FINISH_SHIPPING);
        assertTrue(fsm.trigger(Event.RESET));
        assertEquals(State.IDLE, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("Invalid transition should fail and maintain current state")
    void testInvalidTransition() {
        State initialState = fsm.getCurrentState();
        assertFalse(fsm.trigger(Event.FINISH_PACKING));
        assertEquals(initialState, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("Invalid transition from RECEIVING to PICKING should fail")
    void testInvalidReceivingToPicking() {
        fsm.trigger(Event.START_RECEIVING);
        assertFalse(fsm.trigger(Event.START_PICKING));
        assertEquals(State.RECEIVING, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("Reset from any state should return to IDLE")
    void testResetFromAnyState() {
        fsm.trigger(Event.START_RECEIVING);
        assertTrue(fsm.trigger(Event.RESET));
        assertEquals(State.IDLE, fsm.getCurrentState());
        
        fsm.trigger(Event.START_PICKING);
        fsm.trigger(Event.FINISH_PICKING);
        assertTrue(fsm.trigger(Event.RESET));
        assertEquals(State.IDLE, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("Transition history should be recorded correctly")
    void testTransitionHistory() {
        fsm.trigger(Event.START_RECEIVING);
        fsm.trigger(Event.FINISH_RECEIVING);
        
        List<String> history = fsm.getTransitionHistory();
        assertEquals(2, history.size());
        assertTrue(history.get(0).contains("Idle"));
        assertTrue(history.get(0).contains("Receiving"));
        assertTrue(history.get(1).contains("Storing"));
    }
    
    @Test
    @DisplayName("Reset should clear transition history")
    void testResetClearsHistory() {
        fsm.trigger(Event.START_RECEIVING);
        fsm.trigger(Event.FINISH_RECEIVING);
        
        fsm.reset();
        
        assertEquals(0, fsm.getTransitionHistory().size());
        assertEquals(State.IDLE, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("isValidTransition should correctly identify valid transitions")
    void testIsValidTransition() {
        assertTrue(fsm.isValidTransition(Event.START_RECEIVING));
        assertTrue(fsm.isValidTransition(Event.START_PICKING));
        assertFalse(fsm.isValidTransition(Event.FINISH_PACKING));
        
        fsm.trigger(Event.START_RECEIVING);
        assertTrue(fsm.isValidTransition(Event.FINISH_RECEIVING));
        assertFalse(fsm.isValidTransition(Event.START_RECEIVING));
    }
    
    @Test
    @DisplayName("getValidEvents should return all valid events from current state")
    void testGetValidEvents() {
        List<Event> validEvents = fsm.getValidEvents();
        assertTrue(validEvents.contains(Event.START_RECEIVING));
        assertTrue(validEvents.contains(Event.START_PICKING));
        
        fsm.trigger(Event.START_RECEIVING);
        validEvents = fsm.getValidEvents();
        assertTrue(validEvents.contains(Event.FINISH_RECEIVING));
        assertTrue(validEvents.contains(Event.RESET));
    }
    
    @Test
    @DisplayName("Complete workflow: Receive, Store, Pick, Pack, Ship")
    void testCompleteWorkflow() {
        // Receive inventory
        assertTrue(fsm.trigger(Event.START_RECEIVING));
        assertEquals(State.RECEIVING, fsm.getCurrentState());
        
        // Store inventory
        assertTrue(fsm.trigger(Event.FINISH_RECEIVING));
        assertEquals(State.STORING, fsm.getCurrentState());
        
        // Pick order
        assertTrue(fsm.trigger(Event.START_PICKING));
        assertEquals(State.PICKING, fsm.getCurrentState());
        
        // Pack order
        assertTrue(fsm.trigger(Event.FINISH_PICKING));
        assertEquals(State.PACKING, fsm.getCurrentState());
        
        // Ship order
        assertTrue(fsm.trigger(Event.FINISH_PACKING));
        assertEquals(State.SHIPPING, fsm.getCurrentState());
        
        // Complete
        assertTrue(fsm.trigger(Event.FINISH_SHIPPING));
        assertEquals(State.COMPLETED, fsm.getCurrentState());
        
        // Reset
        assertTrue(fsm.trigger(Event.RESET));
        assertEquals(State.IDLE, fsm.getCurrentState());
    }
    
    @Test
    @DisplayName("Multiple consecutive workflows")
    void testMultipleWorkflows() {
        // First workflow
        fsm.trigger(Event.START_PICKING);
        fsm.trigger(Event.FINISH_PICKING);
        fsm.trigger(Event.FINISH_PACKING);
        fsm.trigger(Event.FINISH_SHIPPING);
        fsm.trigger(Event.RESET);
        
        // Second workflow
        assertTrue(fsm.trigger(Event.START_RECEIVING));
        assertTrue(fsm.trigger(Event.FINISH_RECEIVING));
        assertTrue(fsm.trigger(Event.FINISH_STORING));
        
        assertEquals(State.IDLE, fsm.getCurrentState());
        assertEquals(8, fsm.getTransitionHistory().size());
    }
}
