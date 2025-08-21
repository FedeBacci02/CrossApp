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

    public static boolean isMonth(String stringa) {
        try {
            int month = Integer.parseInt(stringa.substring(0, 2));
            int year = Integer.parseInt(stringa.substring(2, 6));
            if (month >= 1 && month <= 12 && year <= 2025 && year > 0)
                return true;
            return false;

        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

}
