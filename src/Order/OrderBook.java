package Order;

import java.util.*;

import org.fusesource.jansi.Ansi;

public class OrderBook {
    Map<Integer, Queue<EvaluatingOrder>> askBook; // Chiave: "price" Values: "Order"
    Map<Integer, Queue<EvaluatingOrder>> bidBook;

    public OrderBook() {

        askBook = new TreeMap<>();
        bidBook = new TreeMap<>(Comparator.reverseOrder());

        // Seed di ordini BID
        limitOrderInsert(new EvaluatingOrder(OType.BID, 100, 1050, "mario", 1));
        limitOrderInsert(new EvaluatingOrder(OType.BID, 80, 1030, "luigi", 2));
        limitOrderInsert(new EvaluatingOrder(OType.BID, 50, 1000, "peach", 3));

        // Seed di ordini ASK
        limitOrderInsert(new EvaluatingOrder(OType.ASK, 70, 1080, "daisy", 4));
        limitOrderInsert(new EvaluatingOrder(OType.ASK, 60, 1100, "wario", 5));
        limitOrderInsert(new EvaluatingOrder(OType.ASK, 40, 1120, "yoshi", 6));

        System.out.println("Check ordine bidBook:");
        for (Integer prezzo : bidBook.keySet()) {
            System.out.println(prezzo);
        }

    }

    public void limitOrderInsert(EvaluatingOrder o) {
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

        for (Map.Entry<Integer, Queue<EvaluatingOrder>> entry : askBook.entrySet()) {
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
        for (Map.Entry<Integer, Queue<EvaluatingOrder>> entry : bidBook.entrySet()) {
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

    public static int integraSize(Queue<EvaluatingOrder> ordini) {
        Iterator<EvaluatingOrder> iterator = ordini.iterator();
        int sommaSize = 0;
        while (iterator.hasNext()) {
            sommaSize = sommaSize + iterator.next().getSize();
        }
        return sommaSize;
    }

    // algoritmo di matching (ritorna 1 se è stato matchato con successo, -1
    // altrimenti)
    public static int matchingAlgorithm(OrderContext orderContext) {
        orderContext.matchOrder();
        return 1;
    }

    public Map<Integer, Queue<EvaluatingOrder>> getAskBook() {
        return askBook;
    }

    public void setAskBook(Map<Integer, Queue<EvaluatingOrder>> askBook) {
        this.askBook = askBook;
    }

    public Map<Integer, Queue<EvaluatingOrder>> getBidBook() {
        return bidBook;
    }

    public void setBidBook(Map<Integer, Queue<EvaluatingOrder>> bidBook) {
        this.bidBook = bidBook;
    }

    public OrderBook backUpCreate() {

        System.out.println(">>> STAMPA DA backUpCreate() <<<");
        this.visualizzaOrderBook(); // <-- stampa lo stato reale al momento della copia

        OrderBook oldBook = new OrderBook();
        oldBook.askBook.clear();
        oldBook.bidBook.clear();
        
        for (Map.Entry<Integer, Queue<EvaluatingOrder>> entry : askBook.entrySet()) {
            Queue<EvaluatingOrder> nuovaCoda = new LinkedList<>();
            for (EvaluatingOrder ordine : entry.getValue()) {
                nuovaCoda.add(new EvaluatingOrder(ordine)); // Aggiungi alla nuova coda
            }
            oldBook.askBook.put(entry.getKey(), nuovaCoda); // Inserisci nel nuovo libro
        }

        for (Map.Entry<Integer, Queue<EvaluatingOrder>> entry : bidBook.entrySet()) {
            Queue<EvaluatingOrder> nuovaCoda = new LinkedList<>();
            for (EvaluatingOrder ordine : entry.getValue()) {
                nuovaCoda.add(new EvaluatingOrder(ordine)); // Aggiungi alla nuova coda
            }
            oldBook.bidBook.put(entry.getKey(), nuovaCoda); // Inserisci nel nuovo libro
        }

        return oldBook;

    }

}
