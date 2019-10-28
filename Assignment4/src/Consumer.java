import java.io.File;
import java.util.*;
import java.util.concurrent.locks.*;

public class Consumer implements Runnable{
    private LinkedList<String> files;
    private Lock lock;
    private Lock lockup;
    private Condition notEmpty;
    public Consumer(LinkedList<String> files,Lock lock,Condition notEmpty,Lock lockup){
        this.lock=lock;
        this.notEmpty=notEmpty;
        this.files=files;
        this.lockup=lockup;
    }

    public void run(){
        lock.lock();
        while (Global.up||!files.isEmpty()) {
            while (Global.up&& files.isEmpty()) {  //variabile up nel while per controllarla all'uscita dalla wait
                try {
                   // System.out.println("waiting");
                    notEmpty.await(); //aspetto su notEmpty se la coda è vuota
                   // System.out.println("risvegliato ");
                    }
                catch (InterruptedException e) {
                        System.out.println("Interrotto");
                        e.printStackTrace();
                    }
                }
            File toprint;
            if (!files.isEmpty()){
                toprint = new File(files.poll()); //prendo il primo elemento della coda e lo stampo
                System.out.println(toprint.getName());
            }
            lock.unlock();
            lock.lock();
        }
        lock.unlock();
    }
}
