import java.io.*;
import java.net.*;

import org.fusesource.jansi.Ansi;

import com.google.gson.*;
//import java.io.*;

public class Register implements ComandoStrategy {

    private User utenteCorrente;

    public void esegui(String[] parameters, Socket socket) {
        //System.out.println("Register's command is executed  ..");

        if (parameters.length < 2) {
            System.out.println("Mancano Username/Password");
            return;
        }

        Gson gson = new Gson();
        User utente = new User(parameters[1], parameters[2],"offline");
        Request r = new Request("register", utente);
        String message = gson.toJson(r);

        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // invio del messaggio
            out.println(message);
            //System.out.println("messaggio inviato");

            // attesa ricesione
            //System.out.println("messaggio in attesa");
            String jsonResponse = in.readLine();
            
            //output al client
            AutResponse response = AutResponse.desMessage(jsonResponse);
            if(response.getCode() == 100)
                utenteCorrente = utente;

            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(response).reset());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUserCorrente(){
        return utenteCorrente;
    }
}
