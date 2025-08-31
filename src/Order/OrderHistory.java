package Order;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

public class OrderHistory {
    private ConcurrentHashMap<Integer, OrdineEvaso> ordiniEvasi;

    public OrderHistory() {
        ordiniEvasi = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<Integer, OrdineEvaso> getOrdiniEvasi() {
        return ordiniEvasi;
    }

    // ritorna l'ultimo elemento in lista
    public OrdineEvaso getLast() {

        // Creiamo una copia
        Map<Integer, OrdineEvaso> copiaOrdiniEvasi = new HashMap<>(ordiniEvasi);

        if (copiaOrdiniEvasi.isEmpty()) {
            throw new NoSuchElementException("Err - Non sono presenti dati storici di ordini evasi");
        }

        OrdineEvaso lastEntry = null;

        // Iteriamo sulla copia
        for (Map.Entry<Integer, OrdineEvaso> entry : copiaOrdiniEvasi.entrySet()) {
            lastEntry = entry.getValue();
        }

        return lastEntry;
    }

    public void saveHistory(String fileName) {
        JsonWriter writer;

        try {
            writer = new JsonWriter(new FileWriter(fileName));
            writer.setIndent("  "); // per indentare

            writer.beginObject();
            writer.name("trades");
            writer.beginArray();
            for (Map.Entry<Integer, OrdineEvaso> entry : ordiniEvasi.entrySet()) {
                writer.beginObject();
                writer.name("orderId").value(entry.getValue().getOrderId());
                String type;
                if (entry.getValue().getType() == OType.ASK)
                    type = "ask";
                else
                    type = "bid";

                writer.name("type").value(type);
                writer.name("orderType").value(entry.getValue().getOrderType());
                writer.name("size").value(entry.getValue().getSize());
                writer.name("price").value(entry.getValue().getPrice());
                writer.name("timestamp").value(entry.getValue().getTimestamp());
                writer.endObject();
            }
            writer.endArray();
            writer.endObject();
            writer.close();
        } catch (IOException e) {
            System.err.print(e.getMessage());
        }
    }

    // funzione che carica il file esterno nella struttura dati
    public void loadOrdersFromFile(String jsonFileName) {
        File jfile = new File(jsonFileName);

        if (!jfile.exists() || !jfile.isFile() || jfile.length() == 0) {
            System.out.println("[+] File non valido o vuoto -> struttura dati vuota");
            return;
        }

        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(jfile));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfTrades = fileObject.get("trades").getAsJsonArray();
            for (JsonElement tradeElement : jsonArrayOfTrades) {
                JsonObject tradeJsonObject = tradeElement.getAsJsonObject();

                // facciamo il parser dell'oggetto json
                int orderId = tradeJsonObject.get("orderId").getAsInt();

                String type = tradeJsonObject.get("type").getAsString();

                OType otype;
                if (type.equals("ask"))
                    otype = OType.ASK;
                else if (type.equals("bid"))
                    otype = OType.BID;
                else {
                    System.err.println("err");
                    return;
                }
                String orderType = tradeJsonObject.get("orderType").getAsString();

                int size = tradeJsonObject.get("size").getAsInt();

                int price = tradeJsonObject.get("price").getAsInt();

                long timestampInt = tradeJsonObject.get("timestamp").getAsLong();

                OrdineEvaso trade = new OrdineEvaso(orderId, otype, orderType, size, price, timestampInt);
                ordiniEvasi.put(trade.getOrderId(), trade);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("[+] CARICAMENTO DI " + jsonFileName + " COMPLETATO.");
        }
    }

    public void printLista() {
        for (Map.Entry<Integer, OrdineEvaso> entry : ordiniEvasi.entrySet()) {
            System.out.println(entry.getValue().toString());
        }
    }

    public OrderHistoryResponse filtraPerMese(String stringa) {

        // creiamo una
        Map<Integer, OrdineEvaso> copiaOrdiniEvasi = new HashMap<>(ordiniEvasi);

        // parser dell'input
        int month = Integer.parseInt(stringa.substring(0, 2));
        int year = Integer.parseInt(stringa.substring(2, 6));
        System.out.println(month + " " + year);

        // crea lista filtrata
        LinkedList<OrdineEvaso> ordiniEvasiFiltrati = new LinkedList<>();

        // filtriamo per mese e anno
        for (Map.Entry<Integer, OrdineEvaso> entry : copiaOrdiniEvasi.entrySet()) {

            // Si esegua il parser del timestamp
            Long inttimestamp = entry.getValue().getTimestamp();
            LocalDateTime datetime = Instant.ofEpochSecond(inttimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // controlliamo se è un elemento da filtrarte in base all' input stringa
            if (datetime.getMonthValue() == month &&
                    datetime.getYear() == year) {
                ordiniEvasiFiltrati.add(entry.getValue());
            }
        }

        // ritorna l'oggetto OrderHistoryResponse per la risposta all utente
        try {
            return new OrderHistoryResponse(ordiniEvasiFiltrati.getFirst().getPrice(), ordiniEvasiFiltrati.getLast().getPrice(),
                    OrderHistory.trovaMaxOrd(ordiniEvasiFiltrati).getPrice(), OrderHistory.trovaMinOrd(ordiniEvasiFiltrati).getPrice(), 100,"");
        } catch (NoSuchElementException ex) {
            System.out.println("[+] Elementi non presenti per il periodo richiesto");
            return new OrderHistoryResponse(-1, -1, -1, -1, 101,"Errore mese non presente");
        }

    }

    public static OrdineEvaso trovaMaxOrd(LinkedList<OrdineEvaso> ordiniEvasiFiltrati) {
        OrdineEvaso maxOrdine = null;
        for (OrdineEvaso ordine : ordiniEvasiFiltrati) {
            if (maxOrdine == null) {
                maxOrdine = ordine;
            } else {
                if (Math.max(maxOrdine.getPrice(), ordine.getPrice()) == ordine.getPrice())
                    maxOrdine = ordine;
            }
        }
        return maxOrdine;
    }

    public int getMaxId() {
        int maxId = 0; // poichè l'ordine 0 non esiste perchè consideriamo che gl'ordini partino da 1
        for (Map.Entry<Integer, OrdineEvaso> entry : ordiniEvasi.entrySet()) {
            if (maxId == 0) {
                maxId = entry.getKey();
            } else {
                if ((Math.max(maxId, entry.getKey()) != maxId))
                    maxId = entry.getKey();
            }
        }

        return maxId;
    }

    public static OrdineEvaso trovaMinOrd(LinkedList<OrdineEvaso> ordiniEvasiFiltrati) {
        OrdineEvaso minOrdine = null;
        for (OrdineEvaso ordine : ordiniEvasiFiltrati) {
            if (minOrdine == null) {
                minOrdine = ordine;
            } else {
                if (Math.min(minOrdine.getPrice(), ordine.getPrice()) == ordine.getPrice())
                    minOrdine = ordine;
            }
        }
        return minOrdine;
    }
}
