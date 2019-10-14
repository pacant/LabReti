import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Paziente implements Runnable{
    private Codice codice;
    private Lock lock;
    private Condition occupato;
    private int indice;
    public Paziente(Codice codice,int indice){
        this.codice=codice;
        lock=new ReentrantLock();
        occupato=lock.newCondition();
        this.indice=indice;
    }
    @Override
    public void run() {
        try {
            long k = Math.round(Math.random() * 10);
            switch (codice) {
                case ROSSO:
                    break;
                case GIALLO:
                    for (int i = 0; i < k; i++) {
                        lock.lock();
                        while (Ambulatorio.medici[indice]) {
                            occupato.await();
                        }
                        Ambulatorio.medici[indice] = true;
                        lock.unlock();
                        Thread.sleep(Math.round(Math.random() * 10000));
                        lock.lock();
                        Ambulatorio.medici[indice]=false;
                        occupato.signalAll();
                        lock.unlock();
                    }
                    break;
                case BIANCO:
                    break;
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
            return;
            }
    }
}
