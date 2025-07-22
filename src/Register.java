import com.google.gson.*;
import java.io.*;

public class Register implements ComandoStrategy {
    public void esegui(String [] parameters) {
        System.out.println("Register's command is executed  ..");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        User utente = new User(parameters[1], parameters[2]);
        Request r = new Request("register", utente);
        String message = gson.toJson(r);
        System.out.println("lol");
        System.out.println(message);
    }
}
