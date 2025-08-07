package Order;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OrderHistory {
    ConcurrentHashMap<Integer, OrdineEvaso> ordiniEvasi;

    public OrderHistory() {
        ordiniEvasi = new ConcurrentHashMap<>();
    }

    public void loadOrdersFromFile(String jsonFileName) {
        File jfile = new File(jsonFileName);
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

                LocalDateTime dateTime = Instant.ofEpochSecond(timestampInt)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                OrdineEvaso trade = new OrdineEvaso(orderId, otype, orderType, size, price, dateTime);
                ordiniEvasi.put(trade.getOrderId(), trade);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("CARICAMENTO DI " + jsonFileName + " COMPLETATO.");
        }
    }

    public void printLista() {
        for (Map.Entry<Integer, OrdineEvaso> entry : ordiniEvasi.entrySet()) {
            System.out.println(entry.getValue().toString());
        }
    }

    public String filtraPerMese(String stringa) {

        // parser dell'input
        String[] my = stringa.split("/");
        int month = Integer.parseInt(my[0]);
        int year = Integer.parseInt(my[1]);

        // crea lista filtrata
        LinkedList<OrdineEvaso> ordiniEvasiFiltrati = new LinkedList<>();

        // filtriamo per mese e anno
        for (Map.Entry<Integer, OrdineEvaso> entry : ordiniEvasi.entrySet()) {
            if (entry.getValue().getTimestamp().getMonthValue() == month &&
                    entry.getValue().getTimestamp().getYear() == year) {
                    ordiniEvasiFiltrati.add(entry.getValue());
            }
        }

        
        return
            "ordine di apertura: " + ordiniEvasiFiltrati.getFirst()
        +   "\nordine di chiusura: " + ordiniEvasiFiltrati.getLast()
        +   "\nprezzo massimo" +  OrderHistory.trovaMaxOrd(ordiniEvasiFiltrati)
        +   "\nprezzo minimo" + OrderHistory.trovaMinOrd(ordiniEvasiFiltrati);
    }

    public static OrdineEvaso trovaMaxOrd(LinkedList<OrdineEvaso> ordiniEvasiFiltrati){
        OrdineEvaso maxOrdine = null;
        for(OrdineEvaso ordine: ordiniEvasiFiltrati){
            if(maxOrdine == null){
                maxOrdine = ordine;
            }else{
                if(Math.max(maxOrdine.getPrice(), ordine.getPrice()) == ordine.getPrice())
                    maxOrdine = ordine;
            }
        }
        return maxOrdine;
    }

        public static OrdineEvaso trovaMinOrd(LinkedList<OrdineEvaso> ordiniEvasiFiltrati){
        OrdineEvaso minOrdine = null;
        for(OrdineEvaso ordine: ordiniEvasiFiltrati){
            if(minOrdine == null){
                minOrdine = ordine;
            }else{
                if(Math.min(minOrdine.getPrice(), ordine.getPrice()) == ordine.getPrice())
                    minOrdine = ordine;
            }
        }
        return minOrdine;
    }

}
