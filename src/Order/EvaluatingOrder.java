package Order;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import Utenti.*;


public class EvaluatingOrder extends Order {

    private int orderId;
    private String username;
    private UserConnected utente;
    private String orderType;

    // Costruttore standard
    public EvaluatingOrder(OType type, int size, int price, String username, int orderId, int timestamp,
            String orderType,UserConnected utente) {
        super(type, size, price);
        this.orderId = orderId;
        this.username = username;
        this.orderType = orderType;
        this.utente=utente;
    }

    // Costruttore di copia (deep copy)
    public EvaluatingOrder(EvaluatingOrder other) {
        super(other); // chiama il costruttore di copia di Order
        this.orderId = other.orderId;
        this.username = other.username;
        this.utente=other.utente;
    }
    
    //cancella un ordine da valutare nell'orderbook
    public static int cancelEvaluatingOrder(int orderId, OrderBook orderBook, String username) {

        //cerca e nel caso cancella dalla lista degli stop order
        Iterator<EvaluatingOrder> iterator = orderBook.getStopOrders().iterator();
        if (iterator.hasNext()) {
            EvaluatingOrder order =iterator.next();
            if (order.getOrderId() == orderId && order.getUsername() == username) {
                iterator.remove();
                return 1;
            }
        }

        //cerca e nel caso cancella dall book degli ask
        for (Map.Entry<Integer, LinkedList<EvaluatingOrder>> entry : orderBook.getAskBook().entrySet()) {
            for (EvaluatingOrder ordine : entry.getValue()) {
                if (ordine.getOrderId() == orderId && ordine.getUsername() == username){
                    entry.getValue().remove(ordine);
                    if(entry.getValue().isEmpty())
                        orderBook.getAskBook().remove(entry.getKey());
                    return 1;
                }
            }
        }

        //cerca e nel caso cancella dall book degli bid
        for (Map.Entry<Integer, LinkedList<EvaluatingOrder>> entry : orderBook.getAskBook().entrySet()) {
            for (EvaluatingOrder ordine : entry.getValue()) {
                if (ordine.getOrderId() == orderId && ordine.getUsername() == username){
                    entry.getValue().remove(ordine);
                    return 1;
                }
            }
        }

        //nel caso non fosse un ordine in lista
        return 0;

    }

    public OrdineEvaso evadiOrdine(){
        long timestamp = System.currentTimeMillis()/1000;
        System.out.println(timestamp);
        return new OrdineEvaso(orderId, getType(), orderType,getSize(), getPrice(),timestamp);
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

    public UserConnected getUtente() {
        return utente;
    }

    public void setUtente(UserConnected utente) {
        this.utente = utente;
    }

    
}