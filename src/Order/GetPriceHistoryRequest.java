package Order;

public class GetPriceHistoryRequest {
    private String month;

    public GetPriceHistoryRequest(String month) {
        this.month = month;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
