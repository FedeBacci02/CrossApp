
import java.util.*;
import com.google.gson.*;

public class RegisterResponse implements response {

    public static final Map<Integer, String> codici = new HashMap<>();

    static {
        codici.put(100, "OK");
        codici.put(101, "invalid password");
        codici.put(102, "username not available");
        codici.put(103, "other error cases");
    }

    @Override
    public void sendResponse(int number) {
        // send response for register request
        responseFormat r = new responseFormat(number, codici.get(number));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(r);
        System.out.println("test :" + jsonString);
    }
}
