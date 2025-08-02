package Order;

import java.util.HashMap;
import java.util.Map;

public class OrderContext {
    private OrderStrategy strategy;     //tipo di ordine
    private EvaluatingOrder order;      //type(BID/ASK),price e size 
    private OrderBook orderBook;        //orderbook di riferimento

    public static final Map<String, OrderStrategy> strategie = new HashMap<>();

    public OrderContext(EvaluatingOrder order, OrderBook orderBook) {
        this.order = order;
        this.orderBook = orderBook;

        strategie.put("insertlimitorder", new LimitOrder());
        strategie.put("insertmarketorder", new MarketOrder());
        strategie.put("insertstoporder", new StopOrder());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("orderId = ").append(order.getOrderId())
                .append("\n type = ").append(order.getType())
                .append("\n size = ").append(order.getSize())
                .append("\n price = ").append(order.getPrice())
                ;
        return sb.toString();
    }

    public int elaboraOrdine() {
        return strategy.esegui(this.order, this.orderBook);
    }

    public OrderStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategie.get(strategy);
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(EvaluatingOrder order) {
        this.order = order;
    }

    public OrderBook getOrderBook() {
        return orderBook;
    }

    public void setOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

}
