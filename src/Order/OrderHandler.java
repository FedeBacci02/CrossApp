package Order;

import java.util.concurrent.ConcurrentLinkedQueue;

public class OrderHandler implements Runnable{

    private OrderBook orderBook;
    private ConcurrentLinkedQueue<EvaluatingOrder> listaOrdini;

    public OrderHandler(OrderBook orderBook, ConcurrentLinkedQueue<EvaluatingOrder> listaOrdini) {
        this.orderBook = orderBook;
        this.listaOrdini = listaOrdini;
    }

    public void run() {
        
    }
}
