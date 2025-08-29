package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.fusesource.jansi.Ansi;

import Properties.Prop;

public class ClientMain {
    public static void main(String[] args) throws IOException {

        // config del Client
        Prop config = new Prop("resources/client.properties");

        // inizializzazione delle variabili di configurazione
        int timeout = config.getInt("timeout");

        // inzializzazione del timer
        AtomicInteger timer = new AtomicInteger(timeout);
        System.out.println("timeout value is: " + timer.get());
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new LogoutUserHandler(timer));

        // input da tastiera
        Scanner in = new Scanner(System.in);

        // inizializza menu
        MenuContext menu = new MenuContext();

        // System.out.println(ip);
        boolean end = false;

        // titolo della schermata principale
        ClearScreen.clearScreen();  //server solo a pulire lo schermo
        System.out.println(Ansi.ansi().fgYellow().a("CROSS: an exChange oRder bOokS Service").reset());

        // gestione della comunicazione col server
        try (Socket socket = new Socket(config.get("server.host"), config.getInt("server.port"))) {

            // thread centro gestione notifiche in arrivo dal server
            NotifyHandler notifyHandler = new NotifyHandler(socket);
            ExecutorService notifyService = Executors.newSingleThreadExecutor();
            notifyService.submit(notifyHandler);

            while (end != true) {
                if (menu.getUtenteCorrente() != null)
                    System.out.print(menu.getUtenteCorrente().getUsername() + "> ");
                else
                    System.out.print("> ");
                String command = in.nextLine();
                timer.set(timeout);
                if (command.toLowerCase().equals("exit")){
                    end = true;
                    notifyHandler.stop();
                    notifyService.shutdownNow();
                }
                else if (command.toLowerCase().equals("clean")) {
                    ClearScreen.clearScreen();
                    System.out.println(Ansi.ansi().fgYellow().a("CROSS: an exChange oRder bOokS Service").reset());
                } else if (command.toLowerCase().equals("help")) {
                    System.out.println("Command list of Cross App:");
                    System.out.println("Register (username password) -> registra nuovo utente");
                    System.out.println("Login (username password) -> identifica utente per l' accesso");
                    System.out.println("Logout  -> scollega utente");
                    System.out.println(
                            "UpdateCredentials (username oldPassword newpassword) -> aggiorna credenziali utente");
                    System.out.println("InsertMarketOrder (tipo dimensione) -> inserisci uno MarketOrder");
                    System.out.println("InsertStopOrder  (tipo dimensione stopPrice)-> inserisci uno StopOrder");
                    System.out.println("InsertLimitOrder (tipo dimensione prezzoLimite) -> inserisci uno LimitOrder");
                    System.out.println("CancelOrder  -> cancella ordine ancora non evaso");
                    System.out.println("Clean  -> pulisce la schermata");
                } else
                    menu.eseguiComando(command, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            service.shutdownNow();
            in.close();
        }
    }
}
