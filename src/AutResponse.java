public class AutResponse {

    private int responde;
    private String errorMessage;

    public AutResponse(int responde, String errorMessage) {
        this.responde = responde;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return responde;
    }

    public void setCode(int responde) {
        this.responde = responde;
    }

    public String getMessage() {
        return errorMessage;
    }

    public void setMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
