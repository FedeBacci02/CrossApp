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
               
                String x = in.nextLine();
                System.out.println(x);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                //String y = gson.fromJson(null, null)
   
            }
        }catch (Exception e){
            System.out.println("Error:" + socket);
        }


    }

}
