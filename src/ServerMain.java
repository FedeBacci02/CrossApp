import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.*;

import Order.OrderBook;
import Order.OrderHistory;

import org.fusesource.jansi.Ansi;

public class ServerMain {
    public static void main(String[] args) throws Exception {

        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("[+] Cross server is loading").reset());

        //struttura dati per permettere al server di mantenere gli utenti registrati
        ConcurrentHashMap <String,UserConnected> users = new ConcurrentHashMap<>();

        //carica Ordini storici
        OrderHistory oHistory = new OrderHistory();
        oHistory.loadOrdersFromFile("resources/storicoOrdini.json");
        System.out.println(oHistory.filtraPerMese("10/2024"));

        //OrderBook
        OrderBook orderbook = new OrderBook();
        orderbook.visualizzaOrderBook();
        
        //orderId inizializzazione
        AtomicInteger newid = new AtomicInteger(0);

        //caricamento degl'utenti registrati
        File input = new File("resources/users.json");
        JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
        JsonArray jsonArrayOfUsers = fileElement.getAsJsonArray();

        for(JsonElement user: jsonArrayOfUsers){
            JsonObject userJsonObject = user.getAsJsonObject();
            String username = userJsonObject.get("username").getAsString();
            String password = userJsonObject.get("password").getAsString();
            String status = userJsonObject.get("status").getAsString();
            users.put(username, new UserConnected(username,password,status));
        }

        System.out.println(users.toString());

        //assegnazione di un thread x ogni client che vuole connettersi al server
        try (ServerSocket listener = new ServerSocket(1234)) {
            System.out.println(Ansi.ansi().bgBright(Ansi.Color.RED).fg(Ansi.Color.WHITE).a("[+] Cross server is running ..").reset());
            ExecutorService pool = Executors.newFixedThreadPool(20);
            while (true) {
                pool.execute(new CrossServer(listener.accept(),users,orderbook,newid, oHistory));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
