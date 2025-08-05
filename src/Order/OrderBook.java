package Order;

import java.util.*;

import org.fusesource.jansi.Ansi;

public class OrderBook {
    TreeMap<Integer, LinkedList<EvaluatingOrder>> askBook; // Chiave: "price" Values: "Order"
    TreeMap<Integer, LinkedList<EvaluatingOrder>> bidBook;

    List<EvaluatingOrder> stopOrders;

    public OrderBook() {

        stopOrders = new LinkedList<>();
        askBook = new TreeMap<>();
        bidBook = new TreeMap<>(Comparator.reverseOrder());

        System.out.println("Check ordine bidBook:");
        for (Integer prezzo : bidBook.keySet()) {
            System.out.println(prezzo);
        }

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
    public void visualizzaOrderBook() {
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
            int total = price * size;

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
            int total = price * size;

            System.out.print(Ansi.ansi().fg(Ansi.Color.GREEN).a(String.format("%-10d", price)));
            System.out.print(Ansi.ansi().fg(Ansi.Color.WHITE).a(String.format("%-10d", size)));
            System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a(String.format("%-10d", total)).reset());
        }

    }

    public static int integraSize(LinkedList<EvaluatingOrder> ordini) {
        Iterator<EvaluatingOrder> iterator = ordini.iterator();
        int sommaSize = 0;
        while (iterator.hasNext()) {
            sommaSize = sommaSize + iterator.next().getSize();
        }
        return sommaSize;
    }

    public Map<Integer, LinkedList<EvaluatingOrder>> getAskBook() {
        return askBook;
    }

    public void setAskBook(TreeMap<Integer, LinkedList<EvaluatingOrder>> askBook) {
        this.askBook = askBook;
    }

    public Map<Integer, LinkedList<EvaluatingOrder>> getBidBook() {
        return bidBook;
    }

    public void setBidBook(TreeMap<Integer, LinkedList<EvaluatingOrder>> bidBook) {
        this.bidBook = bidBook;
    }

    public OrderBook backUpCreate() {

        System.out.println(">>> STAMPA DA backUpCreate() <<<");
        this.visualizzaOrderBook(); // stampa lo stato reale al momento della copia

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

    public void stopOrderMatch() {

        Iterator<EvaluatingOrder> iterator = stopOrders.iterator();
        OrderStrategy strategy = new MarketOrder();

        while (iterator.hasNext()) {
            EvaluatingOrder ordine = iterator.next();
            switch (ordine.getType()) {
                case BID:
                    if (ordine.getPrice() >= askBook.firstKey())
                        strategy.esegui(ordine, this);
                    break;
                case ASK:
                    if (ordine.getPrice() <= bidBook.firstKey())
                        strategy.esegui(ordine, this);
                    break;
                default:
                    break;
            }
        }
    }

    public synchronized void matchLimitOrders() {
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

                // Aggiorna le quantità residue
                bidOrder.setSize(bidSize - tradedSize);
                askOrder.setSize(askSize - tradedSize);

                // Rimuovi ordini completati
                if (bidOrder.getSize() == 0) {
                    bidQueue.poll();
                    if (bidQueue.isEmpty()) {
                        bidBook.remove(highestBidPrice);
                    }
                }
                if (askOrder.getSize() == 0) {
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

    //funzione per cancellare un ordine dall'orderbook o dalla lista stop order poichè non sono evasi
    public int cancelOrder(int orderId){
        return EvaluatingOrder.cancelEvaluatingOrder(orderId, this);
    }

    public List<EvaluatingOrder> getStopOrders() {
        return stopOrders;
    }

    public void setStopOrders(List<EvaluatingOrder> stopOrders) {
        this.stopOrders = stopOrders;
    }

}
