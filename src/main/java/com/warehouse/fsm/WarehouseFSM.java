package com.warehouse.fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Finite State Machine for warehouse operations
 */
public class WarehouseFSM {
    private State currentState;
    private final Map<State, Map<Event, State>> transitions;
    private final List<String> transitionHistory;

    public WarehouseFSM() {
        this.currentState = State.IDLE;
        this.transitions = new HashMap<>();
        this.transitionHistory = new ArrayList<>();
        initializeTransitions();
    }

    /**
     * Initialize valid state transitions
     */
    private void initializeTransitions() {
        // From IDLE
        addTransition(State.IDLE, Event.START_RECEIVING, State.RECEIVING);
        
        // From RECEIVING
        addTransition(State.RECEIVING, Event.FINISH_RECEIVING, State.STORING);
        
        // From STORING
        addTransition(State.STORING, Event.FINISH_STORING, State.IDLE);
        addTransition(State.STORING, Event.START_PICKING, State.PICKING);
        
        // From IDLE to PICKING (for existing inventory)
        addTransition(State.IDLE, Event.START_PICKING, State.PICKING);
        
        // From PICKING
        addTransition(State.PICKING, Event.FINISH_PICKING, State.PACKING);
        
        // From PACKING
        addTransition(State.PACKING, Event.FINISH_PACKING, State.SHIPPING);
        
        // From SHIPPING
        addTransition(State.SHIPPING, Event.FINISH_SHIPPING, State.COMPLETED);
        
        // From COMPLETED
        addTransition(State.COMPLETED, Event.RESET, State.IDLE);
        
        // Reset from any state
        for (State state : State.values()) {
            if (state != State.IDLE && state != State.COMPLETED) {
                addTransition(state, Event.RESET, State.IDLE);
            }
        }
    }

    /**
     * Add a transition to the state machine
     */
    private void addTransition(State from, Event event, State to) {
        transitions.computeIfAbsent(from, k -> new HashMap<>()).put(event, to);
    }

    /**
     * Trigger an event to transition to a new state
     * @param event The event to trigger
     * @return true if transition was successful, false otherwise
     */
    public boolean trigger(Event event) {
        Map<Event, State> stateTransitions = transitions.get(currentState);
        
        if (stateTransitions == null || !stateTransitions.containsKey(event)) {
            System.out.println("Invalid transition: Cannot trigger '" + event + 
                             "' from state '" + currentState + "'");
            return false;
        }
        
        State previousState = currentState;
        currentState = stateTransitions.get(event);
        
        String transitionLog = previousState + " --[" + event + "]--> " + currentState;
        transitionHistory.add(transitionLog);
        
        System.out.println("Transition: " + transitionLog);
        return true;
    }

    /**
     * Get the current state
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Get the transition history
     */
    public List<String> getTransitionHistory() {
        return new ArrayList<>(transitionHistory);
    }

    /**
     * Reset the FSM to initial state
     */
    public void reset() {
        currentState = State.IDLE;
        transitionHistory.clear();
        System.out.println("FSM reset to IDLE state");
    }

    /**
     * Check if a transition is valid from current state
     */
    public boolean isValidTransition(Event event) {
        Map<Event, State> stateTransitions = transitions.get(currentState);
        return stateTransitions != null && stateTransitions.containsKey(event);
    }

    /**
     * Get all valid events from current state
     */
    public List<Event> getValidEvents() {
        Map<Event, State> stateTransitions = transitions.get(currentState);
        if (stateTransitions == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(stateTransitions.keySet());
    }
}
