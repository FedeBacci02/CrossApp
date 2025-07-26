public class OrderContext{

    private OrderStrategy strategy;    //MarketOrder, LimitOrder o StopOrder
    private Order order;

    public OrderContext(OrderStrategy strategy, Order order) {
        this.strategy = strategy;
        this.order = order;
    }


}
