public class UserConnected extends User {
    private String status;

    public UserConnected(String username, String password, String status) {
        super(username, password);
        this.status = status;
    }

    public static UserConnected toConnect(User utente) {
        UserConnected utenteConnesso = new UserConnected(utente.getUsername(), utente.getPassword(), "offline");
        return utenteConnesso;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void toOnline() {
        this.status="online";
    }

    
    public void toOffline() {
        this.status="offline";
    }

}
