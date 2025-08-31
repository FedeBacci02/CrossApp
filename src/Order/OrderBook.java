package Order;

import java.util.*;

import org.fusesource.jansi.Ansi;

public class OrderBook {

    // struttura dati usata per il lato ask
    private TreeMap<Integer, LinkedList<EvaluatingOrder>> askBook; // Chiave: "price" Values: "Order"

    // struttura dati usata per il lato bid
    private TreeMap<Integer, LinkedList<EvaluatingOrder>> bidBook; // Chiave: "price" Values: "Order"

    // struttura dati utilizzata per la gestione degli stop orders
    private List<EvaluatingOrder> stopOrders;

    public OrderBook() {

        stopOrders = new LinkedList<>();
        askBook = new TreeMap<>();
        bidBook = new TreeMap<>(Comparator.reverseOrder());
    }

    public synchronized void limitOrderInsert(EvaluatingOrder o) {
        if (o.getType() == OType.BID) {
            // aggiunge nel book dei bid
            if (!bidBook.containsKey(o.getPrice())) {
                // crea lista e aggiunge il limit order
                bidBook.put(o.getPrice(), new LinkedList<>());
            }
            bidBook.get(o.getPrice()).add(o);
        } else {
            // aggiunge nel book dei ask
            if (!askBook.containsKey(o.getPrice())) {
                // crea lista e aggiunge il limit order
                askBook.put(o.getPrice(), new LinkedList<>());
            }
            askBook.get(o.getPrice()).add(o);
        }
    }

