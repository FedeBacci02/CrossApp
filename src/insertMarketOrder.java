import java.io.*;
import java.net.*;

import org.fusesource.jansi.Ansi;

import com.google.gson.*;

import Order.Order;
import Order.OType;
import Order.OrdResponse;

public class insertMarketOrder implements ComandoStrategy {

    public User getUserCorrente() {
        return null;
    }

    public void esegui(String[] parameters, Socket socket) {
        OType type;
        int size;

        if (parameters.length < 3) {
            System.out.println("Mancano parametri");
            return;
        }

        try {
            if (parameters[1].toLowerCase().equals("ask")) {
                // tipo selezionato ask
                type = OType.ASK;
                size = Integer.parseInt(parameters[2]);
            } else if (parameters[1].toLowerCase().equals("bid")) {
                // tipo selezionato bid
                type = OType.BID;
                size = Integer.parseInt(parameters[2]);
            } else {
                System.out.println("type non corretto: selezionale ASK o BID");
                return;
            }

            Order ordine = new Order(type, size, 0);

            Gson gson = new Gson();
            Request r = new Request("insertmarketorder", ordine);
            String message = gson.toJson(r);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // invio del messaggio
            out.println(message);
            // System.out.println("messaggio inviato");

            // attesa ricesione
            // System.out.println("messaggio in attesa");
            String jsonResponse = in.readLine();

            // output al client
            OrdResponse response = OrdResponse.desMessage(jsonResponse);
           

            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a( response.toString()).reset());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}