package Order;

//interfaccia dello strategy per il tipo di ordine

public interface OrderStrategy {
    int esegui(EvaluatingOrder order,OrderBook orderBook,OrderHistory oHistory);
    String getStrategyName();
}
