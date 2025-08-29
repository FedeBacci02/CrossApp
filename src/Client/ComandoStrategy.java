package Client;

import java.net.*;

import Utenti.User;

public interface ComandoStrategy{
    void esegui(String [] parameters,Socket socket);
    User getUserCorrente();
}
