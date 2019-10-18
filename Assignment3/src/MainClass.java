import java.util.*;
import java.util.concurrent.locks.*;

public class MainClass {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Inserire il numero di codici rossi");
        int red = scanner.nextInt();
        System.out.println("Inserire il numero di codici gialli");
        int yellow = scanner.nextInt();
        System.out.println("Inserire il numero di codici bianchi");
        int white = scanner.nextInt();
        int numpazienti=yellow+red+white;
        int j=0;

        final Lock lock=new ReentrantLock();
        final Condition occupato=lock.newCondition();
        final Lock lockred=new ReentrantLock();
        //final Lock readred= lockred.readLock();
        //final Lock writered= lockred.readLock();
        final Condition waitred=lockred.newCondition();

        Thread [] arraythread=new Thread[numpazienti]; //creo un array di thread

        for (int i = 0; i < Ambulatorio.medici.length; i++) {
            Ambulatorio.medici[i] = false;  //inizializzo l'array dei medici a false (medici tutti non occupati)
        }


        for (int i = 0; i < red; i++) {
            Paziente pazienter = new Paziente(Codice.ROSSO,lock,occupato,lockred,waitred);
            Thread threadr = new Thread(pazienter);
            arraythread[j]=threadr;
            j++;
            threadr.start();
        }
        for (int i = 0; i < yellow; i++) {
            int indice =(int) (Math.random() * 10)%10; //restituisce un numero da 0 a 9, indice per il codice giallo
            Paziente pazientey = new Paziente(Codice.GIALLO,lock,occupato,lockred,waitred, indice);
            Thread thready = new Thread(pazientey);
            arraythread[j]=thready;
            j++;
            thready.start();
        }
        for (int i = 0; i < white; i++) {
            Paziente pazientew = new Paziente(Codice.BIANCO,lock,occupato,lockred,waitred);
            Thread threadw = new Thread(pazientew);
            arraythread[j]=threadw;
            j++;
            threadw.start();
        }
        for(int i= 0; i<numpazienti; i++){
            try {
                arraythread[i].join();
            }
            catch(InterruptedException e){
                e.printStackTrace();
                return;
            }
        }
        System.out.println("Finito");
    }
}