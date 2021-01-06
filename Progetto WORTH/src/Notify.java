import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Notify extends Remote {

    // metodo per notificare il client sullo stato degli utenti
    public void notifyUser(String nick, boolean status) throws RemoteException;
}
