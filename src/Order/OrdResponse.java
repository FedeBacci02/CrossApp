package Order;

import com.google.gson.*;

//risposta di tipo ordine e serve per inviare al client o -1 in caso di errore o
//l' id dell'ordine in caso di conferma dell'ordine 

public class OrdResponse {

    private int orderId;

    public OrdResponse(int orderId) {
        this.orderId = orderId;
    }

    public static OrdResponse desMessage(String jsonMessage) {
        Gson gson = new Gson();
        OrdResponse message = gson.fromJson(jsonMessage, OrdResponse.class);
        return message;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if (this.orderId == -1) {
            sb.append("error!");
        } else {
            sb.append("orderID: ").append(orderId);
        }

        return sb.toString();
    }
}