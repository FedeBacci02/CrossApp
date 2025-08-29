package Server;
import com.google.gson.*;

public class RegistrationMessage {
    private int udpPort;

    public RegistrationMessage(int udpPort) {
        this.udpPort = udpPort;
    }

    public static RegistrationMessage desMessage(String jsonMassage){
        Gson gson = new Gson();
        return gson.fromJson(jsonMassage, RegistrationMessage.class);
        
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

}
