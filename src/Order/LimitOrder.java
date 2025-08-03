package Order;
public class LimitOrder implements OrderStrategy{
    @Override
    public int esegui(EvaluatingOrder order,OrderBook orderBook) {
        System.out.println("LimitOrder execution");

        //inserimento del limit order
        orderBook.limitOrderInsert(order);

        //iterazione dello stop order (poichè deve fare come un market)
        orderBook.stopOrderMatch();

        //iterazione dell'algoritmo di matching
        orderBook.matchLimitOrders();

        return 1;
    }

    public String getStrategyName(){
        return "limit";
    }
}
