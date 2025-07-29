import java.io.*;
import java.net.*;

import org.fusesource.jansi.Ansi;

import com.google.gson.*;

import Order.Order;
import Order.OType;

public class insertLimitOrder implements ComandoStrategy {

    public User getUserCorrente() {
        return null;
    }

    public void esegui(String[] parameters, Socket socket) {
        OType type;
        int size;
        int price;

        if (parameters.length < 4) {
            System.out.println("Mancano parametri");
            return;
        }

        try {
            if (parameters[1].toLowerCase().equals("ask")) {
                // tipo selezionato ask
                type = OType.ASK;
                size = Integer.parseInt(parameters[2]);
                price = Integer.parseInt(parameters[3]);
            } else if (parameters[1].toLowerCase().equals("bid")) {
                // tipo selezionato bid
                type = OType.BID;
                size = Integer.parseInt(parameters[2]);
                price = Integer.parseInt(parameters[3]);
            } else {
                System.out.println("type non corretto: selezionale ASK o BID");
                return;
            }

            Order ordine = new Order(type, size, price);

            Gson gson = new Gson();
            Request r = new Request("insertlimitorder", ordine);
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
            AutResponse response = AutResponse.desMessage(jsonResponse);

            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(response).reset());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}