package Order;

public class CancelOrderRequest{
    
    private int orderId;

    public CancelOrderRequest(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

}
