package Order;
public class LimitOrder implements OrderStrategy{
    @Override
    public void esegui(Order order,OrderBook orderBook) {
        System.out.println("LimitOrder execution");
    }

    public String getStrategyName(){
        return "limit";
    }
}
