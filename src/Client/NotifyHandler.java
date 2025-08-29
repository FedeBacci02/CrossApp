package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

import com.google.gson.Gson;

import Server.RegistrationMessage;

public class NotifyHandler implements Runnable {

    private Socket socket;
    private DatagramSocket ds;

    public NotifyHandler(Socket socket) {
        this.socket = socket;
    }

    public void stop() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
        }
    }

    // handler che gestisce le notifiche lato client
    public void run() {

        try {
            ds = new DatagramSocket();
            // invio al server della porta udp in ascolto
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Gson gson = new Gson();
            String jsonMessage = gson.toJson(new Request("start", new RegistrationMessage(ds.getLocalPort())));
            out.println(jsonMessage);

            while (true) {

                DatagramPacket response = new DatagramPacket(new byte[1024], 1024);

                // attesa del messaggio dal server
                Thread.sleep(1000);

                // System.out.println("client in attesa di notifiche dal server");

                // attesa ricezione di notifiche dal server
                ds.receive(response);

                // costruiamo il messaggio di notifica
                String msg = new String(response.getData(), 0, response.getLength());
                System.out.println("\n Notifica di completamento ordine: \n" + msg);
            }
        } catch (SocketException e) {
            // viene lanciata quando chiudi il socket -> uscita normale
            System.out.println("NotifyHandler chiuso.");
            Thread.currentThread().interrupt();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}