package Order;

public class EvaluatingOrder extends Order {

    int orderId;
    String username;

    public EvaluatingOrder(OType type2, int size, int price, String username, int orderId) {
        super(type2,size,price);
        this.orderId = orderId;
        this.username=username;
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

    
}
