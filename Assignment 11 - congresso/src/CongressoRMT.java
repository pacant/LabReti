import java.rmi.Remote;
import java.rmi.RemoteException;

//__________559397 PACE ANTONIO__________
public interface CongressoRMT extends Remote {

    // metodo per registrare uno speaker in una determinata sessione di un giorno
    int register(int date, String session, String speaker) throws RemoteException;

    // metodo per ottenere il programma di un giorno
    Programma getProgram(int date) throws RemoteException;
}
