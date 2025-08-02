package Order;

import java.util.concurrent.BlockingQueue;

public class OrderHandler implements Runnable {

    private OrderBook orderBook;
    private BlockingQueue<EvaluatingOrder> listaOrdini;

    public OrderHandler(OrderBook orderBook, BlockingQueue<EvaluatingOrder> listaOrdini) {
        this.orderBook = orderBook;
        this.listaOrdini = listaOrdini;
    }

    public void run() {
        try {
            while (true) {
                EvaluatingOrder ordine = listaOrdini.take(); // aspetta se la coda è vuota

                // creazione oggetto context
                OrderContext orderContext = new OrderContext(ordine, orderBook);

                // seleziona la strategia da applicare
                orderContext.setStrategy(ordine.getOrderType());

                // elaborare l'ordine
                orderContext.elaboraOrdine();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
