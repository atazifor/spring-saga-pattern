package taz.amin.sagapattern.event;

public class BalanceDeductedEvent {
    public enum Type {SUCCESS, FAILURE}
    private final String userId;
    private final String orderId;
    private final Type eventType;

    // No-args constructor
    public BalanceDeductedEvent() {
        this.userId = null;
        this.orderId = null;
        this.eventType = null;
    }

    public BalanceDeductedEvent(Type eventType, String userId, String orderId) {
        this.userId = userId;
        this.orderId = orderId;
        this.eventType = eventType;
    }

    public Type getEventType() {
        return eventType;
    }

    public String getUserId() {
        return userId;
    }

    public String getOrderId() {
        return orderId;
    }

}
