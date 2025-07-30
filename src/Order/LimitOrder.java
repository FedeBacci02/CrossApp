package Order;
public class LimitOrder implements OrderStrategy{
    @Override
    public int esegui(EvaluatingOrder order,OrderBook orderBook) {
        System.out.println("LimitOrder execution");
        return 1;
    }

    public String getStrategyName(){
        return "limit";
    }
}
