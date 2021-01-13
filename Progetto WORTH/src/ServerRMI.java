import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ServerRMI extends Remote {

    /* metodo per la registrazione di un utente a WORTH.
       Il valore di ritorno è un codice di successo/errore:
       - "Done" se la registrazione è andata a buon fine
       - "User already registered" o "Missing fields" in caso di errore
    */
    public String register(String nick,String pass) throws RemoteException;

    // registrazione alle callback del server, per le notifiche
    public Map<String,Boolean> registerCB(Notify client,String nick) throws RemoteException;

    public void unregisterCB(Notify client,String nick) throws RemoteException;
}
