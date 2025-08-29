package Order;

import com.google.gson.Gson;

public class OrderHistoryResponse {
    private int code ;
    private OrdineEvaso oApertura, oChiusura, oMax, oMin;

    public OrderHistoryResponse(OrdineEvaso oApertura, OrdineEvaso oChiusura, OrdineEvaso oMax, OrdineEvaso oMin,int code) {
        this.oApertura = oApertura;
        this.oChiusura = oChiusura;
        this.oMax = oMax;
        this.oMin = oMin;
        this.code = code;
    }

    public OrdineEvaso getoApertura() {
        return oApertura;
    }

    public void setoApertura(OrdineEvaso oApertura) {
        this.oApertura = oApertura;
    }

    public OrdineEvaso getoChiusura() {
        return oChiusura;
    }

    public void setoChiusura(OrdineEvaso oChiusura) {
        this.oChiusura = oChiusura;
    }

    public OrdineEvaso getoMax() {
        return oMax;
    }

    public void setoMax(OrdineEvaso oMax) {
        this.oMax = oMax;
    }

    public OrdineEvaso getoMin() {
        return oMin;
    }

    public void setoMin(OrdineEvaso oMin) {
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

}
