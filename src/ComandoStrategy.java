import java.net.*;

public interface ComandoStrategy{
    void esegui(String [] parameters,Socket socket);
}
