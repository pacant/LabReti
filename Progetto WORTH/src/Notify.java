import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Notify extends Remote {

    // metodo per notificare il client sullo stato degli utenti
    public void notifyUser(String nick, Boolean status) throws RemoteException;

    // per avvisare il client di essere stato aggiunto ad un progetto (e quindi alla relativa chat)
    public void notifyProject(Chat chat) throws  RemoteException;

    //avviso tutti i client partecipanti ad un progetto che è stato cancellato, e che quindi la chat è chiusa
    public void notifyProjectCancel(Chat chat) throws RemoteException;
}
