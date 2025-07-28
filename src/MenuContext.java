import java.util.HashMap;
import java.util.Map;
import java.net.*;

public class MenuContext {

    private User utenteCorrente;

    private ComandoStrategy strategy;

    public static final Map<String, ComandoStrategy> strategie = new HashMap<>();

    public MenuContext() {
        strategie.put("login", new Login());
        strategie.put("register", new Register());
        strategie.put("logout", new Logout());
        strategie.put("updatecredentials", new updateCredentials());
        strategie.put("insertmarketorder", new insertMarketOrder());
        //strategie.put("", new updateCredentials());
        //strategie.put("", new updateCredentials());
        //strategie.put("", new updateCredentials());
    }

    public void eseguiComando(String c, Socket socket) {
        // System.out.println(c);
        if (c.isEmpty()) {
            System.out.println("command line is empty");
            return;
        }

        String[] subc = c.split(" ");

        this.strategy = strategie.get(subc[0].toLowerCase());

        if (strategy != null) {
            strategy.esegui(subc, socket);
            if(strategy.getUserCorrente() != null)
                utenteCorrente = strategy.getUserCorrente();
            if(strategy instanceof Logout){
                utenteCorrente = null;
            }
        } else {
            System.out.println("Comando non esistente");
        }
    }

    public User getUtenteCorrente() {
        return utenteCorrente;
    }

    public void setUtenteCorrente(User utenteCorrente) {
        this.utenteCorrente = utenteCorrente;
    }
}
