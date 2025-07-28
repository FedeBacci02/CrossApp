public class StringMethods {
    public static int IsInteger(String s) {
        try {
            Integer.parseInt(s);
            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }
}
