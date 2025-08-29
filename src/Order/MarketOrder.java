package Order;

public class MarketOrder implements OrderStrategy {

    public int esegui(EvaluatingOrder order, OrderBook orderBook, OrderHistory oHistory) {
        // algotitmo di matching per market order.esegue subito o restituisce errore
        System.out.println("[+] MarketOrder execution");
        return orderBook.eseguiMarketOrder(order, oHistory);
    }

    public String getStrategyName() {
        return "market";
    }
}
