import java.net.*;
import java.util.Scanner;
import com.google.gson.*;
import java.io.*;

public class CrossServer implements Runnable {
    private Socket socket;

    public CrossServer(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        System.out.println("[+] Connected: " + socket);
        Gson gson = new Gson();

        // servizio di richiesta
        try (Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // legge il file json
            while (in.hasNextLine()) {
                String jsonRequest = in.nextLine();
                Request r = gson.fromJson(jsonRequest, Request.class);
                System.out.println("[+] " + r.toString());

                if (r.getOperation().equals("register")) {
                    // estraiamo l'utente
                    String values = gson.toJson(r.getValues());
                    User utente = gson.fromJson(values, User.class);
                    System.out.println("[+] " + utente.getUsername() + " tenta la registrazione");

                    if (utente.getPassword().isEmpty()) {
                        RegisterResponse risposta = new RegisterResponse(101, "invalid password");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    }else{
                        RegisterResponse risposta = new RegisterResponse(100, "OK");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("lol");
        }
    }
}
