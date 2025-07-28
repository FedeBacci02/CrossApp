import java.util.*;

public class OrderBook {
    Map<Integer, Queue<Order>> askBook;
    Map<Integer, Queue<Order>> bidBook;

    public OrderBook() {

        askBook = new TreeMap<>();
        bidBook = new TreeMap<>(Comparator.reverseOrder());
    }

    public void limitOrderInsert(Order o) {
        if (o.getType().equals(OType.BID)) {
            //aggiunge nel book dei bid
            if (!bidBook.containsKey(o.getPrice())) {
                // crea lista e aggiunge il limit order
                bidBook.put(o.getPrice(), new LinkedList<>());
            }
            bidBook.get(o.getPrice()).add(o);
        } else {
            //aggiunge nel book dei ask
            if (!askBook.containsKey(o.getPrice())) {
                // crea lista e aggiunge il limit order
                askBook.put(o.getPrice(), new LinkedList<>());
            }a
            askBook.get(o.getPrice()).add(o);
        }
    }

}
