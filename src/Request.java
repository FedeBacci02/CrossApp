import com.google.gson.*;

public class Request{
    private String operation;
    private Object values;

    public Request(String operation, Object values) {
        this.operation = operation;
        this.values = values;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(this);
        return jsonString;
    }

    public String getOperation() {
        return operation;
    }

    public Object getValues() {
        return values;
    }

}
