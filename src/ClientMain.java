import java.io.*;
import java.net.Socket;
import java.util.Scanner;

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

        // gestione della comunicazione col server
        try (Socket socket = new Socket(args[0], 1234)) {
            while (end != true) {
                System.out.println("> ");
                String command = in.nextLine();
                if (command.toLowerCase().equals("exit"))
                    end = true;
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
