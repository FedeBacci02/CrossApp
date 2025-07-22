public class Login implements ComandoStrategy {

    private String username;
    private String password;

    public void esegui(String [] parameters) {
        System.out.println("Login's command is executed  ..");

        //esecuzione del comando
        //parser dei parametri

        this.username = parameters[1];
        this.password = parameters[2];

        


    }
}
