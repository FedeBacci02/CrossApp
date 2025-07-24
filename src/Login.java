import java.net.*;

import org.fusesource.jansi.Ansi;

import java.io.*;

import com.google.gson.Gson;

public class Login implements ComandoStrategy {

    public void esegui(String[] parameters, Socket socket) {
        System.out.println("Login's command is executed  ..");

        if (parameters.length < 2) {
            System.out.println("Mancano Username/Password");
            return;
        }

        Gson gson = new Gson();
        User utente = new User(parameters[1], parameters[2], "offline");
        Request r = new Request("login", utente);
        String message = gson.toJson(r);

        try {
            
            // inizializzazione delle variabili di stream
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // invio del messaggio
            out.println(message);
            System.out.println("messaggio inviato");

            // attesa ricesione
            System.out.println("messaggio in attesa");
            String jsonResponse = in.readLine();

            //output al client
            AutResponse response = AutResponse.desMessage(jsonResponse);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(response).reset());

            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
