import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Paziente implements Runnable{
    private Codice codice;
    private final Lock lock;
    private final Condition occupato;
    private final Lock lockred;
    private final Condition waitred;
    private int indice;
    public Paziente(Codice codice,Lock lock,Condition occupato,Lock lockred,Condition waitred){
        this.codice=codice;
        this.lock=lock;
        this.occupato=occupato;
        this.lockred=lockred;
        this.waitred=waitred;
    }
    public Paziente(Codice codice,Lock lock,Condition occupato,Lock lockred,Condition waitred,int indice){
        this(codice,lock,occupato,lockred,waitred);
        this.indice=indice;
    }
    boolean allfree(){
        if (Ambulatorio.full) return false;
        for(int j = 0;j < Ambulatorio.medici.length;j++){
            if(Ambulatorio.medici[j]){
                return false;
            }
        }
        return true;
    }
    int onefree(){
        int j;
        for(j = 0;j < Ambulatorio.medici.length;j++){
            if(!Ambulatorio.medici[j]){
                break;
            }
        }
        if(j<Ambulatorio.medici.length) return j;
        else return -1;
    }
    @Override
    public void run() {
        try {
            int num;
            int k = (int)Math.round(Math.random() * 4)+1;
            switch (codice) {
                case ROSSO:
                    for (int i = 0; i < k; i++) {
                        lockred.lock();
                        Ambulatorio.redwaiting++;
                        lockred.unlock();
                        lock.lock();
                        while(!allfree()) occupato.await();
                        System.out.println("Codice rosso assegnato ai medici");
                        Ambulatorio.full=true;
                        lock.unlock();
                        Thread.sleep(Math.round(Math.random() * 5000)); //simulazione visita
                        lock.lock();
                        Ambulatorio.full=false;
                        lockred.lock();
                        Ambulatorio.redwaiting--;
                        waitred.signalAll();
                        lockred.unlock();
                        occupato.signalAll();
                        System.out.println("Codice rosso dimesso");
                        lock.unlock();
                        Thread.sleep(Math.round(Math.random() * 5000)); // aspetto prima di ripetere
                    }
                    break;
                case GIALLO:
                        lockred.lock();
                        while(Ambulatorio.redwaiting!=0){
                          // System.out.println("Codice giallo in attesa, ci sono codici rossi in fila");
                            waitred.await();
                        }
                        lockred.unlock();
                    for (int i = 0; i < k; i++) {
                        lock.lock();

                        while (Ambulatorio.full||Ambulatorio.medici[indice]) {
                           //System.out.println("Codice giallo in attesa del medico " + ++indice);
                            //--indice;
                            occupato.await();
                        }
                        System.out.println("Codice giallo assegnato al medico " + ++indice);

                        --indice;
                        Ambulatorio.medici[indice] = true;
                        lock.unlock();
                        Thread.sleep(Math.round(Math.random() * 5000));
                        lock.lock();
                        Ambulatorio.medici[indice]=false;
                        occupato.signalAll();
                        System.out.println("Codice giallo dimesso");
                        lock.unlock();
                        Thread.sleep(Math.round(Math.random() * 5000));

                    }
                    break;
                case BIANCO:
                    int j;
                    lockred.lock();
                    while(Ambulatorio.redwaiting!=0){
                       // System.out.println("Codice bianco in attesa, ci sono codici rossi in fila");
                        waitred.await();
                    }
                    lockred.unlock();
                    for (int i = 0; i < k; i++) {
                        lock.lock();
                        while (((j=onefree())==-1)||Ambulatorio.full) {
                            System.out.println("Codice bianco in attesa di un medico");
                            occupato.await();
                        }
                        System.out.println("Codice bianco assegnato al medico " + ++j); //aggiustare indici
                        --j;
                        Ambulatorio.medici[j] = true;
                        lock.unlock();
                        Thread.sleep(Math.round(Math.random() * 5000));
                        lock.lock();
                        Ambulatorio.medici[j]=false;
                        occupato.signalAll();
                    System.out.println("Codice bianco dimesso");
                        lock.unlock();
                        Thread.sleep(Math.round(Math.random() * 5000));

                    }
                    break;
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
            return;
            }
    }
}
