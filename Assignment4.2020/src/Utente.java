import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Utente implements Runnable {
    private Ruolo ruolo;
    private int indice;

    public Utente(Ruolo ruolo) {
        this.ruolo = ruolo;
    }

    public Utente(Ruolo ruolo,int indice) { //costruttore per i tesisti che necessitano di un particolare computer
        this(ruolo);
        this.indice = indice;
    }

    boolean allfree() {
        if (Laboratorio.full) return false;
        for (int j = 0; j < Laboratorio.computer.length; j++) {
            if (Laboratorio.computer[j]) {
                return false;
            }
        }
        return true;
    }
    int onefree(){
        int j;
        for(j = 0;j < Laboratorio.computer.length;j++){
            if(!Laboratorio.computer[j]&&Laboratorio.tesistiwaiting[j]==0){
                break;
            }
        }
        if(j<Laboratorio.computer.length) return j;
        else return -1;
    }
    public void runprof(){

    }
    public void run() {
        try {
            int num;
            int k = (int) Math.round(Math.random() * 4) + 1;
            switch (ruolo) {
                case PROFESSORE:
                    for (int i = 0; i < k; i++) {
                        lockprof.lock();
                        Laboratorio.profwaiting++;
                        lockprof.unlock();
                        lock.lock();
                        while (!allfree()) occupato.await();
                        System.out.println("Professore assegnato ai computer");
                        Laboratorio.full = true;
                        lock.unlock();
                        Thread.sleep(Math.round(Math.random() * 5000)); //simulazione utilizzo
                        lock.lock();
                        Laboratorio.full = false;
                        lockprof.lock();
                        Laboratorio.profwaiting--;
                        waitprof.signalAll();
                        lockprof.unlock();
                        occupato.signalAll();
                        System.out.println("Professore uscito");
                        lock.unlock();
                        Thread.sleep(Math.round(Math.random() * 5000)); // aspetto prima di ripetere
                    }
                    break;
                case TESISTA:
                    lockprof.lock();
                    while (Laboratorio.profwaiting != 0) {
                        waitprof.await();
                    }
                    lockprof.unlock();
                    for (int i = 0; i < k; i++) {
                        lock.lock();
                        Laboratorio.tesistiwaiting[indice]++; // vedo quanti tesisti sono in coda per un computer
                        while (Laboratorio.full || Laboratorio.computer[indice]) {
                            // aspetto il computer "indice"
                            occupato.await();
                        }
                        System.out.println("Tesista assegnato al computer " + ++indice);
                        --indice;
                        Laboratorio.tesistiwaiting[indice]--;
                        Laboratorio.computer[indice] = true;
                        lock.unlock();
                        Thread.sleep(Math.round(Math.random() * 5000));
                        lock.lock();
                        Laboratorio.computer[indice] = false;
                        occupato.signalAll();
                        System.out.println("Tesista uscito");
                        lock.unlock();
                        Thread.sleep(Math.round(Math.random() * 5000));

                    }
                    break;
                case STUDENTE:
                    int j;
                    lockprof.lock();
                    while (Laboratorio.profwaiting != 0) {
                        waitprof.await();
                    }
                    lockprof.unlock();
                    for (int i = 0; i < k; i++) {
                        lock.lock();
                        while (((j = onefree()) == -1) || Laboratorio.full) {
                            System.out.println("Studente in attesa di un computer");
                            occupato.await();
                        }
                        System.out.println("Studente assegnato al computer " + ++j);
                        --j;
                        Laboratorio.computer[j] = true;
                        lock.unlock();
                        Thread.sleep(Math.round(Math.random() * 5000));
                        lock.lock();
                        Laboratorio.computer[j] = false;
                        occupato.signalAll();
                        System.out.println("Studente uscito");
                        lock.unlock();
                        Thread.sleep(Math.round(Math.random() * 5000));

                    }
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }
}
