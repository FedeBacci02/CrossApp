package Server;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.stream.JsonWriter;

import Order.OrderHistory;
import Utenti.UserConnected;

public class SalvataggioDati implements Runnable {

    private OrderHistory oHistory;
    ConcurrentHashMap<String, UserConnected> users;
    int timer;

    public SalvataggioDati(OrderHistory oHistory, ConcurrentHashMap<String, UserConnected> users, int timer) {
        this.oHistory = oHistory;
        this.users = users;
        this.timer = timer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                //il thread è in pausa per tot secondi
                Thread.sleep(timer * 60 * 1000);

                // salvataggio della struttura dati oHistory aggiornata con i nuovi ordini
                System.out.println("[+] Salvataggio nei file in corso");
                oHistory.saveHistory("resources/storicoOrdini.json");
                System.out.println("[+] Salvataggio ordini eseguito");

                // salvataggio della struttura dati users con i nuovi utenti registrati
                JsonWriter writer;
                try {
                    writer = new JsonWriter(new FileWriter("resources/users.json"));
                    writer.setIndent("  "); // due spazi per indentare

                    writer.beginObject();
                    writer.name("users");
                    writer.beginArray();
                    for (Map.Entry<String, UserConnected> entry : users.entrySet()) {
                        writer.beginObject();
                        writer.name("username").value(entry.getValue().getUsername());
                        writer.name("password").value(entry.getValue().getPassword());
                        writer.endObject();
                    }
                    writer.endArray();
                    writer.endObject();
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                System.out.println("[+] Salvataggio utenti eseguito");
            }

        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}
