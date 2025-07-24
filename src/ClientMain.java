import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import org.fusesource.jansi.Ansi;

public class ClientMain {
    public static void main(String[] args) {

        // controllo se l'input da CLI
        if (args.length != 1) {
            System.err.println("Pass thr server IP as the sole command line argument");
            return ;
        }

        // input da tastiera
        Scanner in = new Scanner(System.in);

        // inizializza menu
        MenuContext menu = new MenuContext();

        // System.out.println(ip);
        boolean end = false;

        //titolo della scermata principale
        ClearScreen.clearScreen();
        System.out.println(Ansi.ansi().fgYellow().a("CROSS: an exChange oRder bOokS Service").reset());

        // gestione della comunicazione col server
        try (Socket socket = new Socket(args[0], 1234)) {
            while (end != true) {
                if(menu.getUtenteCorrente() != null)
                    System.out.print( menu.getUtenteCorrente().getUsername()+"> ");
                else
                    System.out.print("> ");
                String command = in.nextLine();
                if (command.toLowerCase().equals("exit"))
                    end = true;
                else if (command.toLowerCase().equals("clean")){
                    ClearScreen.clearScreen();
                    System.out.println(Ansi.ansi().fgYellow().a("CROSS: an exChange oRder bOokS Service").reset());
                }
                else if (command.toLowerCase().equals("help")){
                    System.out.println("Command list of Cross App:");
                    System.out.println("Register username password -> registra nuovo utente");
                    System.out.println("Login username password -> identifica utente per l' accesso");
                    System.out.println("Logout  -> scollega utente");
                    System.out.println("UpdateCredentials username oldPassword newpassword -> aggiorna credenziali utente");
                    System.out.println("Clean  -> pulisce la schermata");
                }
                else
                    menu.eseguiComando(command,socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
    }
}
