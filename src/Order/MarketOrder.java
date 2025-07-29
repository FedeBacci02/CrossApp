package Order;

public class MarketOrder implements OrderStrategy {

    public void esegui(Order order, OrderBook orderBook) {
        System.out.println("MarketOrder execution");

    }

    public String getStrategyName() {
        return "market";
    }
}
