import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;

//__________559397 PACE ANTONIO__________
public class Server extends RemoteServer implements CongressoRMT{
    private static long serialVersionUID = 1L; // per versione compatibile serializzazione
    private static final int registryPort = 2020; // porta del servizio registry

    static Programma program[]; // array di Programmi, ogni posizione è una giornata

    public Server() throws RemoteException{ // costruttore
        super();
    }

    public int register(int date, String session, String speaker) throws RemoteException{
        int n=-1;

        if (session.equals("S1")) n=1;
        if (session.equals("S2")) n=2;
        if (session.equals("S3")) n=3;
        if (session.equals("S4")) n=4;
        if (session.equals("S5")) n=5;
        if (session.equals("S6")) n=6;
        if (session.equals("S7")) n=7;
        if (session.equals("S8")) n=8;
        if (session.equals("S9")) n=9;
        if (session.equals("S10")) n=10;
        if (session.equals("S11")) n=11;
        if (session.equals("S12")) n=12;

        if (date < 1 || date > 3) throw new RemoteException(); // giorno sbagliato
        if (n == -1) throw new RemoteException(); // sessione inesistente

        return program[date-1].registration(n-1,speaker);
    }
    public Programma getProgram(int date) throws RemoteException{
        System.out.println("Richiesto il programma della giornata " + date);
        return program[date-1];
    }
    public static void main(String[] args) throws RemoteException {
        program = new Programma[3]; // creo un array di programma per 3 giornate

        for (int i = 0; i < 3; i++) program[i]= new Programma();

        Server s = new Server(); // oggetto remoto

        CongressoRMT stub = (CongressoRMT) UnicastRemoteObject.exportObject(s,0); // si esporta l'oggetto remoto e si ottiene uno stub

        LocateRegistry.createRegistry(registryPort); // creo un registri sulla porta 2020 di localhost

        Registry r = LocateRegistry.getRegistry(registryPort); // riferimento al registry creato

        r.rebind("//localhost:2020/CONGRESSOSERVER", stub); // associazione nel registry di stub-nome

        System.out.println("Waiting for requests");

    }

}