    // output in frontend dell orderbook
    public synchronized void visualizzaOrderBook() {
        // Iterating over the elements of the tree map
        System.out.print(Ansi.ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a(String.format("%-10s", "Price")));
        System.out.print(Ansi.ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a(String.format("%-10s", "Size")));
        System.out.println(
                Ansi.ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a(String.format("%-10s", "Total")).reset());

        for (Map.Entry<Integer, LinkedList<EvaluatingOrder>> entry : askBook.entrySet()) {
            // somma le dimensioni che appartegono alla stessa chiave, ossia alla solita
            // offerta/richiesta
            int price = entry.getKey();
            int size = integraSize(entry.getValue());
            long total = (long) price * size;

            System.out.print(Ansi.ansi().fg(Ansi.Color.RED).a(String.format("%-10d", price)));
            System.out.print(Ansi.ansi().fg(Ansi.Color.WHITE).a(String.format("%-10d", size)));
            System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a(String.format("%-10d", total)).reset());
        }
        System.out.println("    ");
        for (Map.Entry<Integer, LinkedList<EvaluatingOrder>> entry : bidBook.entrySet()) {
            // somma le dimensioni che appartegono alla stessa chiave, ossia alla solita
            // offerta/richiesta
            int price = entry.getKey();
            int size = integraSize(entry.getValue());
            long total = (long) price * size;

            System.out.print(Ansi.ansi().fg(Ansi.Color.GREEN).a(String.format("%-10d", price)));
            System.out.print(Ansi.ansi().fg(Ansi.Color.WHITE).a(String.format("%-10d", size)));
            System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a(String.format("%-10d", total)).reset());
        }

        System.out.println(bidBook.toString());
        System.out.println(askBook.toString());

    }

    public synchronized void visualizzaStopOrders() {
        Iterator<EvaluatingOrder> iterator = stopOrders.iterator();
        while (iterator.hasNext())
            System.out.println(iterator.next().toString());
    }

    public synchronized static int integraSize(LinkedList<EvaluatingOrder> ordini) {
        Iterator<EvaluatingOrder> iterator = ordini.iterator();
        int sommaSize = 0;
        while (iterator.hasNext()) {
            sommaSize = sommaSize + iterator.next().getSize();
        }
        return sommaSize;
    }

    // funzione di back up dell'order book per il market order
    private OrderBook backUpCreate() {

        // System.out.println("STAMPA DA backUpCreate()");
        // this.visualizzaOrderBook();

        OrderBook oldBook = new OrderBook();
        oldBook.askBook.clear();
        oldBook.bidBook.clear();

        for (Map.Entry<Integer, LinkedList<EvaluatingOrder>> entry : askBook.entrySet()) {
            LinkedList<EvaluatingOrder> nuovaCoda = new LinkedList<>();
            for (EvaluatingOrder ordine : entry.getValue()) {
                nuovaCoda.add(new EvaluatingOrder(ordine)); // Aggiungi alla nuova coda
            }
            oldBook.askBook.put(entry.getKey(), nuovaCoda); // Inserisci nel nuovo libro
        }

        for (Map.Entry<Integer, LinkedList<EvaluatingOrder>> entry : bidBook.entrySet()) {
            LinkedList<EvaluatingOrder> nuovaCoda = new LinkedList<>();
            for (EvaluatingOrder ordine : entry.getValue()) {
                nuovaCoda.add(new EvaluatingOrder(ordine)); // Aggiungi alla nuova coda
            }
            oldBook.bidBook.put(entry.getKey(), nuovaCoda); // Inserisci nel nuovo libro
        }

        return oldBook;

    }

    // Fa il match degli stop orders
    public synchronized void stopOrderMatch(OrderHistory oHistory) {

        Iterator<EvaluatingOrder> iterator = stopOrders.iterator();
        OrderStrategy strategy = new MarketOrder();

        if (!stopOrders.isEmpty()) {
            while (iterator.hasNext()) {
                EvaluatingOrder ordine = iterator.next();
                switch (ordine.getType()) {
                    case BID:
                        if (!askBook.isEmpty())
                            if (ordine.getPrice() >= askBook.firstKey())
                                strategy.esegui(ordine, this, oHistory);
                        break;
                    case ASK:
                        if (!bidBook.isEmpty())
                            if (ordine.getPrice() <= bidBook.firstKey())
                                strategy.esegui(ordine, this, oHistory);
                        break;
                    default:
                        break;
                }
            }
        } else {
            System.out.println("[+] Nessun stop order è stato soddisfatto");
        }
    }

    // algoritmo di matching dei limit orders
    public synchronized void matchLimitOrders(OrderHistory oHistory) {
        // Finché ci sono bid e ask da matchare
        while (!bidBook.isEmpty() && !askBook.isEmpty()) {
            int highestBidPrice = bidBook.firstKey(); // Bid più alto
            int lowestAskPrice = askBook.firstKey(); // Ask più basso

            // Controlla se i prezzi matchano
            if (highestBidPrice >= lowestAskPrice) {
                Queue<EvaluatingOrder> bidQueue = bidBook.get(highestBidPrice);
                Queue<EvaluatingOrder> askQueue = askBook.get(lowestAskPrice);

                EvaluatingOrder bidOrder = bidQueue.peek();
                EvaluatingOrder askOrder = askQueue.peek();

                int bidSize = bidOrder.getSize();
                int askSize = askOrder.getSize();

                int tradedSize = Math.min(bidSize, askSize);
                int tradePrice = lowestAskPrice; // Spesso si esegue al prezzo ask

                // Esegui il trade
                System.out.printf("Trade executed: Size=%d at Price=%d%n", tradedSize, tradePrice);

                OrdineEvaso ordineBidEvaso = bidOrder.evadiOrdine();
                OrdineEvaso ordineAskEvaso = askOrder.evadiOrdine();

                // Aggiorna le quantità residue
                bidOrder.setSize(bidSize - tradedSize);
                askOrder.setSize(askSize - tradedSize);

                // Rimuovi ordini completati
                if (bidOrder.getSize() == 0) {
                    System.out.println(
                            "[+]" + bidOrder.getOrderId() + " di " + bidOrder.getUsername() + " è stato completato");

                    // invio della notifica all'utente interessato
                    oHistory.getOrdiniEvasi().put(ordineBidEvaso.getOrderId(), ordineBidEvaso);
                    NotificaOrdine notifica = new NotificaOrdine(ordineBidEvaso);
                    notifica.inviaOrdine(bidOrder.getUtente());

                    bidQueue.poll();
                    if (bidQueue.isEmpty()) {
                        bidBook.remove(highestBidPrice);
                    }
                }
                if (askOrder.getSize() == 0) {

                    // output nel server
                    System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(
                            "[+]" + askOrder.getOrderId() + " di " + askOrder.getUsername() + " è stato completato"));

                    // aggiungi alla struttura dati degl'ordini evasi
                    oHistory.getOrdiniEvasi().put(ordineAskEvaso.getOrderId(), ordineAskEvaso);

                    // invia notifica
                    NotificaOrdine notifica = new NotificaOrdine(ordineAskEvaso);
                    notifica.inviaOrdine(askOrder.getUtente());

                    askQueue.poll();
                    if (askQueue.isEmpty()) {
                        askBook.remove(lowestAskPrice);
                    }
                }
            } else {
                // Nessun match possibile
                System.out.println("[+] Nessun match disponibile");
                break;
            }
        }
    }

    // esegue il market order
    public synchronized int eseguiMarketOrder(EvaluatingOrder order, OrderHistory oHistory) {

        /*
         * System.out.println("ORDER BOOK ORIGINALE");
         * this.visualizzaOrderBook();
         */

        // crea la copia per il back up
        OrderBook backupBook = this.backUpCreate();

        /*
         * System.out.println("BACK UP BOOK");
         * backupBook.visualizzaOrderBook();
         */

        // insieme delle chiavi (prize) da rimuovere
        List<Integer> daRimuovere = new ArrayList<>();

        // insieme degl'ordini da notificare
        LinkedList<EvaluatingOrder> daNotificare = new LinkedList<>();

        // size dell market order da azzerare
        int size = order.getSize();

        switch (order.getType()) {
            case ASK:
                //System.out.println("[+] esegue ASK");
                for (Map.Entry<Integer, LinkedList<EvaluatingOrder>> entry : this.getBidBook().entrySet()) {

                    Iterator<EvaluatingOrder> iterator = entry.getValue().iterator();

                    while (iterator.hasNext() && size != 0) {
                        EvaluatingOrder ordine = iterator.next();

                        System.out.println(size);
                        if (size - ordine.getSize() >= 0) {
                            size = size - ordine.getSize();
                            //System.out.println("[+] OK2");
                            daNotificare.add(ordine);
                            iterator.remove();
                        } else {
                            // size < 0
                            //System.out.println("[+] OK1");
                            int newSize = ordine.getSize() - size;
                            ordine.setSize(newSize);
                            size = 0;
                        }
                    }

                    // controlliamo se la lista è vuota e aggiungiamo la chiave da rimuovere in
                    // lista
                    if (entry.getValue().isEmpty())
                        daRimuovere.add(entry.getKey());
                }

                if (size != 0) {
                    // resettiamo l'order book come era in partenza
                    //backupBook.visualizzaOrderBook();

                    //esegue il backup dell orderbook
                    this.getBidBook().clear();
                    this.getBidBook().putAll(backupBook.getBidBook());

                    // fine alg
                    System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("[+] Market order ha fallito"));
                    //this.visualizzaOrderBook();

                    return -1; //ritorna codice di errore - non può essere evaso
                } else {
                    // ordine soddisfatto
                    System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("[+] Market order eseguito con successo"));

                    // invia notifica a chi ha inviato il market order
                    OrdineEvaso ordineEvaso = order.evadiOrdine();
                    oHistory.getOrdiniEvasi().put(ordineEvaso.getOrderId(), ordineEvaso);
                    NotificaOrdine notifica = new NotificaOrdine(ordineEvaso);
                    notifica.inviaOrdine(order.getUtente());

                    // rimozione chiavi
                    Iterator<Integer> iterator = daRimuovere.iterator();
                    while (iterator.hasNext()) {
                        this.bidBook.remove(iterator.next());
                    }

                    // notifichiamo i limitorder evasi
                    Iterator<EvaluatingOrder> iterator3 = daNotificare.iterator();
                    while (iterator3.hasNext()) {
                        //estrae l'ordine e lo si trasforma nell'ordine evaso
                        EvaluatingOrder order2 = iterator3.next();
                        OrdineEvaso ordineEvaso2 = order2.evadiOrdine();

                        // aggiungi alla struttura dati degl'ordini evasi
                        oHistory.getOrdiniEvasi().put(ordineEvaso2.getOrderId(), ordineEvaso2);
                        
                        // invia la notifica
                        NotificaOrdine notifica2 = new NotificaOrdine(ordineEvaso2);
                        notifica2.inviaOrdine(order2.getUtente());
                    }

                    return 1;
                }
            case BID:
                //System.out.println("[+] esegue BID");
                for (Map.Entry<Integer, LinkedList<EvaluatingOrder>> entry : this.getAskBook().entrySet()) {

                    Iterator<EvaluatingOrder> iterator2 = entry.getValue().iterator();

                    while (iterator2.hasNext() && size != 0) {
                        EvaluatingOrder ordine = iterator2.next();

                        //System.out.println(size);
                        if (size - ordine.getSize() >= 0) {
                            size = size - ordine.getSize();
                            //System.out.println("[+] OK2");
                            daNotificare.add(ordine);
                            iterator2.remove();
                        } else {
                            // size < 0
                            //System.out.println("[+] OK1");
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
                    this.askBook.remove(iterator2.next());
                }

                if (size != 0) {
                    // resettiamo l'order book come era in partenza
                    backupBook.visualizzaOrderBook();
                    this.getAskBook().clear();
                    this.getAskBook().putAll(backupBook.getAskBook());
                    // fine alg
                    System.out.println("[+] NO OK");
                    this.visualizzaOrderBook();

                    return -1; // non può essere evaso
                } else {
                    // ordine soddisfatto
                    System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("[+] Market order eseguito con successo"));

                    OrdineEvaso ordineEvaso = order.evadiOrdine();
                    NotificaOrdine notifica = new NotificaOrdine(ordineEvaso);
                    oHistory.getOrdiniEvasi().put(order.getOrderId(), ordineEvaso);
                    notifica.inviaOrdine(order.getUtente());

                    // notifichiamo i limitorder evasi
                    Iterator<EvaluatingOrder> iterator3 = daNotificare.iterator();
                    while (iterator3.hasNext()) {
                        EvaluatingOrder order2 = iterator3.next();
                        OrdineEvaso ordineEvaso2 = order2.evadiOrdine();
                        NotificaOrdine notifica2 = new NotificaOrdine(ordineEvaso2);
                        notifica2.inviaOrdine(order2.getUtente());
                    }

                    return 1;
                }
            default:
                System.out.println("errore");
                break;
        }
        return 0;

    }

    // funzione per cancellare un ordine dall'orderbook o dalla lista stop order
    // poichè non sono evasi
    public synchronized int cancelOrder(int orderId, String username) {
        return EvaluatingOrder.cancelEvaluatingOrder(orderId, this, username);
    }

    // funzioni opzionali
    public synchronized List<EvaluatingOrder> getStopOrders() {
        return stopOrders;
    }

    public synchronized void setStopOrders(List<EvaluatingOrder> stopOrders) {
        this.stopOrders = stopOrders;
    }

    public synchronized Map<Integer, LinkedList<EvaluatingOrder>> getAskBook() {
        return askBook;
    }

    public synchronized void setAskBook(TreeMap<Integer, LinkedList<EvaluatingOrder>> askBook) {
        this.askBook = askBook;
    }

    public synchronized Map<Integer, LinkedList<EvaluatingOrder>> getBidBook() {
        return bidBook;
    }

    public synchronized void setBidBook(TreeMap<Integer, LinkedList<EvaluatingOrder>> bidBook) {
        this.bidBook = bidBook;
    }

}
