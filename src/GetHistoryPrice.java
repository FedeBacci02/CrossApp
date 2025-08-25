import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.fusesource.jansi.Ansi;

import com.google.gson.Gson;

import Order.GetPriceHistoryRequest;
import Order.OrderHistoryResponse;
import Utenti.User;

public class GetHistoryPrice implements ComandoStrategy {

    @Override
    public void esegui(String[] parameters, Socket socket) {
        if (parameters.length < 2) {
            System.out.println("Sintassi errata> getPriceHistory mese (MMYYYY)");
            return;
        }

        if (!GetPriceHistoryRequest.isMonth(parameters[1])) {
            System.out.println("Sintassi mese errata> getPriceHistory mese (MMYYYY)");
            return;
        }

        Gson gson = new Gson();
        Request r = new Request("getpricehistory", new GetPriceHistoryRequest(parameters[1]));
        String message = gson.toJson(r);

        try {

            // inizializzazione delle variabili di stream
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // invio del messaggio
            out.println(message);
            // System.out.println("messaggio inviato");

            // attesa ricesione
            // System.out.println("messaggio in attesa");
            String jsonResponse = in.readLine();

            // output al client
            OrderHistoryResponse response = OrderHistoryResponse.desMessage(jsonResponse);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(response.toString()).reset());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public User getUserCorrente() {

        return null;
    }

}
