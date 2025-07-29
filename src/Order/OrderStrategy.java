package Order;
public interface OrderStrategy {
    void esegui(Order order,OrderBook orderBook);
    String getStrategyName();
}
