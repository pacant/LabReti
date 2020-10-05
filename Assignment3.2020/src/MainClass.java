import java.util.*;
import java.util.concurrent.locks.*;

public class MainClass {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Avvio del programma...");
        System.out.println("Inserire il numero di professori");
        int num_professori = scanner.nextInt();
        System.out.println("Inserire il numero di tirocinanti");
        int num_tirocinanti = scanner.nextInt();
        System.out.println("Inserire il numero di studenti");
        int num_studenti = scanner.nextInt();
        int num_utilizzatori=num_professori+num_tirocinanti+num_studenti;
        int j=0;

        final Lock lock=new ReentrantLock();
        final Condition occupato=lock.newCondition();
        final Lock lockprof=new ReentrantLock();
        final Condition waitprof=lockprof.newCondition();

        Thread [] arraythread=new Thread[num_utilizzatori]; //creo un array di thread

        for (int i = 0; i < Laboratorio.computer.length; i++) {
            Laboratorio.computer[i] = false;  //inizializzo l'array dei computer a false (computer tutti non occupati)
        }

        for(int i=0; i<num_professori; i++){
            //TODO: creo un thread per ogni professore e lo metto nell'array di thread
        }

        for(int i=0; i<num_tirocinanti; i++){
            //TODO:creo un thread per ogni tirocinante e lo metto nell'array di thread
        }

        for(int i=0; i<num_studenti; i++){
            //TODO:creo un thread per ogni studente e lo metto nell'array di thread
        }

        //TODO:qui faccio una join per ogni thread nell'array
    }
}
