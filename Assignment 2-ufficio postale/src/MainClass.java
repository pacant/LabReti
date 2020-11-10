import java.util.*;
import java.util.concurrent.*;
public class MainClass{
    public static void main(String [] args) throws InterruptedException {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(); // coda nella prima sala, illimitata
        Scanner scanner = new Scanner(System.in);
        System.out.println("Inserire capienza ufficio postale");
        int p = scanner.nextInt();
        System.out.println("Inserire capienza coda seconda sala");
        int k = scanner.nextInt();
        scanner.close();
        Ufficio ufficio = new Ufficio(queue, k);
        ufficio.entrata(p); //faccio entare le persone nella prima sala

        ufficio.executeAll(); //servo tutte le persone

        ufficio.closeOffice(); //chiudo l'ufficio, comunque il pool finisce di elaborare i task

        System.out.println("Attenzione: l'ufficio sta chiudendo, serviamo le persone in coda.");

    }
}
