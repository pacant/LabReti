import java.util.*;
import java.util.concurrent.locks.*;

public class Consumer implements Runnable{
    private LinkedList<String> files;
    private Lock lock;
    private Condition notEmpty;
    public Consumer(LinkedList<String> files,Lock lock,Condition notEmpty){
        this.lock=lock;
        this.notEmpty=notEmpty;
        this.files=files;
    }
    public void run(){
        try {
            leggifile();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }
    public void leggifile() throws InterruptedException {
        try {
            while (!Thread.interrupted()) {
                lock.lock();
                while (!Thread.interrupted()&&files.isEmpty()) {
                    System.out.println("Waiting ");
                    notEmpty.await();
                }
                //TODO: non usare le interrupt per morire, usare una variabile condivisa accedibile tramite lock
                if (!Thread.interrupted())System.out.println("Il file si chiama " + files.poll());
                lock.unlock();
            }
        }
        catch(InterruptedException e){
            System.out.print("Interrotto " + Thread.currentThread());
            return;
        }
    }
}
