import java.net.*;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.fusesource.jansi.Ansi;

import com.google.gson.*;

import Order.*;

import java.io.*;

public class CrossServer implements Runnable {
    private Socket socket;
    private ConcurrentHashMap<String, UserConnected> users;
    private UserConnected utente = null;
    OrderBook orderBook = null;
    AtomicInteger newid;

    public CrossServer(Socket socket, ConcurrentHashMap<String, UserConnected> users, OrderBook orderBook, AtomicInteger newid) {
        this.socket = socket;
        this.users = users;
        this.orderBook = orderBook;
        this.newid = newid;
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
                    User userToConnect = gson.fromJson(values, User.class);
                    UserConnected newUtente = UserConnected.toConnect(userToConnect);
                    System.out.println("[+] " + newUtente.getUsername() + " is trying to register");

                    if (newUtente.getPassword().isEmpty()) {
                        // registrazione non completata perchè password è vuota
                        AutResponse risposta = new AutResponse(101, "invalid password");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    } else if (users.containsKey(newUtente.getUsername())) {
                        // registrazione non completata perchè username esiste già
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
                    User userToConnect = gson.fromJson(values, User.class);
                    UserConnected newUtente = UserConnected.toConnect(userToConnect);
                    System.out.println("[+] " + newUtente.getUsername() + " is trying to log in the server");

                    if (users.containsKey(newUtente.getUsername())) {
                        if (users.get(newUtente.getUsername()).getPassword().equals(newUtente.getPassword())) {
                            // accesso consentito
                            if (!users.get(newUtente.getUsername()).getStatus().equals("online")) {
                                if (utente != null) {
                                    AutResponse risposta = new AutResponse(103, "other cases error");
                                    String jsonResponse = gson.toJson(risposta);
                                    out.println(jsonResponse);
                                } else {
                                    utente = newUtente;
                                    utente.setStatus("online");
                                    users.put(utente.getUsername(), utente);
                                    AutResponse risposta = new AutResponse(100, "OK");
                                    String jsonResponse = gson.toJson(risposta);
                                    out.println(jsonResponse);
                                }
                            } else {
                                AutResponse risposta = new AutResponse(103, "fother error cases");
                                String jsonResponse = gson.toJson(risposta);
                                out.println(jsonResponse);
                            }
                        } else {
                            AutResponse risposta = new AutResponse(102,
                                    "Username/password mismatch or non existent username");
                            String jsonResponse = gson.toJson(risposta);
                            out.println(jsonResponse);
                        }
                    } else {
                        AutResponse risposta = new AutResponse(101,
                                "Username/password mismatch or non existent username");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    }
                }

                if (r.getOperation().equals("logout")) {
                    if (utente == null) {
                        AutResponse risposta = new AutResponse(101,
                                " user not logged in or other error  cases");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    } else {
                        utente.toOffline();
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
                    System.out.println(
                            "[+] " + newUtente.getUsername() + " is trying to uddate credentials in the server");

                    if (users.containsKey(newUtente.getUsername())) {
                        if (!(utente == null)) {
                            System.out.println("[+]Error: User currently logged");

                            AutResponse risposta = new AutResponse(104,
                                    "user currently logged");
                            String jsonResponse = gson.toJson(risposta);
                            out.println(jsonResponse);
                        } else if (users.get(newUtente.getUsername()).getPassword()
                                .equals(newUtente.getOldPassword())) {
                            if (!(newUtente.getOldPassword() == newUtente.getPassword())) {
                                // CASO IN CUI SIA TUTTO GIUSTO
                                System.out.println("[+]OK: Username is in the register");

                                // aggiornamento del register degl'utenti registrati
                                users.put(newUtente.getUsername(),
                                        new UserConnected(newUtente.getUsername(), newUtente.getPassword(), "offline"));

                                // invio risposta al client
                                AutResponse risposta = new AutResponse(100,
                                        "OK");
                                String jsonResponse = gson.toJson(risposta);
                                out.println(jsonResponse);
                            } else {
                                System.out.println("[+]Error: Password equal to old one");

                                AutResponse risposta = new AutResponse(100,
                                        "new password equal to old one");
                                String jsonResponse = gson.toJson(risposta);
                                out.println(jsonResponse);
                            }
                        } else {
                            // invio messaggio di errore al client
                            System.out.println(Ansi.ansi().fg(Ansi.Color.RED)
                                    .a("[+]Error: old password is not in the register at the username "
                                            + newUtente.getUsername())
                                    .reset());

                            System.out.println("    " + users.get(newUtente.getUsername()).getPassword() + " == "
                                    + newUtente.getOldPassword());

                            AutResponse risposta = new AutResponse(102,
                                    "username/old password mismatch or non existent username");
                            String jsonResponse = gson.toJson(risposta);
                            out.println(jsonResponse);
                        }
                    } else {
                        // caso in cui il nome utente non è presente nel register
                        System.out.println("[+]Error: Username is not in the register");

                        AutResponse risposta = new AutResponse(102,
                                "username/old password mismatch or non existent username");
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    }
                }

                if (r.getOperation().equals("insertmarketorder") || r.getOperation().equals("insertlimitorder")
                        || r.getOperation().equals("insertstoporder")) {
                    int code = 0;
                    if (utente == null) {
                        // risposta
                        OrdResponse risposta = new OrdResponse(-1);
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    } else {
                        // si estrae l'oggetto order dalla request json
                        String values = gson.toJson(r.getValues());
                        Order newOrder = gson.fromJson(values, Order.class);

                        // creiamo oggetto ordine da valutare
                        EvaluatingOrder ordine = new EvaluatingOrder(newOrder.getType(), newOrder.getSize(),
                                newOrder.getPrice(), utente.getUsername(), newid.incrementAndGet(), 0,
                                r.getOperation());

                        // creazione oggetto context
                        OrderContext orderContext = new OrderContext(ordine, orderBook);

                        // seleziona la strategia da applicare
                        orderContext.setStrategy(ordine.getOrderType());

                        // elaborare l'ordine
                        code = orderContext.elaboraOrdine();

                        // risposta al client
                        if (code == -1) {
                            OrdResponse risposta = new OrdResponse(code);
                            String jsonResponse = gson.toJson(risposta);
                            out.println(jsonResponse);
                        }
                        OrdResponse risposta = new OrdResponse(ordine.getOrderId());
                        String jsonResponse = gson.toJson(risposta);
                        out.println(jsonResponse);
                    }
                }

                if (r.getOperation().equals("cancelorder")) {

                    AutResponse risposta;
                    System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("[+] "+ utente + " tenta di cancellare un ordine ").reset());
                    if (utente == null) {
                        // risposta
                        risposta = new AutResponse(102,
                                "utente non loggato");
                    } else {
                        String values = gson.toJson(r.getValues());
                        CancelOrderRequest orderid = gson.fromJson(values, CancelOrderRequest.class);
                        int code = orderBook.cancelOrder(orderid.getOrderId());
                        if (code == 1) {
                            risposta = new AutResponse(100,
                                    "OK");
                            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("[+] "+ utente + " ordine cancellato con successo ").reset());
                        } else {
                            risposta = new AutResponse(101,
                                    "order does not exist");
                            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("[+] "+ utente + " errore ").reset());
                        }

                    }


                    String jsonResponse = gson.toJson(risposta);
                    out.println(jsonResponse);
                }

                if (r.getOperation().equals("getpricehistory")) {

                }

                // output di eventuali aggiornamenti a schermo per controlli
                System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("[+] update user register: ").reset());
                for(Map.Entry<String,UserConnected> entry : users.entrySet()){
                    System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a(entry.getValue().toString()).reset());
                }
                System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("[+] update order book: ").reset());
                orderBook.visualizzaOrderBook();

            }

            // set status offline
            if (utente != null) {
                utente.toOffline();
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
