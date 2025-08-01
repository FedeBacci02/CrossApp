package Order;

import com.google.gson.*;

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