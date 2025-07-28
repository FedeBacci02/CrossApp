public class OrderContext {
    private OrderStrategy strategy;
    private Order order;
    private int orderId;

    public OrderContext(OrderStrategy strategy, Order order) {
        this.strategy = strategy;
        this.order = order;
    }

    public void spedisciOrdine(){
        strategy.esegui();
    }
    
}
