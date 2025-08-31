package Order;

//Classe per comporre il messaggio di topo CancelOrderRequest

public class CancelOrderRequest{
    
    private int orderId; //order id dell'ordine da eliminare

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
