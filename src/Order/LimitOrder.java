package Order;
public class LimitOrder implements OrderStrategy{
    @Override
    public int esegui(EvaluatingOrder order,OrderBook orderBook, OrderHistory oHistory) {
        System.out.println("LimitOrder execution");

        //inserimento del limit order
        orderBook.limitOrderInsert(order);

        //iterazione dello stop order (poichè deve fare come un market)
        orderBook.stopOrderMatch(oHistory);

        //iterazione dell'algoritmo di matching (successivo al controllo degli stop poichè devono essere eseguiti subito)
        orderBook.matchLimitOrders(oHistory);
        
        return 1;
    }

    public String getStrategyName(){
        return "limit";
    }
}
