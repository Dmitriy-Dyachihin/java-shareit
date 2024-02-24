package ru.practicum.shareit.booking;

public enum State {

    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    UNSUPPORTED_STATUS;

    public static State getState(String value) {
        for (State state : values()) {
            if (state.toString().equals(value)) {
                return state;
            }
        }
        return UNSUPPORTED_STATUS;
    }
}
