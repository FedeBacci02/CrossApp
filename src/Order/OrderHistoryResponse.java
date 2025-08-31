package Order;

import com.google.gson.Gson;

//risposta di tipo order history

public class OrderHistoryResponse {
    private int code ;
    private int oApertura, oChiusura, oMax, oMin;
    private String message;

    public OrderHistoryResponse(int oApertura, int oChiusura, int oMax, int oMin,int code, String message) {
        this.oApertura = oApertura;
        this.oChiusura = oChiusura;
        this.oMax = oMax;
        this.oMin = oMin;
        this.code = code;
        this.message = message;
    }

    public int getoApertura() {
        return oApertura;
    }

    public void setoApertura(int oApertura) {
        this.oApertura = oApertura;
    }

    public int getoChiusura() {
        return oChiusura;
    }

    public void setoChiusura(int oChiusura) {
        this.oChiusura = oChiusura;
    }

    public int getoMax() {
        return oMax;
    }

    public void setoMax(int oMax) {
        this.oMax = oMax;
    }

    public int getoMin() {
        return oMin;
    }

    public void setoMin(int oMin) {
        this.oMin = oMin;
    }

    @Override
    public String toString() {
        return "oApertura=" + oApertura + "\n, oChiusura=" + oChiusura + "\n, oMax=" + oMax
                + "\n, oMin=" + oMin;
    }

    public static OrderHistoryResponse desMessage(String jsonMessage) {
        Gson gson = new Gson();
        OrderHistoryResponse message = gson.fromJson(jsonMessage, OrderHistoryResponse.class);
        return message;
    }



    public int getCode() {
        return code;
    }



    public void setCode(int code) {
        this.code = code;
    }



    public String getMessage() {
        return message;
    }



    public void setMessage(String message) {
        this.message = message;
    }

}
