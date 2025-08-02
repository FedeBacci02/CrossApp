package Order;

public class EvaluatingOrder extends Order {

    private int orderId;
    private String username;
    private String orderType;

    // Costruttore standard
    public EvaluatingOrder(OType type2, int size, int price, String username, int orderId, int timestamp,
            String orderType) {
        super(type2, size, price);
        this.orderId = orderId;
        this.username = username;
        this.orderType = orderType;
    }

    // Costruttore di copia (deep copy)
    public EvaluatingOrder(EvaluatingOrder other) {
        super(other); // chiama il costruttore di copia di Order
        this.orderId = other.orderId;
        this.username = other.username;
    }
    
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}