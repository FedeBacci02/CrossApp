import java.io.*;
import java.net.*;

import org.fusesource.jansi.Ansi;

import com.google.gson.Gson;

public class updateCredentials implements ComandoStrategy {

    private User utenteCorrente;

    public void esegui(String[] parameters, Socket socket) {
        // System.out.println("Logout's command is executed ..");

        if (parameters.length < 3) {
            System.out.println("Mancano Username/Password/VecchiaPassword");
            return;
        }

        Gson gson = new Gson();
        Request r = new Request("updateCredentials",
                new NewUser(parameters[2], parameters[1], parameters[3], "offline"));
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
            AutResponse response = AutResponse.desMessage(jsonResponse);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(response).reset());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUserCorrente() {
        return utenteCorrente;
    }
}
