package taz.amin.sagapattern.account;

public class AccountRequest {
    private String userId;
    private String orderId;
    private double amount;

    // Constructors
    public AccountRequest() {
    }

    public AccountRequest(String userId, String orderId, double amount) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
