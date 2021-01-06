import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

//__________559397 PACE ANTONIO__________
public class Client {
    private static final int registryPort= 2020; // porta del registry
    private static final String serviceName = "CONGRESSOSERVER";

    public static void main(String [] args){
        try{
            Registry r = LocateRegistry.getRegistry(registryPort); // riferimento al registry nel server (su localhost)

            // cerco lo stub nel registry ed effettuo un cast
            Remote remoteObj = r.lookup("//localhost:2020/CONGRESSOSERVER");
            CongressoRMT server = (CongressoRMT) remoteObj;

            // prevedo la registrazione di 200 speaker al congresso
            // i giorni vanno da 1 a 3 e le sessioni da 1 a 12, sono generate casualmente
            // la sessione n-esima è indicata con Sn
            for(int i = 0; i < 200; i++){
                int day= ((int)(Math.random()*3)+ 1);
                String session = "S" + ((int)(Math.random()*12)+ 1);
                String speaker = "Speaker " + (1+i);

                if(server.register(day,session,speaker)==1){
                    System.out.println("Registrazione di " + speaker + " non riuscita, la sessione " + session + " è piena");
                }
                else{
                    System.out.println("Registrazione di " + speaker + " effettuata: sessione " + session + " del giorno " + day );
                }
            }

            // stampa del programma della giornata
            for(int i = 0; i < 3; i++){
                Programma programma = server.getProgram(i+1);
                System.out.println();
                System.out.println("GIORNATA " +  (i+1) + ":\n");

                programma.printProgram();
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
