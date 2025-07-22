import java.util.HashMap;
import java.util.Map;

public class MenuContext {
    private ComandoStrategy strategy;

    public static final Map<String, ComandoStrategy> strategie = new HashMap<>();

    public MenuContext() {
        strategie.put("login", new Login());
        strategie.put("register", new Register());
    }

    public void eseguiComando(String c) {
        System.out.println(c);
        if (c.isEmpty()) {
            System.out.println("command line is empty");
            return;
        }

        String [] subc = c.split(" ");

        for(String x:subc)
            System.out.println(x);

        this.strategy = strategie.get(subc[0].toLowerCase());

        if (strategy != null) {
            strategy.esegui(subc);
        } else {
            System.out.println("Strategy is not setted");
        }
    }
}
