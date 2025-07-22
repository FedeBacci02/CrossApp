import java.net.*;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class CrossServer implements Runnable{
    private Socket socket;

    public CrossServer(Socket socket) {
        this.socket = socket;
    }

    public void run(){
        System.out.println("Connected: " + socket);

        //servizio di richiesta
        try(Scanner in = new Scanner(socket.getInputStream())){
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            while (in.hasNextLine()) {
                System.out.println(in.nextLine());
                out.println("Registrazione completata");
                out.flush();
            }
        }catch (Exception e){
            System.out.println("Error:" + socket);
        }


    }

}
