package Server;
import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.*;

import Order.OrderBook;
import Order.OrderHistory;
import Properties.Prop;
import Utenti.UserConnected;

import org.fusesource.jansi.Ansi;

public class ServerMain {
    public static void main(String[] args) throws Exception {

        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("[+] Cross server is loading").reset());

        // struttura dati per permettere al server di mantenere gli utenti registrati
        ConcurrentHashMap<String, UserConnected> users = new ConcurrentHashMap<>();

        // config del server
        Prop config = new Prop("resources/server.properties");

        // carica Ordini storici
        OrderHistory oHistory = new OrderHistory();
        oHistory.loadOrdersFromFile("resources/storicoOrdini.json");
        // System.out.println(oHistory.filtraPerMese("102024").toString());

        //thread per persistere i dati ogni 2 min
        ExecutorService saveService = Executors.newSingleThreadExecutor();
        saveService.submit(new SalvataggioDati(oHistory,users,config.getInt("timer")));

        // OrderBook
        OrderBook orderbook = new OrderBook();
        orderbook.visualizzaOrderBook();

        // orderId inizializzazione
        AtomicInteger newid = new AtomicInteger(oHistory.getMaxId());
        System.out.println("order id start value: " + newid.get());

        // caricamento degl'utenti registrati
        File input = new File("resources/users.json");
        JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
        JsonObject jsonUsers = fileElement.getAsJsonObject();
        JsonArray jsonArrayOfUsers = jsonUsers.get("users").getAsJsonArray();

        for (JsonElement user : jsonArrayOfUsers) {
            JsonObject userJsonObject = user.getAsJsonObject();
            String username = userJsonObject.get("username").getAsString();
            String password = userJsonObject.get("password").getAsString();
            users.put(username, new UserConnected(username, password, "offline"));
        }

        // output degl'utenti caricati dal file
        System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("[+] update user register: ").reset());
        for (Map.Entry<String, UserConnected> entry : users.entrySet()) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a(entry.getValue().toString()).reset());
        }

        // assegnazione di un thread x ogni client che vuole connettersi al server
        try (ServerSocket listener = new ServerSocket(config.getInt("server.port"))) {
            System.out.println(Ansi.ansi().bgBright(Ansi.Color.RED).fg(Ansi.Color.WHITE)
                    .a("[+] Cross server is running ..").reset());
            ExecutorService pool = Executors.newFixedThreadPool(config.getInt("max_users"));
            while (true) {
                pool.execute(new CrossServer(listener.accept(), users, orderbook, newid, oHistory));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
