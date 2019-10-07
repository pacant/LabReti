import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
public class MainClass {
    public static void main(String [] args) throws InterruptedException {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(); // coda nella prima sala, illimitata
        //ReentrantLock l=new ReentrantLock();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Inserire capienza ufficio postale");
        int p = scanner.nextInt();
        System.out.println("Inserire capienza coda seconda sala");
        int k = scanner.nextInt();
        Ufficio ufficio = new Ufficio(queue, k);
        ufficio.entrata(p); //faccio entare le persone nella prima sala
        //queue.peek() lo prende dalla coda ma non lo elimina. Consigliato
        ufficio.executeAll(); //servo tutte le persone

        ufficio.closeOffice(); //chiudo l'ufficio

        System.out.println("Attenzione: l'ufficio sta chiudendo, serviamo le persone in coda.");

    }
}
