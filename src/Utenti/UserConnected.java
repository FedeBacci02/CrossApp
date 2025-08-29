package Utenti;
import java.net.InetAddress;
import java.net.Socket;

public class UserConnected extends User {
    private String status;
    private InetAddress address;
    private int port;

    public UserConnected(String username, String password, String status) {
        super(username, password);
        this.status = status;

    }

    // Funzione per la conversione di un utente ad un utente da connettere
    public static UserConnected toConnect(User utente) {
        UserConnected utenteConnesso = new UserConnected(utente.getUsername(), utente.getPassword(), "offline");
        return utenteConnesso;
    }

    public String toString(){
        StringBuilder stringa = new StringBuilder();
        stringa.append("username= ").append(getUsername())
        .append(", password= ").append(getPassword())
        .append(", status= ").append(getStatus())
        .append(", port").append(port);
        return stringa.toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void toOnline(Socket socket,int port) {
        this.status = "online";
        this.address=socket.getInetAddress();
        this.port=port;
    }

    public void toOffline() {
        this.status = "offline";
        this.address=null;
        this.port=-1;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    
}
