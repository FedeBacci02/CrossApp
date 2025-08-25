package Order;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.google.gson.Gson;

import Utenti.UserConnected;

public class NotificaOrdine {
    OrdineEvaso ordineEvaso;

    public NotificaOrdine(OrdineEvaso ordineEvaso) {
        this.ordineEvaso = ordineEvaso;
    }

    public void inviaOrdine(UserConnected utente) {
        System.out.println(utente.getPort());
        try (DatagramSocket ds = new DatagramSocket();) {
            Gson gson = new Gson();
            String json = gson.toJson(this.ordineEvaso);
            byte[] data = json.getBytes("US-ASCII");

            DatagramPacket notify = new DatagramPacket(data, data.length, utente.getAddress(), utente.getPort());
            System.out.println("notifica inviata");
            ds.send(notify);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }
}
