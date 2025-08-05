package Order;

import java.util.*;

public class MarketOrder implements OrderStrategy {

    public int esegui(EvaluatingOrder order, OrderBook orderBook) {
        // algotitmo di matching per market order.esegue subito o restituisce errore
        System.out.println("[+] MarketOrder execution");

        System.out.println("==== ORDER BOOK ORIGINALE ====");
        orderBook.visualizzaOrderBook(); // Stampa PRIMA di fare backup

        OrderBook backupBook = orderBook.backUpCreate(); // Copia lo stato attuale

        System.out.println("==== BACKUP BOOK ====");
        backupBook.visualizzaOrderBook(); // Stampa backup

        List<Integer> daRimuovere = new ArrayList<>();
        int size = order.getSize();
        switch (order.getType()) {
            case ASK:
                System.out.println("[+] esegue ASK");
                for (Map.Entry<Integer, LinkedList<EvaluatingOrder>> entry : orderBook.getBidBook().entrySet()) {

                    Iterator<EvaluatingOrder> iterator = entry.getValue().iterator();

                    while (iterator.hasNext() && size != 0) {
                        EvaluatingOrder ordine = iterator.next();

                        System.out.println(size);
                        if (size - ordine.getSize() >= 0) {
                            size = size - ordine.getSize();
                            System.out.println("[+] OK2");
                            entry.getValue().remove();
                        } else {
                            // size < 0
                            System.out.println("[+] OK1");
                            int newSize = ordine.getSize() - size;
                            ordine.setSize(newSize);
                            size = 0;
                        }
                    }

                    // controlliamo se la lista è vuota e aggiungiamo la chiave da rimuovere in lista
                    if (entry.getValue().isEmpty())
                        daRimuovere.add(entry.getKey());
                }

                // rimozione chiavi
                Iterator<Integer> iterator = daRimuovere.iterator();
                while (iterator.hasNext()) {
                    orderBook.bidBook.remove(iterator.next());
                }

                if (size != 0) {
                    // resettiamo l'order book come era in partenza
                    backupBook.visualizzaOrderBook();
                    orderBook.getBidBook().clear();
                    orderBook.getBidBook().putAll(backupBook.getBidBook());
                    // fine alg
                    System.out.println("[+] NO OK");
                    orderBook.visualizzaOrderBook();

                    return -1; // non può essere evaso
                } else {
                    // ordine soddisfatto
                    System.out.println("[+] OK");
                    return 1;
                }
            case BID:
                System.out.println("[+] esegue BID");
                for (Map.Entry<Integer, LinkedList<EvaluatingOrder>> entry : orderBook.getAskBook().entrySet()) {

                    Iterator<EvaluatingOrder> iterator2 = entry.getValue().iterator();

                    while (iterator2.hasNext() && size != 0) {
                        EvaluatingOrder ordine = iterator2.next();

                        System.out.println(size);
                        if (size - ordine.getSize() >= 0) {
                            size = size - ordine.getSize();
                            System.out.println("[+] OK2");
                            entry.getValue().remove();
                        } else {
                            // size < 0
                            System.out.println("[+] OK1");
                            int newSize = ordine.getSize() - size;
                            ordine.setSize(newSize);
                            size = 0;
                        }
                    }

                    // controlliamo se la lista è vuota
                    if (entry.getValue().isEmpty())
                        daRimuovere.add(entry.getKey());
                }

                // rimozione chiavi
                Iterator<Integer> iterator2 = daRimuovere.iterator();
                while (iterator2.hasNext()) {
                    orderBook.askBook.remove(iterator2.next());
                }

                if (size != 0) {
                    // resettiamo l'order book come era in partenza
                    backupBook.visualizzaOrderBook();
                    orderBook.getAskBook().clear();
                    orderBook.getAskBook().putAll(backupBook.getAskBook());
                    // fine alg
                    System.out.println("[+] NO OK");
                    orderBook.visualizzaOrderBook();

                    return -1; // non può essere evaso
                } else {
                    // ordine soddisfatto
                    System.out.println("[+] OK");
                    return 1;
                }
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
