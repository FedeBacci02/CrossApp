public class Request{
    String operation;
    Object values;

    public Request(String operation, Object values) {
        this.operation = operation;
        this.values = values;
    }

    @Override
    public String toString() {
        return "Request [operation=" + operation + ", values=" + values + "]";
    }

    public String getOperation() {
        return operation;
    }

    public Object getValues() {
        return values;
    }

}
