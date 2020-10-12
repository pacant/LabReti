import java.util.*;
import java.util.concurrent.locks.*;

public class MainClass {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Avvio del programma...");
        System.out.println("Inserire il numero di professori");
        int num_professori = scanner.nextInt();
        System.out.println("Inserire il numero di tesisti");
        int num_tesisti= scanner.nextInt();
        System.out.println("Inserire il numero di studenti");
        int num_studenti = scanner.nextInt();
        int num_utilizzatori=num_professori+num_tesisti+num_studenti;
        int j=0;

        final Lock lock=new ReentrantLock();
        final Condition occupato=lock.newCondition();
        final Lock lockprof=new ReentrantLock();
        final Condition waitprof=lockprof.newCondition();

        Thread [] arraythread=new Thread[num_utilizzatori]; //creo un array di thread

        for (int i = 0; i < Laboratorio.computer.length; i++) {
            Laboratorio.computer[i] = false;  //inizializzo l'array dei computer a false (computer tutti non occupati)
        }

        for(int i=0;i<Laboratorio.tesistiwaiting.length;i++){
            Laboratorio.tesistiwaiting[i]=0;
        }

        for(int i=0; i<num_professori; i++){
            Utente professore=new Utente(Ruolo.PROFESSORE,lock,occupato,lockprof,waitprof);
            Thread threadprof=new Thread(professore);
            arraythread[j]=threadprof;
            j++;
            threadprof.start();
        }

        for(int i=0; i<num_tesisti; i++){
            int indice =(int) (Math.random() * 20);
            Utente tesista=new Utente(Ruolo.TESISTA,lock,occupato,lockprof,waitprof,indice);
            Thread threadtesista=new Thread(tesista);
            arraythread[j]=threadtesista;
            j++;
            threadtesista.start();
        }

        for(int i=0; i<num_studenti; i++){
            Utente studente=new Utente(Ruolo.STUDENTE,lock,occupato,lockprof,waitprof);
            Thread threadstudente=new Thread(studente);
            arraythread[j]=threadstudente;
            j++;
            threadstudente.start();
        }
        for(int i= 0; i<num_utilizzatori; i++){
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
