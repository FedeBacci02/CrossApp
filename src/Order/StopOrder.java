package Order;

public class StopOrder implements OrderStrategy {
    @Override
    public int esegui(EvaluatingOrder order, OrderBook orderBook) {
        System.out.println("StopOrder execution");
        
        return 1;
    }

    public String getStrategyName() {
        return "stop";
    }
}
