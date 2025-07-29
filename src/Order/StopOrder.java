package Order;

public class StopOrder implements OrderStrategy {
    @Override
    public void esegui(Order order, OrderBook orderBook) {
        System.out.println("StopOrder execution");
    }

    public String getStrategyName() {
        return "stop";
    }
}
