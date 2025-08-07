package Order;

import java.time.*;

public class OrdineEvaso extends Order {
    private int orderId;
    private String orderType;
    private LocalDateTime timestamp;

    public OrdineEvaso(int orderId, OType type, String orderType, int size, int price, LocalDateTime timestamp) {
        super(type, size, price);
        this.orderId = orderId;
        this.orderType = orderType;
        this.timestamp = timestamp;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }



    public LocalDateTime getTimestamp() {
        return timestamp;
    }



   public String toString() {
    return "OrdineEvaso {id=" + orderId +
           ", tipo=" + getOrderType()+
           ", prezzo=" + getPrice() +
           ", quantità=" + getSize() +
           ", data=" + getTimestamp()+ "}\n";
}

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
