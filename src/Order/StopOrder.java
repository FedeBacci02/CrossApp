package Order;

public class StopOrder implements OrderStrategy {
    @Override
    public int esegui(EvaluatingOrder order, OrderBook orderBook) {
        System.out.println("StopOrder execution");

        //aggiungiamo alla lista lo stop order
        orderBook.getStopOrders().add(order);

        //iterazione dello stop order (poichè deve fare come un market)
        orderBook.stopOrderMatch();
        
        return 1;
    }

    public String getStrategyName() {
        return "stop";
    }
}
