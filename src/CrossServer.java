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
                System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a("[+] " + r.toString()).reset());
                if (r.getOperation().equals("register")) {
                    // estraiamo l'utente
                    String values = gson.toJson(r.getValues());
                    User newUtente = gson.fromJson(values, User.class);
                    System.out.println("[+] " + newUtente.getUsername() + " tenta la registrazione");

                    if (newUtente.getPassword().isEmpty()) {
                        // registrazione non completata perchè password è vuota
                        AutResponse risposta = new AutResponse(101, "invalid password");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    } else if (users.containsKey(newUtente.getUsername())) {
                        // registrazione non completata perchè password è vuota
                        AutResponse risposta = new AutResponse(102, "username is not available");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    } else if (utente != null) {
                        // registrazione non completata perchè già registrato e loggato
                        AutResponse risposta = new AutResponse(103, "cother error cases");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    } else {
                        // registrazione completata
                        utente = newUtente;
                        utente.setStatus("online");
                        AutResponse risposta = new AutResponse(100, "OK");
                        String jsonResponse = gson.toJson(risposta);
                        users.put(utente.getUsername(), utente); // la chiave è l'username poichè è univoca
                        System.out.println("[+] " + utente.getUsername() + " registration is successful !");
                        out.println(jsonResponse);
                    }
                }

                if (r.getOperation().equals("login")) {
                    String values = gson.toJson(r.getValues());
                    User newUtente = gson.fromJson(values, User.class);
                    System.out.println("[+] " + newUtente.getUsername() + " is trying to log in the server");

                    if (users.containsKey(newUtente.getUsername())) {
                        if (users.get(newUtente.getUsername()).getPassword().equals(newUtente.getPassword())) {
                            // accesso consentito
                            System.out.println("a");
                            if (!users.get(newUtente.getUsername()).getStatus().equals("online")) {
                                if (utente != null) {
                                    AutResponse risposta = new AutResponse(103, "other cases error");
                                    String jsonResponse = gson.toJson(risposta);
                                    out.println(jsonResponse);
                                } else {
                                    System.out.println("b");
                                    utente = newUtente;
                                    utente.setStatus("online");
                                    users.put(utente.getUsername(), utente);
                                    AutResponse risposta = new AutResponse(100, "OK");
                                    String jsonResponse = gson.toJson(risposta);
                                    out.println(jsonResponse);
                                }
                            } else {
                                System.out.println("e");
                                AutResponse risposta = new AutResponse(103, "fother error cases");
                                String jsonResponse = gson.toJson(risposta);
                                out.println(jsonResponse);
                            }
                        } else {
                            System.out.println("e");
                            AutResponse risposta = new AutResponse(102,
                                    "Username/password mismatch or non existent username");
                            String jsonResponse = gson.toJson(risposta);
                            out.println(jsonResponse);
                        }
                    } else {
                        System.out.println("f");
                        AutResponse risposta = new AutResponse(101,
                                "Username/password mismatch or non existent username");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    }
                    System.out.println("z");
                }

                if (r.getOperation().equals("logout")) {
                    if (utente == null) {
                        AutResponse risposta = new AutResponse(101,
                                " user not logged in or other error  cases");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    } else {
                        utente.setStatus("offline");
                        users.put(utente.getUsername(), utente);
                        utente = null;
                        AutResponse risposta = new AutResponse(100,
                                "OK");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    }
                }

                if (r.getOperation().equals("updateCredentials")) {
                    String values = gson.toJson(r.getValues());
                    NewUser newUtente = gson.fromJson(values, NewUser.class);
                    System.out.println("[+] " + newUtente.getUsername() + " is trying to log in the server");

                    if (users.containsKey(newUtente.getUsername())) {
                        if (!(utente == null)) {
                            AutResponse risposta = new AutResponse(104,
                                    "user currently logged");
                            String jsonResponse = gson.toJson(risposta);
                            out.println(jsonResponse);
                        } else if (users.get(newUtente.getUsername()).getPassword() == newUtente.getOldPassword()) {
                            if (!(newUtente.oldPassword == newUtente.getPassword())) {
                                // CASO IN CUI SIA TUTTO GIUSTO

                                // aggiornamento dell register degl'utenti registrati
                                users.put(newUtente.getUsername(),
                                        new User(newUtente.getUsername(), newUtente.getPassword(), "online"));

                                // invio risposta al client
                                AutResponse risposta = new AutResponse(100,
                                        "OK");
                                String jsonResponse = gson.toJson(risposta);
                                out.println(jsonResponse);
                            } else {
                                AutResponse risposta = new AutResponse(100,
                                        "new password equal to old one");
                                String jsonResponse = gson.toJson(risposta);
                                out.println(jsonResponse);
                            }
                        } else {
                            // invio messaggio di errore al client
                            AutResponse risposta = new AutResponse(102,
                                    "username/old password mismatch or non existent username");
                            String jsonResponse = gson.toJson(risposta);
                            out.println(jsonResponse);
                        }
                    } else {
                        AutResponse risposta = new AutResponse(102,
                                "username/old password mismatch or non existent username");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    }
                }

                System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("[+] update user register: ").reset());
                System.out.println(users.toString());
            }

            // set status offline
            if (utente != null) {
                utente.setStatus("offline");
                users.put(utente.getUsername(), utente);
            }

            // output per comunicazione della disconnessione volontaria del client
            System.out.println(
                    Ansi.ansi().fg(Ansi.Color.GREEN)
                            .a("[+] Socket: " + socket.getInetAddress() + " ha interrotto la connessione!!").reset());
        } catch (Exception e) {
            // output per comunicazione della disconnessione involontaria del client
            System.out.println(
                    Ansi.ansi().fg(Ansi.Color.RED).a("[+] Socket: " + socket.getInetAddress() + " è stato chiuso!!")
                            .reset());
            e.printStackTrace();
        }
    }
}
