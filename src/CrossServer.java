import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.fusesource.jansi.Ansi;

import com.google.gson.*;
import java.io.*;

public class CrossServer implements Runnable {
    private Socket socket;
    private ConcurrentHashMap<String, User> users;
    private User utente;

    public CrossServer(Socket socket, ConcurrentHashMap<String, User> users) {
        this.socket = socket;
        this.users = users;
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
                    User newUtente = gson.fromJson(values, User.class);
                    System.out.println("[+] " + newUtente.getUsername() + " tenta la registrazione");

                    if (newUtente.getPassword().isEmpty()) {
                        // registrazione non completata perchè password è vuota
                        RegisterResponse risposta = new RegisterResponse(101, "invalid password");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    }
                    if (users.containsKey(newUtente.getUsername())) {
                        // registrazione non completata perchè password è vuota
                        RegisterResponse risposta = new RegisterResponse(102, "username is not available");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    } else {
                        // registrazione completata
                        utente = newUtente;
                        utente.setStatus("online");
                        RegisterResponse risposta = new RegisterResponse(100, "OK");
                        String jsonResponse = gson.toJson(risposta);
                        users.put(utente.getUsername(), utente); // la chiave è l'username poichè è univoca
                        System.out.println("[+] " + utente.getUsername() + " si è registrato con successo!");
                        out.println(jsonResponse);
                    }
                }

                if (r.getOperation().equals("login")) {
                    String values = gson.toJson(r.getValues());
                    User newUtente = gson.fromJson(values, User.class);
                    System.out.println("[+] " + newUtente.getUsername() + " tenta l'accesso al server");

                    if (users.containsKey(newUtente.getUsername())) {
                        if (users.get(newUtente.getUsername()).getPassword().equals(newUtente.getPassword())) {
                            // accesso consentito
                            if (!users.get(newUtente.getUsername()).getStatus().equals("online")) {
                                utente = newUtente;
                                utente.setStatus("online");
                                users.put(utente.getUsername(), utente);
                                LoginResponse risposta = new LoginResponse(100, "OK");
                                String jsonResponse = gson.toJson(risposta);
                                out.println(jsonResponse);
                            } else {
                                LoginResponse risposta = new LoginResponse(102, "user already logged");
                                String jsonResponse = gson.toJson(risposta);
                                out.println(jsonResponse);
                            }
                        } else {
                            LoginResponse risposta = new LoginResponse(101,
                                    "Username/password mismatch or non existent username");
                            String jsonResponse = gson.toJson(risposta);
                            out.println(jsonResponse);
                        }
                    }else{
                        LoginResponse risposta = new LoginResponse(103,
                                    "other error cases");
                            String jsonResponse = gson.toJson(risposta);
                            out.println(jsonResponse);
                    }
                }

                if (r.getOperation().equals("logout")) {
                    if (utente == null) {
                        LoginResponse risposta = new LoginResponse(101,
                                " user not logged in or other error  cases");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    } else {
                        utente.setStatus("offline");
                        users.put(utente.getUsername(),utente);
                        utente = null;
                        LoginResponse risposta = new LoginResponse(100,
                                "OK");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    }
                }
                System.out.println(users.toString());
            }

        } catch (Exception e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("[+] Socket: " + socket.getInetAddress() + " è stato chiuso!!"));
            e.printStackTrace();
        }
    }
}
