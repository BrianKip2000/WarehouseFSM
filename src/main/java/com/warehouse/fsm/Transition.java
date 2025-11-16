package com.warehouse.fsm;

/**
 * Represents a transition in the state machine
 */
public class Transition {
    private final State fromState;
    private final Event event;
    private final State toState;

    public Transition(State fromState, Event event, State toState) {
        this.fromState = fromState;
        this.event = event;
        this.toState = toState;
    }

    public State getFromState() {
        return fromState;
    }

    public Event getEvent() {
        return event;
    }

    public State getToState() {
        return toState;
    }

    @Override
    public String toString() {
        return fromState + " --[" + event + "]--> " + toState;
    }
}
