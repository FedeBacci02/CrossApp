package Order;

import java.util.*;

public class MarketOrder implements OrderStrategy {

    public int esegui(EvaluatingOrder order, OrderBook orderBook) {
        // algotitmo di matching per market order.esegue subito o restituisce errore
        System.out.println("[+] MarketOrder execution");
        OrderBook backupBook = orderBook.backUpCreate();
        List<Integer> daRimuovere = new ArrayList<>();
        int size = order.getSize();
        switch (order.getType()) {
            case ASK:
                System.out.println("[+] esegue ASK");
                for (Map.Entry<Integer, Queue<EvaluatingOrder>> entry : orderBook.getBidBook().entrySet()) {
                    Iterator<EvaluatingOrder> iterator = entry.getValue().iterator();
                    while (iterator.hasNext() && size != 0) {
                        EvaluatingOrder ordine = iterator.next();
                        if (ordine.getSize() == size) {
                            System.out.println("[+] OK1");
                            size=0;
                        } else if (ordine.getSize() >= size) {
                            int newSize = ordine.getSize() - size;
                            System.out.println(newSize);
                            ordine.setSize(newSize);
                            System.out.println("[+] OK2");
                            size=0;
                        } else {
                            int newSize = size - ordine.getSize();
                            if (entry.getValue().isEmpty())
                                orderBook.getBidBook().remove(entry.getKey());
                            entry.getValue().remove();
                            size = newSize;
                            orderBook.visualizzaOrderBook();
                        }
                    }
                    if (entry.getValue().isEmpty())
                        daRimuovere.add(entry.getKey());
                }
                // resettiamo l'order book come era in partenza
                orderBook.getAskBook().clear();
                orderBook.getAskBook().putAll(backupBook.getAskBook());

                orderBook.getBidBook().clear();
                orderBook.getBidBook().putAll(backupBook.getBidBook());
                orderBook.visualizzaOrderBook();

                // fine alg
                System.out.println("[+] NO OK");
                return -1; // non può essere evaso
            case BID:

                break;
            default:
                System.out.println("errore");
                break;
        }
        return 0;
    }

    public String getStrategyName() {
        return "market";
    }
}
