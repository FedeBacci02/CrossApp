package Order;
public interface OrderStrategy {
    int esegui(EvaluatingOrder order,OrderBook orderBook,OrderHistory oHistory);
    String getStrategyName();
}
