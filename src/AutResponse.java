import com.google.gson.*;;

public class AutResponse {

    private int response;
    private String errorMessage;

    public AutResponse(int response, String errorMessage) {
        this.response = response;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return response;
    }

    public void setCode(int response) {
        this.response = response;
    }

    public String getMessage() {
        return errorMessage;
    }

    public void setMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static AutResponse desMessage(String jsonMessage){
        Gson gson = new Gson();
        AutResponse message = gson.fromJson(jsonMessage, AutResponse.class);
        return message;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(response).append(" - ").append(errorMessage);
        return sb.toString();
    }
}
