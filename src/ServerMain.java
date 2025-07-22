import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class ServerMain {
    public static void main(String[] args) throws Exception {

        //UserRegister users = new UserRegister();

        try (ServerSocket listener = new ServerSocket(1234)) {
            System.out.println("Cross server is running ..");
            System.out.println("sium");
            ExecutorService pool = Executors.newFixedThreadPool(20);
            while (true) {
                pool.execute(new CrossServer(listener.accept()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
